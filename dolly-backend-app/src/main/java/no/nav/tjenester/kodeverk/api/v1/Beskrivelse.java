// TODO: Fjern avheningheten til dette bibliotekt som ikke finnes utenfor utv image

package no.nav.tjenester.kodeverk.api.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(
        description = "En beskrivelse er den tekstlige delen av betydningen til en kode, og den kan derfor komme på flere språk. For eksempel, landkoden \"NOR\" kan ha beskrivelsen \"Norge\" på norsk, men \"Norway\" på engelsk. Dersom man ber om å få beskrivelsene på et språk som ikke finnes, så vil bokmålsversjonen brukes isteden."
)
public class Beskrivelse {
    @ApiModelProperty(
            value = "En kort versjon av beskrivelsen, og passer derfor godt til fremvisning i GUI-elementer.",
            required = true
    )
    private String term;
    @ApiModelProperty(
            value = "En mer utfyllende versjon av beskrivelsen, og derfor passer denne verdien bedre som ledetekster der antall tegn ikke er et like stort problem. Ikke alle beskrivelser har en utfyllende versjon, og i de tilfellene vil kortversjonen gå igjen i dette feltet.",
            required = true
    )
    private String tekst;

    Beskrivelse(String term, String tekst) {
        this.term = term;
        this.tekst = tekst;
    }

    public static Beskrivelse.BeskrivelseBuilder builder() {
        return new Beskrivelse.BeskrivelseBuilder();
    }

    private Beskrivelse() {
    }

    public String getTerm() {
        return this.term;
    }

    public String getTekst() {
        return this.tekst;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Beskrivelse)) {
            return false;
        } else {
            Beskrivelse other = (Beskrivelse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$term = this.getTerm();
                Object other$term = other.getTerm();
                if (this$term == null) {
                    if (other$term != null) {
                        return false;
                    }
                } else if (!this$term.equals(other$term)) {
                    return false;
                }

                Object this$tekst = this.getTekst();
                Object other$tekst = other.getTekst();
                if (this$tekst == null) {
                    if (other$tekst != null) {
                        return false;
                    }
                } else if (!this$tekst.equals(other$tekst)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Beskrivelse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, tekst);
    }

    public String toString() {
        return "Beskrivelse(term=" + this.getTerm() + ", tekst=" + this.getTekst() + ")";
    }

    public static class BeskrivelseBuilder {
        private String term;
        private String tekst;

        BeskrivelseBuilder() {
        }

        public Beskrivelse.BeskrivelseBuilder term(String term) {
            this.term = term;
            return this;
        }

        public Beskrivelse.BeskrivelseBuilder tekst(String tekst) {
            this.tekst = tekst;
            return this;
        }

        public Beskrivelse build() {
            return new Beskrivelse(this.term, this.tekst);
        }

        public String toString() {
            return "Beskrivelse.BeskrivelseBuilder(term=" + this.term + ", tekst=" + this.tekst + ")";
        }
    }
}
