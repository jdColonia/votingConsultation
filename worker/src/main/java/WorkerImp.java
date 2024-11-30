import VotingConsultation.ManageTaskPrx;
import com.zeroc.Ice.Current;
import com.zeroc.IceInternal.ThreadPool;


public class WorkerImp implements VotingConsultation.Worker {
    private ThreadPool threadPool;
    ManageTaskPrx manageTaskPrx;
    String workerId;

    public WorkerImp() {
        this.threadPool = new ThreadPool();
    }

    public void connectionResponse(String msg, Current current) {
        System.out.println(msg);
    }

    public void setManageTaskPrx(ManageTaskPrx manageTaskPrx) {
        this.manageTaskPrx = manageTaskPrx;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
        System.out.println("Worker saved");
    }

    public void closeWorker(Current current) {
        System.out.println("Worker disconnected");
        System.exit(0);
    }

    public void getTask(String Function, double start, double end, int cantNum, int taskIndex, Current current) {
        new Thread(() -> {
            System.out.println("Task received: " + Function + " " + start + " " + end);
            try {
                long startTime = System.currentTimeMillis();
                double result = threadPool.execute(Function, start, end, cantNum);
                double partialResult = (end - start) * result / (cantNum * Runtime.getRuntime().availableProcessors());
                long time = System.currentTimeMillis() - startTime;
                this.manageTaskPrx.addPartialResult(partialResult, time, taskIndex, this.workerId);
                System.out.println("Task " + taskIndex + " solved, sending result to master");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
