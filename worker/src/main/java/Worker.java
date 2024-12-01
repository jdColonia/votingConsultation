import com.zeroc.Ice.Util;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public class Worker {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "worker.config")) {
            ObjectAdapter adapter = communicator.createObjectAdapter("WorkerAdapter");
            PublisherI publisher = new PublisherI();
            VotingServiceImpl votingService = new VotingServiceImpl(publisher);

            adapter.add(votingService, Util.stringToIdentity("VotingServiceWorker"));
            adapter.add(publisher, Util.stringToIdentity("Publisher"));

            adapter.activate();
            System.out.println("Worker started and waiting for requests...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}