import java.net.InetAddress;
import java.net.UnknownHostException;

import com.zeroc.Ice.Current;

public class SubscriberI implements VotingConsultation.Subscriber {
	private String subscriberId;
	private boolean isPublisherAvailable;

	public SubscriberI() {
		try {
			// Obtener el nombre del host
			this.subscriberId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// Usar un ID alternativo si no se puede obtener el nombre del host
			this.subscriberId = "UnknownHost-" + System.currentTimeMillis();
		}
		// Inicialmente, asumir que el publicador no está disponible
		this.isPublisherAvailable = false;
	}

	@Override
	public void onUpdate(String state, Current current) {
		isPublisherAvailable = state.startsWith("AVAILABLE");
	}

	// Método para obtener el ID del suscriptor
	public String getSubscriberId() {
		return subscriberId;
	}

	// Método para verificar la disponibilidad del publicador
	public boolean isPublisherAvailable() {
		return isPublisherAvailable;
	}

	// Método para establecer la disponibilidad del publicador
	public void setPublisherAvailable(boolean isAvailable) {
		this.isPublisherAvailable = isAvailable;
	}
}