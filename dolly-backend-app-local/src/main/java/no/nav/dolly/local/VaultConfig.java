package no.nav.dolly.local;

import static java.lang.String.format;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
@VaultPropertySource(value = "kv/preprod/fss/dolly-backend/local", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvfregdolly", propertyNamePrefix = "jira.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/test/srvdolly-backend", propertyNamePrefix = "credentials.test.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvdolly-preprod-env", propertyNamePrefix = "credentials.preprod.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "oracle/dev/creds/dolly_t1-user", propertyNamePrefix = "oracle.datasource.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "oracle/dev/config/dolly_t1", propertyNamePrefix = "dolly.datasource.", ignoreSecretNotFound = false)
class VaultConfig implements InitializingBean {

    private final SecretLeaseContainer container;
    private final HikariDataSource hikariDataSource;
    private final VaultDatabaseProperties properties;

    @Override
    public void afterPropertiesSet() {
        var secret = RequestedSecret.rotating(properties.getBackend() + "/creds/" + properties.getRole());

        container.addLeaseListener(leaseEvent -> {
            log.info("Vault: Lease Event: {}", leaseEvent);
            if (leaseEvent.getSource() == secret && leaseEvent instanceof SecretLeaseCreatedEvent) {
                log.info("Vault: Refreshing database credentials. Lease Event: {}", leaseEvent);
                var lease = (SecretLeaseCreatedEvent) leaseEvent;
                var username = lease.getSecrets().get("username").toString();
                var password = lease.getSecrets().get("password").toString();

                hikariDataSource.setUsername(username);
                hikariDataSource.setPassword(password);
                hikariDataSource.getHikariConfigMXBean().setUsername(username);
                hikariDataSource.getHikariConfigMXBean().setPassword(password);
            }
        });

        container.addRequestedSecret(secret);
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfig() {
        return configuration -> configuration
                .initSql(format("SET ROLE \"%s\"", properties.getRole()));
    }
}