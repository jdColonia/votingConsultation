import VotingConsultation.PublisherPrx;
import VotingConsultation.SubscriberPrx;
import VotingConsultation.VotingServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Master {

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

			// Registrar el Subscriber
			SubscriberI subscriber = new SubscriberI();
			ObjectAdapter subscriberAdapter = communicator.createObjectAdapter("Subscriber");
			SubscriberPrx subscriberPrx = SubscriberPrx
					.checkedCast(subscriberAdapter.add(subscriber, Util.stringToIdentity("Subscriber")));
			subscriberAdapter.activate();

			// Obtener el proxy remoto del Publisher
			PublisherPrx publisher;
			QueryPrx queryProxy = QueryPrx.checkedCast(communicator.stringToProxy("VotingConsultationIceGrid/Query"));
			publisher = PublisherPrx.checkedCast(queryProxy.findObjectByType("::VotingConsultation::Publisher"));

			System.out.println("Publisher: " + publisher);
			if (publisher == null) {
				System.err.println("Publisher not found");
				return;
			}
			// }
			publisher.addSubscriber(subscriber.getSubscriberId(), subscriberPrx);
			subscriber.setPublisherAvailable(publisher.isAvailable());

			// Obtener el proxy remoto del VotingServiceWorker
			VotingServicePrx votingServiceProxy = VotingServicePrx
					.checkedCast(queryProxy.findObjectByType("::VotingConsultation::VotingService"));

			if (votingServiceProxy == null) {
				throw new Error("No se pudo obtener el proxy remoto para VotingServiceWorker.");
			}

			// Inicializar la CLI
			ExecutionService executionService = new ExecutionService(votingServiceProxy, queryProxy,
					subscriber.getSubscriberId());
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