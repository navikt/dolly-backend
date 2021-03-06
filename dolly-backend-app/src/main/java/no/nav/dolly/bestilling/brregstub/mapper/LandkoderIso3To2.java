package no.nav.dolly.bestilling.brregstub.mapper;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LandkoderIso3To2 {

    private static Map<String, String> countryISOMapping = new HashMap<>();
    static {//NOSONAR
        countryISOMapping.put("AFG", "AF");
        countryISOMapping.put("ALA", "AX");
        countryISOMapping.put("ALB", "AL");
        countryISOMapping.put("DZA", "DZ");
        countryISOMapping.put("ASM", "AS");
        countryISOMapping.put("AND", "AD");
        countryISOMapping.put("AGO", "AO");
        countryISOMapping.put("AIA", "AI");
        countryISOMapping.put("ATA", "AQ");
        countryISOMapping.put("ATG", "AG");
        countryISOMapping.put("ARG", "AR");
        countryISOMapping.put("ARM", "AM");
        countryISOMapping.put("ABW", "AW");
        countryISOMapping.put("AUS", "AU");
        countryISOMapping.put("AUT", "AT");
        countryISOMapping.put("AZE", "AZ");
        countryISOMapping.put("BHS", "BS");
        countryISOMapping.put("BHR", "BH");
        countryISOMapping.put("BGD", "BD");
        countryISOMapping.put("BRB", "BB");
        countryISOMapping.put("BLR", "BY");
        countryISOMapping.put("BEL", "BE");
        countryISOMapping.put("BLZ", "BZ");
        countryISOMapping.put("BEN", "BJ");
        countryISOMapping.put("BMU", "BM");
        countryISOMapping.put("BTN", "BT");
        countryISOMapping.put("BOL", "BO");
        countryISOMapping.put("BIH", "BA");
        countryISOMapping.put("BWA", "BW");
        countryISOMapping.put("BVT", "BV");
        countryISOMapping.put("BRA", "BR");
        countryISOMapping.put("VGB", "VG");
        countryISOMapping.put("IOT", "IO");
        countryISOMapping.put("BRN", "BN");
        countryISOMapping.put("BGR", "BG");
        countryISOMapping.put("BFA", "BF");
        countryISOMapping.put("BDI", "BI");
        countryISOMapping.put("KHM", "KH");
        countryISOMapping.put("CMR", "CM");
        countryISOMapping.put("CAN", "CA");
        countryISOMapping.put("CPV", "CV");
        countryISOMapping.put("CYM", "KY");
        countryISOMapping.put("CAF", "CF");
        countryISOMapping.put("TCD", "TD");
        countryISOMapping.put("CHL", "CL");
        countryISOMapping.put("CHN", "CN");
        countryISOMapping.put("HKG", "HK");
        countryISOMapping.put("MAC", "MO");
        countryISOMapping.put("CXR", "CX");
        countryISOMapping.put("CCK", "CC");
        countryISOMapping.put("COL", "CO");
        countryISOMapping.put("COM", "KM");
        countryISOMapping.put("COG", "CG");
        countryISOMapping.put("COD", "CD");
        countryISOMapping.put("COK", "CK");
        countryISOMapping.put("CRI", "CR");
        countryISOMapping.put("CIV", "CI");
        countryISOMapping.put("HRV", "HR");
        countryISOMapping.put("CUB", "CU");
        countryISOMapping.put("CYP", "CY");
        countryISOMapping.put("CZE", "CZ");
        countryISOMapping.put("DNK", "DK");
        countryISOMapping.put("DJI", "DJ");
        countryISOMapping.put("DMA", "DM");
        countryISOMapping.put("DOM", "DO");
        countryISOMapping.put("ECU", "EC");
        countryISOMapping.put("EGY", "EG");
        countryISOMapping.put("SLV", "SV");
        countryISOMapping.put("GNQ", "GQ");
        countryISOMapping.put("ERI", "ER");
        countryISOMapping.put("EST", "EE");
        countryISOMapping.put("ETH", "ET");
        countryISOMapping.put("FLK", "FK");
        countryISOMapping.put("FRO", "FO");
        countryISOMapping.put("FJI", "FJ");
        countryISOMapping.put("FIN", "FI");
        countryISOMapping.put("FRA", "FR");
        countryISOMapping.put("GUF", "GF");
        countryISOMapping.put("PYF", "PF");
        countryISOMapping.put("ATF", "TF");
        countryISOMapping.put("GAB", "GA");
        countryISOMapping.put("GMB", "GM");
        countryISOMapping.put("GEO", "GE");
        countryISOMapping.put("DEU", "DE");
        countryISOMapping.put("GHA", "GH");
        countryISOMapping.put("GIB", "GI");
        countryISOMapping.put("GRC", "GR");
        countryISOMapping.put("GRL", "GL");
        countryISOMapping.put("GRD", "GD");
        countryISOMapping.put("GLP", "GP");
        countryISOMapping.put("GUM", "GU");
        countryISOMapping.put("GTM", "GT");
        countryISOMapping.put("GGY", "GG");
        countryISOMapping.put("GIN", "GN");
        countryISOMapping.put("GNB", "GW");
        countryISOMapping.put("GUY", "GY");
        countryISOMapping.put("HTI", "HT");
        countryISOMapping.put("HMD", "HM");
        countryISOMapping.put("VAT", "VA");
        countryISOMapping.put("HND", "HN");
        countryISOMapping.put("HUN", "HU");
        countryISOMapping.put("ISL", "IS");
        countryISOMapping.put("IND", "IN");
        countryISOMapping.put("IDN", "ID");
        countryISOMapping.put("IRN", "IR");
        countryISOMapping.put("IRQ", "IQ");
        countryISOMapping.put("IRL", "IE");
        countryISOMapping.put("IMN", "IM");
        countryISOMapping.put("ISR", "IL");
        countryISOMapping.put("ITA", "IT");
        countryISOMapping.put("JAM", "JM");
        countryISOMapping.put("JPN", "JP");
        countryISOMapping.put("JEY", "JE");
        countryISOMapping.put("JOR", "JO");
        countryISOMapping.put("KAZ", "KZ");
        countryISOMapping.put("KEN", "KE");
        countryISOMapping.put("KIR", "KI");
        countryISOMapping.put("PRK", "KP");
        countryISOMapping.put("KOR", "KR");
        countryISOMapping.put("KWT", "KW");
        countryISOMapping.put("KGZ", "KG");
        countryISOMapping.put("LAO", "LA");
        countryISOMapping.put("LVA", "LV");
        countryISOMapping.put("LBN", "LB");
        countryISOMapping.put("LSO", "LS");
        countryISOMapping.put("LBR", "LR");
        countryISOMapping.put("LBY", "LY");
        countryISOMapping.put("LIE", "LI");
        countryISOMapping.put("LTU", "LT");
        countryISOMapping.put("LUX", "LU");
        countryISOMapping.put("MKD", "MK");
        countryISOMapping.put("MDG", "MG");
        countryISOMapping.put("MWI", "MW");
        countryISOMapping.put("MYS", "MY");
        countryISOMapping.put("MDV", "MV");
        countryISOMapping.put("MLI", "ML");
        countryISOMapping.put("MLT", "MT");
        countryISOMapping.put("MHL", "MH");
        countryISOMapping.put("MTQ", "MQ");
        countryISOMapping.put("MRT", "MR");
        countryISOMapping.put("MUS", "MU");
        countryISOMapping.put("MYT", "YT");
        countryISOMapping.put("MEX", "MX");
        countryISOMapping.put("FSM", "FM");
        countryISOMapping.put("MDA", "MD");
        countryISOMapping.put("MCO", "MC");
        countryISOMapping.put("MNG", "MN");
        countryISOMapping.put("MNE", "ME");
        countryISOMapping.put("MSR", "MS");
        countryISOMapping.put("MAR", "MA");
        countryISOMapping.put("MOZ", "MZ");
        countryISOMapping.put("MMR", "MM");
        countryISOMapping.put("NAM", "NA");
        countryISOMapping.put("NRU", "NR");
        countryISOMapping.put("NPL", "NP");
        countryISOMapping.put("NLD", "NL");
        countryISOMapping.put("ANT", "AN");
        countryISOMapping.put("NCL", "NC");
        countryISOMapping.put("NZL", "NZ");
        countryISOMapping.put("NIC", "NI");
        countryISOMapping.put("NER", "NE");
        countryISOMapping.put("NGA", "NG");
        countryISOMapping.put("NIU", "NU");
        countryISOMapping.put("NFK", "NF");
        countryISOMapping.put("MNP", "MP");
        countryISOMapping.put("NOR", "NO");
        countryISOMapping.put("OMN", "OM");
        countryISOMapping.put("PAK", "PK");
        countryISOMapping.put("PLW", "PW");
        countryISOMapping.put("PSE", "PS");
        countryISOMapping.put("PAN", "PA");
        countryISOMapping.put("PNG", "PG");
        countryISOMapping.put("PRY", "PY");
        countryISOMapping.put("PER", "PE");
        countryISOMapping.put("PHL", "PH");
        countryISOMapping.put("PCN", "PN");
        countryISOMapping.put("POL", "PL");
        countryISOMapping.put("PRT", "PT");
        countryISOMapping.put("PRI", "PR");
        countryISOMapping.put("QAT", "QA");
        countryISOMapping.put("REU", "RE");
        countryISOMapping.put("ROU", "RO");
        countryISOMapping.put("RUS", "RU");
        countryISOMapping.put("RWA", "RW");
        countryISOMapping.put("BLM", "BL");
        countryISOMapping.put("SHN", "SH");
        countryISOMapping.put("KNA", "KN");
        countryISOMapping.put("LCA", "LC");
        countryISOMapping.put("MAF", "MF");
        countryISOMapping.put("SPM", "PM");
        countryISOMapping.put("VCT", "VC");
        countryISOMapping.put("WSM", "WS");
        countryISOMapping.put("SMR", "SM");
        countryISOMapping.put("STP", "ST");
        countryISOMapping.put("SAU", "SA");
        countryISOMapping.put("SEN", "SN");
        countryISOMapping.put("SRB", "RS");
        countryISOMapping.put("SYC", "SC");
        countryISOMapping.put("SLE", "SL");
        countryISOMapping.put("SGP", "SG");
        countryISOMapping.put("SVK", "SK");
        countryISOMapping.put("SVN", "SI");
        countryISOMapping.put("SLB", "SB");
        countryISOMapping.put("SOM", "SO");
        countryISOMapping.put("ZAF", "ZA");
        countryISOMapping.put("SGS", "GS");
        countryISOMapping.put("SSD", "SS");
        countryISOMapping.put("ESP", "ES");
        countryISOMapping.put("LKA", "LK");
        countryISOMapping.put("SDN", "SD");
        countryISOMapping.put("SUR", "SR");
        countryISOMapping.put("SJM", "SJ");
        countryISOMapping.put("SWZ", "SZ");
        countryISOMapping.put("SWE", "SE");
        countryISOMapping.put("CHE", "CH");
        countryISOMapping.put("SYR", "SY");
        countryISOMapping.put("TWN", "TW");
        countryISOMapping.put("TJK", "TJ");
        countryISOMapping.put("TZA", "TZ");
        countryISOMapping.put("THA", "TH");
        countryISOMapping.put("TLS", "TL");
        countryISOMapping.put("TGO", "TG");
        countryISOMapping.put("TKL", "TK");
        countryISOMapping.put("TON", "TO");
        countryISOMapping.put("TTO", "TT");
        countryISOMapping.put("TUN", "TN");
        countryISOMapping.put("TUR", "TR");
        countryISOMapping.put("TKM", "TM");
        countryISOMapping.put("TCA", "TC");
        countryISOMapping.put("TUV", "TV");
        countryISOMapping.put("UGA", "UG");
        countryISOMapping.put("UKR", "UA");
        countryISOMapping.put("ARE", "AE");
        countryISOMapping.put("GBR", "GB");
        countryISOMapping.put("USA", "US");
        countryISOMapping.put("UMI", "UM");
        countryISOMapping.put("URY", "UY");
        countryISOMapping.put("UZB", "UZ");
        countryISOMapping.put("VUT", "VU");
        countryISOMapping.put("VEN", "VE");
        countryISOMapping.put("VNM", "VN");
        countryISOMapping.put("VIR", "VI");
        countryISOMapping.put("WLF", "WF");
        countryISOMapping.put("ESH", "EH");
        countryISOMapping.put("YEM", "YE");
        countryISOMapping.put("ZMB", "ZM");
        countryISOMapping.put("ZWE", "ZW");
        countryISOMapping.put("XKX", "XK");
    }

    public static String getCountryIso2(String countryCode) {
        return countryISOMapping.getOrDefault(countryCode, "NO");
    }
}