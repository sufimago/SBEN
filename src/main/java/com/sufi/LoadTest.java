package com.sufi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoadTest {
    // Configuración del cliente HTTP con tiempo de espera
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // URL objetivo con parámetros
    private static final String TARGET_URL = "http://localhost:8080/obtenerDisponibilidad?ciudad=Álava&fechaEntrada=2025-10-01&fechaSalida=2025-10-10&occupancy=1&cache=false";

    // Configuración del test
    private static final int REQUESTS = 100;
    private static final int THREADS = 500;

    // Contadores atómicos para estadísticas
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);
    private static final AtomicInteger totalResponses = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("Iniciando test de carga...");
        System.out.println("URL: " + TARGET_URL);
        System.out.println("Total requests: " + REQUESTS);
        System.out.println("Threads: " + THREADS + "\n");

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        long startTime = System.currentTimeMillis();

        // Crear todas las peticiones concurrentes
        List<CompletableFuture<Void>> futures = IntStream.range(0, REQUESTS)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        // Construir la petición HTTP con timeout
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(TARGET_URL))
                                .timeout(Duration.ofSeconds(15))
                                .GET()
                                .build();

                        // Enviar la petición y procesar la respuesta
                        HttpResponse<String> response = httpClient.send(
                                request, HttpResponse.BodyHandlers.ofString());

                        // Registrar estadísticas
                        totalResponses.incrementAndGet();

                        if (response.statusCode() == 200) {
                            successCount.incrementAndGet();
                            System.out.println("Request " + i + " - Success");
                        } else {
                            errorCount.incrementAndGet();
                            System.err.println("Request " + i + " failed with status: " + response.statusCode());
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        System.err.println("Error in request " + i + ": " + e.getClass().getName());
                        System.err.println("Error details: " + e.getMessage());
                    }
                }, executor))
                .collect(Collectors.toList());

        // Esperar a que todas las peticiones terminen
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long duration = System.currentTimeMillis() - startTime;

        // Mostrar resultados finales
        System.out.println("\n=== Test Results ===");
        System.out.println("Total time: " + duration + " ms");
        System.out.println("Requests per second: " + (REQUESTS / (duration / 300)));
        System.out.println("Successful requests: " + successCount.get());
        System.out.println("Failed requests: " + errorCount.get());
        System.out.println("Success rate: " +
                (successCount.get() * 100.0 / REQUESTS) + "%");
        System.out.println("Responses received: " + totalResponses.get());

        executor.shutdown();
    }
}