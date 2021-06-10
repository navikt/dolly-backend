package no.nav.dolly.bestilling.aareg.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAareg.class, Arbeidsforhold.class)
                .customize(new CustomMapper<RsAareg, Arbeidsforhold>() {
                    @Override
                    public void mapAtoB(RsAareg rsArbeidsforhold,
                                        Arbeidsforhold arbeidsforhold, MappingContext context) {

                        if (rsArbeidsforhold.getArbeidsforhold().getArbeidsgiver() instanceof RsOrganisasjon) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (rsArbeidsforhold.getArbeidsforhold().getArbeidsgiver() instanceof RsAktoerPerson) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                    }
                })
                .byDefault()
                .register();
    }
}