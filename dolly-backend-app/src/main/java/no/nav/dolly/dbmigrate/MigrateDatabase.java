package no.nav.dolly.dbmigrate;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.dbmigrate.mapper.BrukerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrateDatabase {

    private final BrukerService brukerService;

    @PostConstruct
    @Transactional
    public void init() {

        log.info("Migerer database starter");
        brukerService.migrate();
    }
}
