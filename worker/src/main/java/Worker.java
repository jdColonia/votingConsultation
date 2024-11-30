import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import com.zeroc.Ice.Util;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;

import VotingConsultation.ManageTaskPrx;
import VotingConsultation.WorkerPrx;

public class Worker {
    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = Util.initialize(args, "worker.config", extraArgs)) {
            ManageTaskPrx service = ManageTaskPrx.checkedCast(communicator.propertyToProxy("Master.Proxy"));

            if (service == null) {
                throw new Error("Invalid proxy");
            }

            String user = System.getProperty("user.name");
            String hostname = "";

            try {
                hostname = execReadToString("hostname").trim();
            } catch (IOException e) {
                hostname = "null";
            }

            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Callback", "tcp -p " + args[0] + " -h " + args[1]);
            WorkerImp workerImp = new WorkerImp();
            ObjectPrx obprx = adapter.add(workerImp, Util.stringToIdentity("Worker"));
            adapter.activate();

            WorkerPrx prx = WorkerPrx.uncheckedCast(obprx);
            String workerId = user + ":" + hostname + ":" + args[0];
            ManageTaskPrx ManageTaskPrx = service.connectWorker(workerId, prx);
            workerImp.setWorkerId(workerId);
            workerImp.setManageTaskPrx(ManageTaskPrx);
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Disconnecting worker from master");
                ManageTaskPrx.disconnectWorker(workerId);
            }));

            communicator.waitForShutdown();
        }
    }

    public static String execReadToString(String execCommand) throws IOException {
        Process proc = Runtime.getRuntime().exec(execCommand);
        try (InputStream stream = proc.getInputStream()) {
            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        }
    }
}
