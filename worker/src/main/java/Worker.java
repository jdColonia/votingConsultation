import com.zeroc.Ice.Util;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public class Worker {

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapter("WorkerAdapter");
            VotingServiceImpl votingService = new VotingServiceImpl();
            adapter.add(votingService, Util.stringToIdentity("VotingServiceWorker"));
            adapter.activate();
            System.out.println("Worker started and waiting for requests...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
