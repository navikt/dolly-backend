package no.nav.dolly.bestilling.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest.SyntetiskOrganisasjon.Adresse;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest.AdresseType.FADR;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest.AdresseType.PADR;

@Component
public class OrganisasjonerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsOrganisasjonBestilling.SyntetiskOrganisasjon.class, BestillingRequest.SyntetiskOrganisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsOrganisasjonBestilling.SyntetiskOrganisasjon rsSyntetiskOrganisasjon, BestillingRequest.SyntetiskOrganisasjon requestOrganisasjon, MappingContext context) {
                        List<Adresse> adresser = new ArrayList<>();
                        if (nonNull(rsSyntetiskOrganisasjon.getForretningsadresse())) {
                            adresser.add(
                                    Adresse.builder()
                                            .adresselinjer(rsSyntetiskOrganisasjon.getForretningsadresse().getAdresselinjer())
                                            .adressetype(FADR)
                                            .landkode(rsSyntetiskOrganisasjon.getForretningsadresse().getLandkode())
                                            .kommunenr(rsSyntetiskOrganisasjon.getForretningsadresse().getKommunenr())
                                            .postnr(rsSyntetiskOrganisasjon.getForretningsadresse().getPostnr())
                                            .build());
                        }
                        if (nonNull(rsSyntetiskOrganisasjon.getPostadresse())) {
                            adresser.add(
                                    Adresse.builder()
                                            .adresselinjer(rsSyntetiskOrganisasjon.getPostadresse().getAdresselinjer())
                                            .adressetype(PADR)
                                            .landkode(rsSyntetiskOrganisasjon.getPostadresse().getLandkode())
                                            .kommunenr(rsSyntetiskOrganisasjon.getPostadresse().getKommunenr())
                                            .postnr(rsSyntetiskOrganisasjon.getPostadresse().getPostnr())
                                            .build());
                        }
                        requestOrganisasjon.setAdresser(adresser);

                        if (!rsSyntetiskOrganisasjon.getUnderenheter().isEmpty()) {
                            requestOrganisasjon.setUnderenheter(mapperFacade.mapAsList(rsSyntetiskOrganisasjon.getUnderenheter(), BestillingRequest.SyntetiskOrganisasjon.class));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
