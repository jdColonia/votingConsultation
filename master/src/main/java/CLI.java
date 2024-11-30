import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private ExecutionService executionService; // Ahora es opcional
    private final Scanner scanner;

    public CLI() {
        this.scanner = new Scanner(System.in);
    }

    public void setExecutionService(ExecutionService executionService) {
        this.executionService = executionService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Sistema de Consulta de Puestos de Votación ===");
            System.out.println("1. Configurar número de hilos");
            System.out.println("2. Consultar votante individual");
            System.out.println("3. Consultar múltiples votantes desde archivo");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    configureThreads();
                    break;
                case "2":
                    consultSingleVoter();
                    break;
                case "3":
                    consultMultipleVoters();
                    break;
                case "4":
                    if (executionService != null) {
                        executionService.shutdown();
                    }
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void configureThreads() {
        if (executionService == null) {
            System.out.println("El servicio de ejecución no está configurado.");
            return;
        }

        System.out.print("Ingrese el número de hilos: ");
        try {
            int threads = Integer.parseInt(scanner.nextLine());
            defineNumberOfThreads(threads);
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un número válido");
        }
    }

    public void defineNumberOfThreads(int N) {
        if (executionService == null) {
            System.out.println("El servicio de ejecución no está configurado.");
            return;
        }

        if (N <= 0) {
            System.out.println("El número de hilos debe ser mayor que 0");
            return;
        }
        executionService.newFixedThreadPool(N);
        System.out.println("Pool de hilos configurado con " + N + " hilos");
    }

    private void consultSingleVoter() {
        if (executionService == null) {
            System.out.println("El servicio de ejecución no está configurado.");
            return;
        }

        System.out.print("Ingrese el número de cédula: ");
        String voterId = scanner.nextLine();
        executionService.execute(voterId);
    }

    private void consultMultipleVoters() {
        if (executionService == null) {
            System.out.println("El servicio de ejecución no está configurado.");
            return;
        }

        System.out.print("Ingrese la ruta del archivo: ");
        String filePath = scanner.nextLine();
        try {
            List<String> voterIds = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    voterIds.add(line.trim());
                }
            }
            executionService.executeMultiple(voterIds.toArray(new String[0]));
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo: " + e.getMessage());
        }
    }

}
