import com.zeroc.Ice.Current;
import VotingConsultation.Subscriber;

public class SubscriberI implements VotingConsultation.Subscriber {

    @Override
    public void onUpdate(String[] voterIds, Current current) {
        System.out.println("Received voter IDs for update:");
        for (String voterId : voterIds) {
            System.out.println(voterId);
        }
        // Lógica para procesar las consultas de votantes
    }
}