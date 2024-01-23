package com.example.citysearch;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Domingo Gomez
 */
@Service
@Slf4j
public class CitySearchService {
    private  Map<String, String> treeMap;

    public void loadFromCSV(String csvFilePath) {
        Instant before = Instant.now();
        treeMap = new TreeMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {
                String cityName = csvRecord.get(0).trim().toLowerCase();
                String hash = hashCityName(cityName);
                treeMap.put(hash, cityName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Instant after = Instant.now();
        Duration time = Duration.between(before, after);

        log.info("Loaded {} cities in {} millis", treeMap.keySet().size(), time.toMillis());
    }

    public String hashCityName(String cityName) {
        return Hashing.sha256().hashString(cityName, StandardCharsets.UTF_8).toString();
    }

    public String searchCityName(String hash) {
        return treeMap.get(hash);
    }
}
