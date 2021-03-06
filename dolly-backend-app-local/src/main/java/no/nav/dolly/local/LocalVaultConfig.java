package no.nav.dolly.local;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.annotation.VaultPropertySource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@VaultPropertySource(value = "kv/preprod/fss/dolly-backend/local", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvfregdolly", propertyNamePrefix = "jira.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/test/srvdolly-backend", propertyNamePrefix = "credentials.test.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvdolly-preprod-env", propertyNamePrefix = "credentials.preprod.", ignoreSecretNotFound = false)
class LocalVaultConfig {
}