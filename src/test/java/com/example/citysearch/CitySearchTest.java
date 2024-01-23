package com.example.citysearch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Domingo Gomez
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class CitySearchTest {

    @Autowired
    private CitySearchService citySearchService;

    @Test
    public void find10Cities() throws Exception {

        List<String> cityHashes = Stream.of("Tokyo", "Arouca", "Terrabona", "Coello", "Kalibo", "Wislane", "Brindisi",
                "Isparta", "Tucker", "Malacacheta").map(name -> citySearchService.hashCityName(name)).toList();

        Instant before = Instant.now();
        for (String hash : cityHashes) {
            citySearchService.searchCityName(hash);
        }
        Instant after = Instant.now();
        Duration time = Duration.between(before, after);

        log.info("Processed 10 searches in {} nanos", time.toNanos());

    }
}
