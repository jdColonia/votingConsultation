import VotingConsultation.PublisherPrx;
import VotingConsultation.SubscriberPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Master {

    private VotingServiceImpl votingService;
    private ObjectAdapter adapter;
    private Communicator communicator;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public void initialize(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try {
            communicator = Util.initialize(args, "master.config", extraArgs);

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

            // Registrar el Subscriber
            SubscriberI subscriber = new SubscriberI();
            ObjectAdapter subscriberAdapter = communicator.createObjectAdapter("Subscriber");
            SubscriberPrx subscriberPrx = SubscriberPrx.checkedCast(subscriberAdapter.add(subscriber, Util.stringToIdentity("Subscriber")));
            subscriberAdapter.activate();

            PublisherPrx publisher = PublisherPrx.checkedCast(communicator.propertyToProxy("Publisher.Proxy"));
            if (publisher != null) {
                publisher.addSubscriber("Master", subscriberPrx);
            } else {
                throw new Error("Invalid proxy for Publisher");
            }

            // Obtener el proxy del servicio
            VotingConsultation.VotingServicePrx votingServiceProxy =
                    VotingConsultation.VotingServicePrx.checkedCast(
                            adapter.createProxy(Util.stringToIdentity("VotingService"))
                    );

            if (votingServiceProxy == null) {
                throw new Error("No se pudo obtener el proxy para VotingService.");
            }

            // Inicializar la CLI
            ExecutionService executionService = new ExecutionService(votingServiceProxy);
            CLI cli = new CLI(executionService);

            // Iniciar CLI en un nuevo hilo
            new Thread(() -> cli.start(() -> {
                shutdown(); // Llama a shutdown al salir del CLI
                communicator.shutdown(); // Cierra el comunicador Ice
            })).start();

            communicator.waitForShutdown(); // Espera conexiones
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            if (adapter != null) {
                adapter.destroy();
            }
            if (votingService != null) {
                votingService.writeStatistics();
            }
            if (communicator != null) {
                communicator.destroy();
            }
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

}