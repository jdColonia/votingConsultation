import java.util.HashMap;
import com.zeroc.Ice.Current;
import VotingConsultation.SubscriberPrx;

public class PublisherI implements VotingConsultation.Publisher {

    private HashMap<String, SubscriberPrx> subscribers;

    public PublisherI() {
        subscribers = new HashMap<>();
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

    public void notifySubscribers(String[] voterIds) {
        for (SubscriberPrx subscriber : subscribers.values()) {
            subscriber.onUpdate(voterIds);
        }
    }
}