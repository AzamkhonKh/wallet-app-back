package com.example.wallet.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import org.springframework.boot.autoconfigure.domain.EntityScan; // Import EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Import EnableJpaRepositories

// Scan f
// Scan components in dependent modules tool or components/services/repos in other modules' base packages
@SpringBootApplication(scanBasePackages = {
                "com.example.wallet.api",
                "com.example.wallet.core",
                "com.example.wallet.auth",
                "com.example.wallet.common"
})
// Tell Spring Data JPA where to find repositories in other modules
@EnableJpaRepositories(basePackages = {
                "com.example.wallet.auth.repository",
                "com.example.wallet.core.repository"
// Add other repository packages if necessary
})
// Tell JPA where to find entities in other modules
@EntityScan(basePackages = {
                "com.example.wallet.auth.domain",
                "com.example.wallet.core.domain"
// Add other entity packages if necessary
})
@ConfigurationPropertiesScan(basePackages = "com.example.wallet.api.config") // Example
public class WalletApplication {

        public static void main(String[] args) {
                SpringApplication.run(WalletApplication.class, args);
        }

}
