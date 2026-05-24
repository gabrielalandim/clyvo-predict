package br.com.fiap.clyvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ClyvoPredictApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClyvoPredictApplication.class, args);
    }
}
