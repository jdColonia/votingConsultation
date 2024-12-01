import java.net.InetAddress;
import java.net.UnknownHostException;

import com.zeroc.Ice.Current;

public class SubscriberI implements VotingConsultation.Subscriber {
    private String subscriberId;

    public SubscriberI() {
        try {
            // Obtener el nombre del host
            this.subscriberId = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Usar un ID alternativo si no se puede obtener el nombre del host
            this.subscriberId = "UnknownHost-" + System.currentTimeMillis();
        }
    }

    @Override
    public void onUpdate(String[] voterIds, Current current) {
        System.out.println("Received update for Subscriber: " + subscriberId);
        System.out.println("Received voter IDs for update:");
        for (String voterId : voterIds) {
            System.out.println(voterId);
        }
    }

    // Método para obtener el ID del suscriptor
    public String getSubscriberId() {
        return subscriberId;
    }
}