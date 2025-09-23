package no.hvl.dat250.pollapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "no.hvl.dat250.pollapp.repository.jpa")
public class JPAConfig {
}
