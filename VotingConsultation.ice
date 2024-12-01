module VotingConsultation {
    // Excepción para manejar cuando no se encuentra un votante
    exception VoterNotFoundException {
        string reason;
    };

    // Excepción para errores generales
    exception SystemException {
        string reason;
    };

    // Estructura para almacenar la respuesta de la consulta
    struct ConsultationResponse {
        string votingStation;    // Puesto de votación
        int primeFactorsCount; // Número de factores primos
        bool isPrime;            // Indicador si el número de factores primos es primo
        long responseTime;       // Tiempo de respuesta en milisegundos
    };

    // Declaración de la secuencia fuera de la interfaz
    sequence<ConsultationResponse> ConsultationResponseSeq;
    sequence<string> StringSeq;

    interface VotingService {
        // Método para consultar el puesto de votación de un votante
        ConsultationResponse getVotingStation(string subscriberId, string voterId) throws VoterNotFoundException, SystemException;

        // Método para consultar múltiples votantes
        ConsultationResponseSeq getMultipleVotingStations(string subscriberId, StringSeq voterIds) throws SystemException;
    };

    interface Subscriber {
        void onUpdate(string state);
    };

    interface Publisher {
        void addSubscriber(string subscriberId, Subscriber* subscriber);
        void removeSubscriber(string subscriberId);
        bool isAvailable();
    };
};