import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;

import VotingConsultation.ConsultationResponse;
import VotingConsultation.SystemException;
import VotingConsultation.VotingServicePrx;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ExecutionService {
	private ExecutorService executorService;
	private final String subscriberId;
	private final VotingServicePrx proxy;
	private final QueryPrx queryProxy;
	private final Logger logger;
	private long totalConsultations;
	private long totalExecutionTime;
	private static final int PARTITION_SIZE = 4870;

	public ExecutionService(VotingServicePrx proxy, QueryPrx queryProxy, String subscriberId) {
		this.subscriberId = subscriberId;
		this.proxy = proxy;
		this.queryProxy = queryProxy;
		this.totalConsultations = 0;
		this.totalExecutionTime = 0;
		this.logger = Logger.getLogger("ExecutionService");

		try {
			FileHandler fh = new FileHandler("ice_server.log", true);

			fh.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					// Solo guardar el mensaje del registro
					return record.getMessage() + "\n";
				}
			});

			logger.addHandler(fh);
			logger.setUseParentHandlers(false); // Evitar que los logs vayan a la consola
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newFixedThreadPool(int N) {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
		executorService = Executors.newFixedThreadPool(N);
	}

	public void execute(String voterId) {
		executorService.submit(() -> {
			try {
				ConsultationResponse response = proxy.getVotingStation(subscriberId, voterId);

				// Registrar en el log del master
				logConsultation(voterId, response.votingStation, response.primeFactorsCount, response.isPrime,
						response.responseTime);

				// Actualizar estadísticas
				updateStatistics(1, response.responseTime);

			} catch (Exception e) {
				// Delegar en el worker si el master está sobrecargado
				delegateToWorker(voterId);
			}
		});
	}

	private void delegateToWorker(String voterId) {
		try (Communicator communicator = Util.initialize()) {
			VotingConsultation.VotingServicePrx workerProxy = VotingConsultation.VotingServicePrx.checkedCast(
					communicator.stringToProxy("VotingServiceWorker@WorkerAdapter"));
			if (workerProxy != null) {
				VotingConsultation.ConsultationResponse response = workerProxy.getVotingStation(subscriberId, voterId);

				// Registrar en el log del master
				logConsultation(voterId, response.votingStation, response.primeFactorsCount, response.isPrime,
						response.responseTime);

				// Actualizar estadísticas
				updateStatistics(1, response.responseTime);

			} else {
				logger.severe("No se pudo obtener el proxy para VotingServiceWorker.");
			}
		} catch (Exception e) {
			logger.severe("Error delegando votante " + voterId + " a worker: " + e.getMessage());
		}
	}

	public void executeMultiple(String[] voterIds) {
		int totalPartitions = (int) Math.ceil((double) voterIds.length / PARTITION_SIZE);
		for (int i = 0; i < totalPartitions; i++) {
			int start = i * PARTITION_SIZE;
			int end = Math.min(start + PARTITION_SIZE, voterIds.length);
			String[] partition = Arrays.copyOfRange(voterIds, start, end);
			executorService.submit(() -> {
				VotingServicePrx votingServiceProxy = VotingServicePrx
						.checkedCast(queryProxy.findObjectByType("::VotingConsultation::VotingService"));
				delegatePartitionToWorker(partition, votingServiceProxy);
			});
		}
	}

	public void delegatePartitionToWorker(String[] voterIds, VotingServicePrx workerProxy) {
		try {
			if (workerProxy != null) {
				ConsultationResponse[] responses = workerProxy.getMultipleVotingStations(subscriberId, voterIds);

				long executionTime = 0;

				// Registrar consultas múltiples
				for (int i = 0; i < responses.length; i++) {
					logConsultation(voterIds[i], responses[i].votingStation, responses[i].primeFactorsCount,
							responses[i].isPrime, responses[i].responseTime);
					executionTime += responses[i].responseTime;
				}
				// Actualizar estadísticas para todas las consultas
				updateStatistics(responses.length, executionTime);
			} else {
				logger.severe("No se pudo obtener el proxy para VotingServiceWorker.");
			}
		} catch (SystemException e) {
			logger.severe("Error delegando partición a worker: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void logConsultation(String voterId, String votingStation, int primeFactorsCount, boolean isPrime,
								 long responseTime) {
		String logEntry = String.format("%s,%s,%d,%d,%d",
				voterId, votingStation, primeFactorsCount, isPrime ? 1 : 0, responseTime);
		logger.info(logEntry);
	}

	private void updateStatistics(long consultations, long executionTime) {
		totalConsultations += consultations;
		totalExecutionTime += executionTime;
	}

	public void writeStatistics() {
		String stats = String.format("Total consultations: %d, Total execution time: %d ms\n",
				totalConsultations,
				totalExecutionTime);
		logger.info(stats);
	}

	public void shutdown() {
		if (executorService != null) {
			writeStatistics();

			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
}