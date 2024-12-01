import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

public class ExecutionService {

    private ExecutorService executorService;
    private final String subscriberId;
    private final VotingConsultation.VotingServicePrx proxy;
    private final Logger logger = Logger.getLogger(ExecutionService.class.getName());

    public ExecutionService(VotingConsultation.VotingServicePrx proxy, String subscriberId) {
        this.subscriberId = subscriberId;
        this.proxy = proxy;
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
                VotingConsultation.ConsultationResponse response = proxy.getVotingStation(subscriberId, voterId);
                logger.info(String.format("Consulta para votante %s completada. Puesto: %s, Tiempo: %d ms",
                        voterId, response.votingStation, response.responseTime));
            } catch (Exception e) {
                // Delegate to worker if master is overloaded
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
                logger.info(String.format(
                        "Consulta delegada para votante %s completada por worker. Puesto: %s, Tiempo: %d ms",
                        voterId, response.votingStation, response.responseTime));
            } else {
                logger.severe("No se pudo obtener el proxy para VotingServiceWorker.");
            }
        } catch (Exception e) {
            logger.severe("Error delegando votante " + voterId + " a worker: " + e.getMessage());
        }
    }

    public void executeMultiple(String[] voterIds) {
        executorService.submit(() -> {
            try {
                VotingConsultation.ConsultationResponse[] responses = proxy.getMultipleVotingStations(subscriberId,
                        voterIds);
                for (int i = 0; i < responses.length; i++) {
                    logger.info(String.format("Consulta para votante %s completada. Puesto: %s, Tiempo: %d ms",
                            voterIds[i], responses[i].votingStation, responses[i].responseTime));
                }
            } catch (Exception e) {
                logger.severe("Error en consulta múltiple: " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        if (executorService != null) {
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