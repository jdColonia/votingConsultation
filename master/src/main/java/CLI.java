import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private final ExecutionService executionService;
    private final Scanner scanner;

    public CLI(ExecutionService executionService) {
        this.scanner = new Scanner(System.in, "UTF-8");
        this.executionService = executionService;
    }

    public void start(Runnable onExit) {
        // Solicitar el número de hilos al inicio
        initializeThreadPool();

        // Menú principal
        while (true) {
            System.out.println("\n=== Sistema de Consulta de Puestos de Votación ===");
            System.out.println("1. Consultar votante individual");
            System.out.println("2. Consultar múltiples votantes desde archivo");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    consultSingleVoter();
                    break;
                case "2":
                    consultMultipleVoters();
                    break;
                case "3":
                    if (executionService != null) {
                        executionService.shutdown();
                    }
                    onExit.run();
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void initializeThreadPool() {
        if (executionService == null) {
            System.out.println("El servicio de ejecución no está configurado.");
            return;
        }

        while (true) {
            System.out.print("Ingrese el número de hilos para el pool: ");
            try {
                int threads = Integer.parseInt(scanner.nextLine());
                if (threads > 0) {
                    executionService.newFixedThreadPool(threads);
                    System.out.println("Pool de hilos configurado con " + threads + " hilos");
                    break;
                } else {
                    System.out.println("El número de hilos debe ser mayor que 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            }
        }
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
