// TODO: Fjern avheningheten til dette bibliotekt som ikke finnes utenfor utv image

package no.nav.tjenester.kodeverk.api.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ApiModel(
        description = "En betydning er en tidsbegrenset periode hvor en gitt kode har en reell betydning. For eksempel kunne koden \"OSLO\" hatt to betydninger: en fra 1048 til 1624, og en fra 1925. Dette er fordi Oslo ble omdøpt til Christiania i en periode."
)
public class Betydning {
    @ApiModelProperty(
            value = "Når denne betydningen trådte i kraft, på YYYY-MM-DD format.",
            required = true
    )
    private LocalDate gyldigFra;
    @ApiModelProperty(
            value = "Når denne betydningen slutter å være gyldig, på YYYY-MM-DD format.",
            required = true
    )
    private LocalDate gyldigTil;
    @ApiModelProperty(
            value = "En samling beskrivelser for denne betydningen, mappet til en språkkode.",
            required = true
    )
    private Map<String, Beskrivelse> beskrivelser;

    public Map<String, Beskrivelse> getBeskrivelser() {
        if (this.beskrivelser == null) {
            this.beskrivelser = new HashMap();
        }

        return this.beskrivelser;
    }

    Betydning(LocalDate gyldigFra, LocalDate gyldigTil, Map<String, Beskrivelse> beskrivelser) {
        this.gyldigFra = gyldigFra;
        this.gyldigTil = gyldigTil;
        this.beskrivelser = beskrivelser;
    }

    public static Betydning.BetydningBuilder builder() {
        return new Betydning.BetydningBuilder();
    }

    private Betydning() {
    }

    public LocalDate getGyldigFra() {
        return this.gyldigFra;
    }

    public LocalDate getGyldigTil() {
        return this.gyldigTil;
    }

    public void setGyldigFra(LocalDate gyldigFra) {
        this.gyldigFra = gyldigFra;
    }

    public void setGyldigTil(LocalDate gyldigTil) {
        this.gyldigTil = gyldigTil;
    }

    public void setBeskrivelser(Map<String, Beskrivelse> beskrivelser) {
        this.beskrivelser = beskrivelser;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Betydning)) {
            return false;
        } else {
            Betydning other = (Betydning)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$gyldigFra = this.getGyldigFra();
                    Object other$gyldigFra = other.getGyldigFra();
                    if (this$gyldigFra == null) {
                        if (other$gyldigFra == null) {
                            break label47;
                        }
                    } else if (this$gyldigFra.equals(other$gyldigFra)) {
                        break label47;
                    }

                    return false;
                }

                Object this$gyldigTil = this.getGyldigTil();
                Object other$gyldigTil = other.getGyldigTil();
                if (this$gyldigTil == null) {
                    if (other$gyldigTil != null) {
                        return false;
                    }
                } else if (!this$gyldigTil.equals(other$gyldigTil)) {
                    return false;
                }

                Object this$beskrivelser = this.getBeskrivelser();
                Object other$beskrivelser = other.getBeskrivelser();
                if (this$beskrivelser == null) {
                    if (other$beskrivelser != null) {
                        return false;
                    }
                } else if (!this$beskrivelser.equals(other$beskrivelser)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Betydning;
    }


    @Override
    public int hashCode() {
        return Objects.hash(gyldigFra, gyldigTil, beskrivelser);
    }

    public String toString() {
        return "Betydning(gyldigFra=" + this.getGyldigFra() + ", gyldigTil=" + this.getGyldigTil() + ", beskrivelser=" + this.getBeskrivelser() + ")";
    }

    public static class BetydningBuilder {
        private LocalDate gyldigFra;
        private LocalDate gyldigTil;
        private Map<String, Beskrivelse> beskrivelser;

        BetydningBuilder() {
        }

        public Betydning.BetydningBuilder gyldigFra(LocalDate gyldigFra) {
            this.gyldigFra = gyldigFra;
            return this;
        }

        public Betydning.BetydningBuilder gyldigTil(LocalDate gyldigTil) {
            this.gyldigTil = gyldigTil;
            return this;
        }

        public Betydning.BetydningBuilder beskrivelser(Map<String, Beskrivelse> beskrivelser) {
            this.beskrivelser = beskrivelser;
            return this;
        }

        public Betydning build() {
            return new Betydning(this.gyldigFra, this.gyldigTil, this.beskrivelser);
        }

        public String toString() {
            return "Betydning.BetydningBuilder(gyldigFra=" + this.gyldigFra + ", gyldigTil=" + this.gyldigTil + ", beskrivelser=" + this.beskrivelser + ")";
        }
    }
}