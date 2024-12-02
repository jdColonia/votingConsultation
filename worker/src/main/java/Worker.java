import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;

public class Worker {
	public static void main(String[] args) {
		List<String> extraArgs = new ArrayList<>();
		try (Communicator communicator = Util.initialize(args, extraArgs)) {
			// Obtener propiedades específicas del worker desde los argumentos extra
			String workerIndex = null;
			for (String arg : extraArgs) {
				if (arg.startsWith("--index=")) {
					workerIndex = arg.substring(arg.indexOf('=') + 1);
					break;
				}
			}

			// Usar el índice para configurar propiedades específicas si es necesario
			if (workerIndex != null) {
				// Configuraciones específicas del worker con índice
				String votingServiceProxyName = "VotingServiceWorker-" + workerIndex;
				String publisherProxyName = "Publisher-" + workerIndex;

				// Crear las identidades correspondientes
				Identity votingServiceIdentity = Util.stringToIdentity(votingServiceProxyName);
				Identity publisherIdentity = Util.stringToIdentity(publisherProxyName);

				ObjectAdapter adapter = communicator.createObjectAdapter("WorkerAdapter");

				PublisherI publisher = new PublisherI();
				VotingServiceImpl votingService = new VotingServiceImpl(publisher);

				// Añadir los objetos al adaptador con sus identidades
				adapter.add(publisher, publisherIdentity);
				adapter.add(votingService, votingServiceIdentity);

				adapter.activate();

				System.out.println("Worker started and waiting for requests...");
				communicator.waitForShutdown();
			} else {
				System.out.println("No worker index provided, exiting...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}