package com.example.citysearch;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Domingo Gomez
 */
@Service
@Slf4j
public class PhoneSearchService {
    private Map<String, String> treeMap;

    public void generatePhoneNumberCombinations(String prefix, int digits) throws InterruptedException {
        Instant before = Instant.now();
        treeMap = new TreeMap<>();
        int combinations = (int) Math.pow(10, digits);

        String format = "%s%0" + digits + "d";
        log.info("Using padding format of {}", format);

        AtomicInteger number = new AtomicInteger(0);

        int vthreads = combinations / 10000;
        for (int i = 0; i < vthreads; i++) {
            Thread virtualThread = Thread.ofVirtual().unstarted(() -> {
                        for (int j = 0; j < 10000; j++) {
                            var num = String.format(format, prefix, number.getAndAdd(1));
                            var hash = hashPhoneNumber(num);
                            treeMap.put(hash, num);
                        }
                        System.out.print(".");
                    }
            );

            virtualThread.start();
            virtualThread.join(Duration.of(5, ChronoUnit.MINUTES));
        }

        Instant after = Instant.now();
        Duration time = Duration.between(before, after);

        log.info("Loaded {} phone numbers in {} minutes", treeMap.keySet().size(), time.toMinutes());
    }

    public String hashPhoneNumber(String hashPhoneNumber) {
        return Hashing.sha256().hashString(hashPhoneNumber, StandardCharsets.UTF_8).toString();
    }

    public String searchPhoneNumber(String hash) {
        return treeMap.get(hash);
    }
}
