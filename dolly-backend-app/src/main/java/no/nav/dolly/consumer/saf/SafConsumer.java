package no.nav.dolly.consumer.saf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.CharStreams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.pdlperson.GraphQLRequest;
import no.nav.dolly.consumer.saf.domain.SafRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.apache.http.Consts;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils.isNotBlank;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.apache.http.util.TextUtils.isBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Service
@RequiredArgsConstructor
public class SafConsumer {

    private static final String SAF_URL = "/rest/hentdokument";
    private static final String SAF_GRAPHQL_URL = "/graphql";
    private static final String GRAPHQL_INNTEKTSMELDING = "saf/safquery-inntektsmelding.graphql";
    private static final String GRAPHQL_DOKARKIV = "saf/safquery-dokarkiv.graphql";
    private static final String PREPROD_ENV = "q";
    private static final String TEST_ENV = "t";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;
    private final ObjectMapper objectMapper;

    @Timed(name = "providers", tags = {"operation", "hent-inntektsmelding-dokumentinfo"})
    public List<JsonNode> getInntektsmeldingDokumentinfo(String environment, String journalpostId, String dokumentinfoId, String variantformat) {

        JsonNode joarkMetadata = sendJoarkMetadataQuery(environment, journalpostId, GRAPHQL_INNTEKTSMELDING);
        if (nonNull(joarkMetadata) && joarkMetadata.has("feil")) {
            return Collections.singletonList(joarkMetadata);
        }

        if (isBlank(dokumentinfoId) && nonNull(joarkMetadata)) {
            JsonNode joarkMetadataValue = joarkMetadata.findValue("dokumentInfoId");
            dokumentinfoId = isNull(joarkMetadataValue) ? null : joarkMetadataValue.asText();
        }

        return getSamletDokumentinfo(
                joarkMetadata,
                sendJoarkDokumentQuery(environment, new SafRequest(dokumentinfoId, journalpostId, variantformat)));
    }

    @Timed(name = "providers", tags = {"operation", "hent-dokarkiv-dokumentinfo"})
    public JsonNode getDokarkivDokumentinfo(String environment, String journalpostId) {

        return sendJoarkMetadataQuery(environment, journalpostId, GRAPHQL_DOKARKIV);
    }

    private List<JsonNode> getSamletDokumentinfo(JsonNode node, String xml) {
        List<JsonNode> samletJson = new ArrayList<>();
        try {
            if (nonNull(node)) {
                samletJson.add(node);
            }
            if (isNotBlank(xml)) {
                XmlMapper xmlMapper = new XmlMapper();
                samletJson.add(xmlMapper.readTree(xml));
            }
        } catch (IOException e) {
            log.error("Json samling av dokument og metadata feilet: " + e);
        }
        return samletJson;
    }

    private String sendJoarkDokumentQuery(String environment, SafRequest request) {

        if (isNotBlank(request.getDokumentInfoId())) {
            return null;
        }
        try {
            return restTemplate.exchange(
                    RequestEntity.get(URI.create(String.format("%s%s/%s/%s/%s", providersProps.getJoark().getUrl().replace("$", environment), SAF_URL,
                            request.getJournalpostId(), request.getDokumentInfoId(), request.getVariantFormat())))
                            .header(AUTHORIZATION, stsOidcService.getIdToken(environment.contains(PREPROD_ENV) ? PREPROD_ENV : TEST_ENV))
                            .header(HEADER_NAV_CALL_ID, getNavCallId("SafDokumentRequest", environment))
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER).build(),
                    String.class).getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    private JsonNode sendJoarkMetadataQuery(String environment, String journalpostId, String graphqlFile) {

        String query = null;
        InputStream queryStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(graphqlFile);
        try {
            Reader reader = new InputStreamReader(requireNonNull(queryStream), Consts.UTF_8);
            query = CharStreams.toString(reader);
        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet");
        }

        GraphQLRequest graphQLRequest = GraphQLRequest.builder()
                .query(query)
                .variables(Map.of("journalpostId", journalpostId))
                .build();


        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    RequestEntity.post(URI.create(format("%s%s", providersProps.getJoark().getUrl().replace("$", environment),
                            SAF_GRAPHQL_URL)))
                            .header(AUTHORIZATION, stsOidcService.getIdToken(environment.contains(PREPROD_ENV) ? PREPROD_ENV : TEST_ENV))
                            .header(HEADER_NAV_CALL_ID, getNavCallId("SafMetadataRequest", environment))
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(graphQLRequest),
                    JsonNode.class);

            return response.getBody();

        } catch (HttpClientErrorException error) {
            if (NOT_FOUND.equals(error.getStatusCode())) {
                return lagJsonFeilmelding(
                        "SAF endepunkt for " + environment + " ikke funnet.");
            }
            if (FORBIDDEN.equals(error.getStatusCode())) {
                return lagJsonFeilmelding(
                        "Manglende tilgang i SAF til å lese dokument.");
            }
            if (UNAUTHORIZED.equals(error.getStatusCode())) {
                return lagJsonFeilmelding(
                        "Token ikke godtatt av SAF.");
            }
        }
        return objectMapper.createObjectNode();
    }

    private JsonNode lagJsonFeilmelding(String feilmelding) {

        final String KONSEKVENS_FOR_BRUKER = "Henting av dokumenter for dette miljøet støttes derfor ikke";
        try {
            return objectMapper.readTree(String.format("{\"feil\":\"%s %s\"}", feilmelding, KONSEKVENS_FOR_BRUKER));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getNavCallId(String message, String environment) {

        String callId = format("%s %s", CONSUMER, UUID.randomUUID().toString());
        log.info("{} sendt, callId: {}, consumerId: {}, miljø: {}", message, callId, CONSUMER, environment);
        return callId;
    }
}
