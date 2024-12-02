import VotingConsultation.ConsultationResponse;
import VotingConsultation.SystemException;
import VotingConsultation.VoterNotFoundException;
import com.zeroc.Ice.Current;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class VotingServiceImpl implements VotingConsultation.VotingService {

	private final Consultation consultation;
	private final Logger logger;
	private final PublisherI publisher;

	public VotingServiceImpl(PublisherI publisher) {
		this.publisher = publisher;
		this.consultation = new Consultation();
		this.logger = Logger.getLogger("VotingService");

		try {
			FileHandler fh = new FileHandler("voter.list.log", true);

			// Crear un formato personalizado para el logger
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

	@Override
	public ConsultationResponse getVotingStation(String subscriberId, String voterId, Current current)
			throws SystemException {
		// Notificar que el servidor está ocupado al comenzar
		publisher.notifySubscribers("BUSY");

		long startTime = System.currentTimeMillis();
		try {
			String votingStation = consultation.consultVotingTableSingle(voterId);
			int primeFactorsCount = countPrimeFactors(Integer.parseInt(voterId));
			boolean isPrime = isPrime(primeFactorsCount);
			long responseTime = System.currentTimeMillis() - startTime;

			// Registrar en el log
			logConsultation(subscriberId, voterId, votingStation, primeFactorsCount, isPrime, responseTime);

			// Notificar que el servidor está disponible al terminar
			publisher.notifySubscribers("AVAILABLE");

			return new ConsultationResponse(votingStation, primeFactorsCount, isPrime, responseTime);
		} catch (Exception e) {
			// Notificar que el servidor está disponible en caso de error
			publisher.notifySubscribers("AVAILABLE");
			throw new SystemException(e.getMessage());
		}
	}

	@Override
	public ConsultationResponse[] getMultipleVotingStations(String subscriberId, String[] voterIds, Current current)
			throws SystemException {
		// Notificar que el servidor está ocupado al comenzar
		publisher.notifySubscribers("BUSY");

		List<ConsultationResponse> responses = new ArrayList<>();
		try {
			// Abrir la conexión al iniciar todas las consultas
			consultation.openConnection();

			for (String voterId : voterIds) {
				try {
					long startTime = System.currentTimeMillis();

					String votingStation = consultation.consultVotingTable(voterId);
					int primeFactorsCount = countPrimeFactors(Integer.parseInt(voterId));
					boolean isPrime = isPrime(primeFactorsCount);
					long responseTime = System.currentTimeMillis() - startTime;

					// Registrar en el log
					logConsultation(subscriberId, voterId, votingStation, primeFactorsCount, isPrime, responseTime);
					responses.add(new ConsultationResponse(votingStation, primeFactorsCount, isPrime, responseTime));
				} catch (SQLException e) {
					logger.warning("Votante no encontrado: " + voterId);
				}
			}
		} catch (Exception e) {
			// Notificar que el servidor está disponible en caso de error
			publisher.notifySubscribers("AVAILABLE");
			throw new SystemException(e.getMessage());
		} finally {
			try {
				// Cerrar la conexión al finalizar todas las consultas
				consultation.closeConnection();
			} catch (SQLException e) {
				logger.severe("Error al cerrar la conexión a la base de datos: " + e.getMessage());
			}
		}

		// Notificar que el servidor está disponible al terminar
		publisher.notifySubscribers("AVAILABLE");

		return responses.toArray(new ConsultationResponse[0]);
	}

	private int countPrimeFactors(int n) {
		int count = 0;
		// Eliminar los factores de 2
		while (n % 2 == 0) {
			count++;
			n /= 2;
		}
		// Eliminar los factores impares
		for (int i = 3; i <= Math.sqrt(n); i += 2) {
			while (n % i == 0) {
				count++;
				n /= i;
			}
		}
		// Si n es mayor que 2, entonces es un número primo
		if (n > 2) {
			count++;
		}
		return count;
	}

	private static boolean isPrime(int n) {
		if (n <= 1)
			return false; // Números <= 1 no son primos
		if (n == 2)
			return true; // 2 es primo
		if (n % 2 == 0)
			return false; // Números pares > 2 no son primos

		// Comprobar solo los impares desde 3 hasta √n
		for (int i = 3; i <= Math.sqrt(n); i += 2) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	private void logConsultation(String subscriberId, String voterId, String votingStation, int primeFactorsCount,
								 boolean isPrime,
								 long responseTime) {
		// Crear la entrada de log con el número de factores primos y el indicador de si
		// es primo o no
		String logEntry = String.format("%s,%s,%s,%d,%d,%d",
				subscriberId, voterId, votingStation, primeFactorsCount, isPrime ? 1 : 0, responseTime);

		// Guardar en el archivo de log
		logger.info(logEntry);
	}

}
