package no.nav.dolly.bestilling.aareg.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAareg.RsAaregArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAaregArbeidsforhold.class, Arbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAaregArbeidsforhold rsArbeidsforhold,
                                        Arbeidsforhold arbeidsforhold, MappingContext context) {

                        if (rsArbeidsforhold.getArbeidsgiver() instanceof RsOrganisasjon) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (rsArbeidsforhold.getArbeidsgiver() instanceof RsAktoerPerson) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                        arbeidsforhold.setArbeidsforholdstype((String) context.getProperty("arbeidsforholdstype"));
                        arbeidsforhold.setPermisjon((nonNull(rsArbeidsforhold.getPermisjon()) && !rsArbeidsforhold.getPermisjon().isEmpty())
                                || (nonNull(rsArbeidsforhold.getPermittering()) && !rsArbeidsforhold.getPermittering().isEmpty())
                                ? Stream.of(
                                mapperFacade.mapAsList(rsArbeidsforhold.getPermisjon(), PermisjonDTO.class),
                                mapperFacade.mapAsList(rsArbeidsforhold.getPermittering(), PermisjonDTO.class))
                                .flatMap(Collection::stream).collect(Collectors.toList())
                                : null);
                    }
                })
                .byDefault()
                .register();
    }

}