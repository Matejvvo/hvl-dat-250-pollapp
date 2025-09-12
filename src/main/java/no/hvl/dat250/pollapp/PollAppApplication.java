package no.hvl.dat250.pollapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PollAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(PollAppApplication.class, args);
        System.out.println("PollAppApplication started ...");
    }
}
