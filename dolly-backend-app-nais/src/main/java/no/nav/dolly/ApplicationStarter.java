package no.nav.dolly;

import java.util.Map;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ApplicationStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        Map<String, Object> properties = PropertyReader.builder()
                .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                .readSecret("oracle.datasource.username", "/var/run/secrets/nais.io/db/username")
                .readSecret("oracle.datasource.password", "/var/run/secrets/nais.io/db/password")
                .readSecret("dolly.datasource.url", "/var/run/secrets/nais.io/dbPath/jdbc_url")
                .build();

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .properties(properties)
                .run(args);
    }
}