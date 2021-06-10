package no.nav.dolly.bestilling.aareg.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAareg.RsAaregArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAaregArbeidsforhold.class, Arbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAaregArbeidsforhold rsArbeidsforhold,
                                        Arbeidsforhold arbeidsforhold, MappingContext context) {

                        if (rsArbeidsforhold.getArbeidsgiver() instanceof RsOrganisasjon && isNull(arbeidsforhold.getArbeidsgiver().getAktoertype())) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (rsArbeidsforhold.getArbeidsgiver() instanceof RsAktoerPerson && isNull(arbeidsforhold.getArbeidsgiver().getAktoertype())) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                    }
                })
                .byDefault()
                .register();
    }
}