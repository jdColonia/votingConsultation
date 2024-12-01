import java.util.HashMap;
import com.zeroc.Ice.Current;
import VotingConsultation.SubscriberPrx;

public class PublisherI implements VotingConsultation.Publisher {

    private HashMap<String, SubscriberPrx> subscribers;

    public PublisherI() {
        subscribers = new HashMap<>();
    }

    @Override
    public void addSubscriber(String name, SubscriberPrx subscriber, Current current) {
        System.out.println("New Subscriber: " + name);
        subscribers.put(name, subscriber);
    }

    @Override
    public void removeSubscriber(String name, Current current) {
        subscribers.remove(name);
        System.out.println("Subscriber has been removed: " + name);
    }

    public void notifySubscribers(String[] voterIds) {
        for (SubscriberPrx subscriber : subscribers.values()) {
            subscriber.onUpdate(voterIds);
        }
    }
}