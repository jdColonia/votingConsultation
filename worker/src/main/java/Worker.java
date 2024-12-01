import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public class Worker {
    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, "worker.config", extraArgs)) {
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
                String votingServiceProxy = communicator.getProperties()
                        .getProperty("VotingServiceWorker-" + workerIndex + ".Proxy");
                String publisherProxy = communicator.getProperties().getProperty("Publisher-" + workerIndex + ".Proxy");

                // Usar estas propiedades como necesites
                System.out.println("VotingService Proxy: " + votingServiceProxy);
                System.out.println("Publisher Proxy: " + publisherProxy);
            }

            ObjectAdapter adapter = communicator.createObjectAdapter("WorkerAdapter");

            // Resto de tu código como antes...
            PublisherI publisher = new PublisherI();
            VotingServiceImpl votingService = new VotingServiceImpl(publisher);

            adapter.add(votingService, Util.stringToIdentity("VotingServiceWorker-" + workerIndex));
            adapter.add(publisher, Util.stringToIdentity("Publisher-" + workerIndex));

            adapter.activate();
            System.out.println("Worker started and waiting for requests...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}