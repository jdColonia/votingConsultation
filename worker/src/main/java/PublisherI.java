import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.zeroc.Ice.Current;
import VotingConsultation.SubscriberPrx;

public class PublisherI implements VotingConsultation.Publisher {
    private HashMap<String, SubscriberPrx> subscribers;
    private String publisherId;
    // Usar AtomicBoolean para garantizar sincronización y visibilidad entre hilos
    private AtomicBoolean isAvailable;

    public PublisherI() {
        subscribers = new HashMap<>();
        // Inicializar como disponible al inicio
        isAvailable = new AtomicBoolean(true);

        try {
            // Generar ID usando "worker-" + nombre del host + timestamp
            String hostName = InetAddress.getLocalHost().getHostName();
            this.publisherId = "worker-" + hostName + "-" + System.currentTimeMillis();
        } catch (UnknownHostException e) {
            // Usar un ID alternativo si no se puede obtener el nombre del host
            this.publisherId = "worker-unknown-" + System.currentTimeMillis();
        }
        System.out.println("Publisher ID: " + publisherId);
    }

    @Override
    public void addSubscriber(String subscriberId, SubscriberPrx subscriber, Current current) {
        System.out.println("New Subscriber: " + subscriberId);
        subscribers.put(subscriberId, subscriber);
    }

    @Override
    public void removeSubscriber(String subscriberId, Current current) {
        subscribers.remove(subscriberId);
        System.out.println("Subscriber has been removed: " + subscriberId);
    }

    public void notifySubscribers(String state) {
        // Actualizar el estado de disponibilidad
        if ("AVAILABLE".equals(state)) {
            isAvailable.set(true);
        } else if ("BUSY".equals(state)) {
            isAvailable.set(false);
        }

        for (SubscriberPrx subscriber : subscribers.values()) {
            // Enviar el estado junto con el ID del publicador
            subscriber.onUpdate(state + ":" + publisherId);
        }
    }

    // Método para obtener el ID del publicador si es necesario
    public String getPublisherId() {
        return publisherId;
    }

    @Override
    // Método para verificar la disponibilidad del publicador
    public boolean isAvailable(Current current) {
        return isAvailable.get();
    }
}