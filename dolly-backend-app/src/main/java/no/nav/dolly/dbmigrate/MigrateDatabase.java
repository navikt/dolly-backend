package no.nav.dolly.dbmigrate;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.dbmigrate.mapper.MigrationService;
import no.nav.dolly.repository.postgres.BrukerRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrateDatabase {

    private final List<MigrationService> migrationServices;
    private final BrukerRepository brukerRepository;

    @PostConstruct
    public void init() {

        if (!brukerRepository.findAllByOrderById().isEmpty()) {
            log.info("Database er migrert");

        } else {
            log.info("Migrering av database starter");

            migrationServices.forEach(MigrationService::migrate);

            log.info(("Migrering ferdig"));
        }
    }
}
