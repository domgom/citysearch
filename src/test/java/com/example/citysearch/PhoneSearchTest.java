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
public class PhoneSearchTest {

    @Autowired
    private PhoneSearchService phoneSearchService;


    @Test
    public void findXmillionPhoneNumbers() throws Exception {
        // Requires a lot of memory, use jvm args -Xmx=24G or more
        // Can be launched from cmd with
        // export MAVEN_OPTS="-Xmx24G -XshowSettings:vm"
        // mvn test -Dtest=PhoneSearchTest
        phoneSearchService.generatePhoneNumberCombinations("6", 8);

        List<String> phoneNumberHashes = Stream.of("600000000", "610000000", "620000000", "630000000", "640000000",
                        "650000000", "660000000", "670000000", "680000000", "690000000")
                .map(name -> phoneSearchService.hashPhoneNumber(name)).toList();

        Instant before = Instant.now();
        for (String hash : phoneNumberHashes) {
            String found = phoneSearchService.searchPhoneNumber(hash);
            if(found == null){
                log.info("Phone not found with hash {}", hash);
            }else{
                log.info("Phone {} found with hash {}", found, hash);
            }
        }
        Instant after = Instant.now();
        Duration time = Duration.between(before, after);

        log.info("Processed 10 searches in {} nanos", time.toNanos());

    }
}
