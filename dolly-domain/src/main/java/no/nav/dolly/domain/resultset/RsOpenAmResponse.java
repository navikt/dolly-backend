package no.nav.dolly.domain.resultset;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsOpenAmResponse {

    private String miljoe;
    private int httpCode;
    private HttpStatus status;
    private String message;
}
