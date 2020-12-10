package no.nav.dolly.bestilling.organisasjonforvalter.mapper;

import ma.glasnost.orika.MapperFactory;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonRequest;
import no.nav.dolly.domain.resultset.organisasjon.RsOrganisasjoner;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class OrganisasjonerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsOrganisasjoner.class, OrganisasjonRequest.class)
                .byDefault()
                .register();
    }
}
