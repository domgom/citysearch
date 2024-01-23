package com.example.citysearch;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
@RequiredArgsConstructor
public class CitySearchApplication implements CommandLineRunner {

    public static final String CITIES_CSV_PATH = "CITIES_CSV_PATH";
    @Autowired
    private final CitySearchService citySearchService;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(CitySearchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String csvEnvVar = System.getenv(CITIES_CSV_PATH);

        if (csvEnvVar == null) {
            System.out.println("Usage: java -jar yourjar.jar. You must declare $CITIES_CSV_PATH env variable.");
            System.exit(1);
        }

        citySearchService.loadFromCSV(System.getenv(CITIES_CSV_PATH));

        if(environment.matchesProfiles("test")){
            return;
        }
        // Start a background thread to continuously read input from the console
        Thread consoleInputThread = new Thread(() -> {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    System.out.print("Enter a new city name (or 'exit' to quit): ");
                    String cityName = consoleReader.readLine().toLowerCase();

                    if (cityName.equalsIgnoreCase("exit")) {
                        break;
                    }

                    String hash = citySearchService.hashCityName(cityName);
                    String result = citySearchService.searchCityName(hash);
                    if (result == null) {
                        System.out.println("City not found: " + cityName);
                    } else {
                        System.out.println("City found: " + cityName + " with hash: " + hash);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Set the thread as a daemon so that it doesn't prevent the application from exiting
        consoleInputThread.setDaemon(true);

        // Start the console input thread
        consoleInputThread.start();

        // Keep the main thread running
        consoleInputThread.join();
    }
}
