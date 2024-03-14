package org.example.cookercorner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.cookercorner.repository")

public class CookerCornerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookerCornerApplication.class, args);
    }

}
