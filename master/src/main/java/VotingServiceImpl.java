import VotingConsultation.ConsultationResponse;
import VotingConsultation.SystemException;
import VotingConsultation.VoterNotFoundException;
import com.zeroc.Ice.Current;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class VotingServiceImpl implements VotingConsultation.VotingService {

    private final Consultation consultation;
    private final Logger logger;
    private long totalConsultations;
    private long totalExecutionTime;

    public VotingServiceImpl() {
        this.consultation = new Consultation();
        this.logger = Logger.getLogger("VotingService");
        this.totalConsultations = 0;
        this.totalExecutionTime = 0;

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
    public ConsultationResponse getVotingStation(String voterId, Current current)
            throws VoterNotFoundException, SystemException {
        long startTime = System.currentTimeMillis();
        try {
            String votingStation = consultation.consultVotingTable(voterId);
            boolean isPrime = isPrimeFactorsCountPrime(voterId);
            long responseTime = System.currentTimeMillis() - startTime;

            // Registrar en el log
            logConsultation(current.id.name, voterId, votingStation, isPrime, responseTime);

            return new ConsultationResponse(votingStation, isPrime, responseTime);
        } catch (Exception e) {
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public ConsultationResponse[] getMultipleVotingStations(String[] voterIds, Current current)
            throws SystemException {
        List<ConsultationResponse> responses = new ArrayList<>();
        for (String voterId : voterIds) {
            try {
                responses.add(getVotingStation(voterId, current));
            } catch (VoterNotFoundException e) {
                // Log the error and continue with next voter
                logger.warning("Voter not found: " + voterId);
            }
        }
        return responses.toArray(new ConsultationResponse[0]);
    }

    private boolean isPrimeFactorsCountPrime(String number) {
        int n = Integer.parseInt(number);
        int factorsCount = countPrimeFactors(n);
        return isPrime(factorsCount);
    }

    private int countPrimeFactors(int n) {
        int count = 0;
        for (int i = 2; i <= n; i++) {
            while (n % i == 0) {
                count++;
                n /= i;
            }
        }
        return count;
    }

    private boolean isPrime(int n) {
        if (n <= 1)
            return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    private void logConsultation(String clientId, String voterId, String votingStation,
            boolean isPrime, long responseTime) {
        // Crear la entrada de log solo con los datos solicitados
        String logEntry = String.format("%s,%s,%d,%d",
                voterId, votingStation, isPrime ? 1 : 0, responseTime);

        // Guardar en el archivo de log
        logger.info(logEntry);

        synchronized (this) {
            totalConsultations++;
            totalExecutionTime += responseTime;
        }
    }

    public synchronized void writeStatistics() {
        String stats = String.format("Total consultations: %d, Total execution time: %d ms\n",
                totalConsultations, totalExecutionTime);
        logger.info(stats);
    }

}