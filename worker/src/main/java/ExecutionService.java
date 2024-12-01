import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExecutionService {

    private ExecutorService executorService;
    private final VotingConsultation.VotingServicePrx proxy;
    private final Logger logger = Logger.getLogger(ExecutionService.class.getName());

    public ExecutionService(VotingConsultation.VotingServicePrx proxy) {
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
                VotingConsultation.ConsultationResponse response = proxy.getVotingStation(voterId);
                logger.info(String.format("Consulta para votante %s completada. Puesto: %s, Tiempo: %d ms",
                        voterId, response.votingStation, response.responseTime));
            } catch (Exception e) {
                logger.severe("Error consultando votante " + voterId + ": " + e.getMessage());
            }
        });
    }

    public void executeMultiple(String[] voterIds) {
        executorService.submit(() -> {
            try {
                VotingConsultation.ConsultationResponse[] responses = proxy.getMultipleVotingStations(voterIds);
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
