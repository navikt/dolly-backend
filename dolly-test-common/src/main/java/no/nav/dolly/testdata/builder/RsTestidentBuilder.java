package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.resultSet.RsTestident;

@Getter
@Setter
@Builder
public class RsTestidentBuilder {

    private Long ident;

    public RsTestident convertToRealRsTestident(){
        RsTestident testident = new RsTestident();
        testident.setIdent(this.ident);

        return testident;
    }
}
