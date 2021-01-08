package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganisasjonBestillingStatusMappingStrategy implements MappingStrategy {

    private final JsonBestillingMapper jsonBestillingMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(OrganisasjonBestilling.class, RsOrganisasjonBestillingStatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonBestilling bestilling, RsOrganisasjonBestillingStatus bestillingStatus, MappingContext context) {

                        RsOrganisasjonBestilling bestillingRequest = jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier());
                        bestillingStatus.setAntallLevert(bestilling.getProgresser().size());
                        bestillingStatus.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        bestillingStatus.setBestilling(bestillingRequest);
                    }
                })
                .byDefault()
                .register();
    }
}