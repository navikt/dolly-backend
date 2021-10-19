package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektsmelding.InntektsmeldingConsumer;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.udistub.UdiStubConsumer;
import no.nav.dolly.domain.resultset.RsDollyProps;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnvironmentPropsController {

    private final ProvidersProps providersProps;
    private final UdiStubConsumer udiStubConsumer;
    private final InntektstubConsumer inntektstubConsumer;
    private final InntektsmeldingConsumer inntektsmeldingConsumer;

    @GetMapping
    @Operation(description = "Hent URL til applikasjonene er integrert mot")
    public RsDollyProps getEnvironmentProps() {
        return RsDollyProps.builder()
                .tpsfUrl(providersProps.getTpsf().getUrl())
                .sigrunStubUrl(providersProps.getSigrunStub().getUrl())
                .krrStubUrl(providersProps.getKrrStub().getUrl())
                .udiStubUrl(providersProps.getUdiStub().getUrl())
                .kodeverkUrl(providersProps.getKodeverk().getUrl())
                .arenaForvalterUrl(providersProps.getArenaForvalter().getUrl())
                .instdataUrl(providersProps.getInstdata().getUrl())
                .aaregdataUrl(providersProps.getAaregdata().getUrl())
                .inntektstub(providersProps.getInntektstub().getUrl())
                .build();
    }

    @GetMapping("/isAlive")
    @Operation(description = "Sjekk om applikasjonene er i live")
    public Map<String, String> checkAlive() {

        return Stream.of(udiStubConsumer.checkAlive().entrySet(),
                        inntektsmeldingConsumer.checkAlive().entrySet(),
                        inntektstubConsumer.checkAlive().entrySet())
                .flatMap(Set::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
