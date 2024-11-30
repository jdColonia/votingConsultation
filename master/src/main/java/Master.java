import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;

public class Master {

    private VotingServiceImpl votingService;
    private ObjectAdapter adapter;

    public void initialize(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = Util.initialize(args, "master.config", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("Too many arguments provided. Extra arguments:");
                extraArgs.forEach(System.out::println);
                return;
            }

            // Crear e inicializar el servicio de votación
            votingService = new VotingServiceImpl();

            // Crear el adaptador
            adapter = communicator.createObjectAdapter("VotingService");

            // Activar el objeto y añadirlo al adaptador
            adapter.add(votingService, Util.stringToIdentity("VotingService"));

            // Activar el adaptador
            adapter.activate();

            System.out.println("Servidor iniciado y esperando conexiones...");

            // Obtener el proxy del servicio
            VotingConsultation.VotingServicePrx votingServiceProxy =
                    VotingConsultation.VotingServicePrx.checkedCast(
                            adapter.createProxy(Util.stringToIdentity("VotingService"))
                    );

            if (votingServiceProxy == null) {
                throw new Error("No se pudo obtener el proxy para VotingService.");
            }

            // Inicializar la CLI
            CLI cli = new CLI();
            ExecutionService executionService = new ExecutionService(votingServiceProxy);
            cli.setExecutionService(executionService);

            // Iniciar la CLI en un nuevo hilo
            new Thread(cli::start).start();

            // Esperar a que el comunicador se cierre
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (votingService != null) {
                votingService.writeStatistics();
            }
        }
    }

    public void shutdown() {
        if (adapter != null) {
            adapter.destroy();
        }
        if (votingService != null) {
            votingService.writeStatistics();
        }
    }

    public static void main(String[] args) {
        Master master = new Master();

        // Registrar un shutdown hook para asegurar un cierre limpio
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando el servidor...");
            master.shutdown();
        }));

        // Inicializar el servidor
        master.initialize(args);
    }

    public void returnResponse() {
        if (votingService != null) {
            votingService.writeStatistics();
        }
    }

}
