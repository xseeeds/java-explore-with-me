package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "ru.practicum",
                "ru.defaultComponent"
        },
        scanBasePackageClasses = {
                ru.client.StatisticClient.class
        })
public class ExploreWithMe {

    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMe.class, args);
    }

}
