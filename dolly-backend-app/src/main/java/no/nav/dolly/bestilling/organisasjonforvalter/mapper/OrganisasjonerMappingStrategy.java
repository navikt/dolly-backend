package no.nav.dolly.bestilling.organisasjonforvalter.mapper;

import ma.glasnost.orika.MapperFactory;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonBestillingRequest;
import no.nav.dolly.domain.resultset.organisasjon.RsSyntetiskeOrganisasjoner;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class OrganisasjonerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsSyntetiskeOrganisasjoner.class, OrganisasjonBestillingRequest.class)
                .byDefault()
                .register();
    }
}
