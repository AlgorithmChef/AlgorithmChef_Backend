package com.webservice.algorithmchef.dto.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CookRcpResponse {

    @JsonProperty("COOKRCP01")
    private Body cookrcp01;

    public Body getCOOKRCP01() {
        return cookrcp01;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JsonProperty("total_count")
        private String totalCount;

        @JsonProperty("row")
        private List<Item> row;

        public String getTotalCount() {
            return totalCount;
        }

        public List<Item> getRow() {
            // NPE 방지용
            return row == null ? List.of() : row;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("RCP_NM")
        private String name;

        @JsonProperty("RCP_SEQ")
        private String recipeIdRaw;


        @JsonProperty("RCP_PARTS_DTLS")
        private String parts;

        @JsonProperty("ATT_FILE_NO_MAIN")
        private String imageUrl;

        @JsonProperty("INFO_ENG")
        private String infoEng;

        @JsonProperty("HASH_TAG")
        private String hashTag;

        @JsonProperty("RCP_WAY2")
        private String way;

        @JsonProperty("RCP_PAT2")
        private String category;

        @JsonProperty("MANUAL01") private String manual01;
        @JsonProperty("MANUAL02") private String manual02;
        @JsonProperty("MANUAL03") private String manual03;
        @JsonProperty("MANUAL04") private String manual04;
        @JsonProperty("MANUAL05") private String manual05;
        @JsonProperty("MANUAL06") private String manual06;
        @JsonProperty("MANUAL07") private String manual07;
        @JsonProperty("MANUAL08") private String manual08;
        @JsonProperty("MANUAL09") private String manual09;
        @JsonProperty("MANUAL10") private String manual10;
        @JsonProperty("MANUAL11") private String manual11;
        @JsonProperty("MANUAL12") private String manual12;
        @JsonProperty("MANUAL13") private String manual13;
        @JsonProperty("MANUAL14") private String manual14;
        @JsonProperty("MANUAL15") private String manual15;
        @JsonProperty("MANUAL16") private String manual16;
        @JsonProperty("MANUAL17") private String manual17;
        @JsonProperty("MANUAL18") private String manual18;
        @JsonProperty("MANUAL19") private String manual19;
        @JsonProperty("MANUAL20") private String manual20;


        public String getName() {
            return name;
        }

        public String getParts() {
            return parts;
        }

        public String getDescription() {
            return parts;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getInfoEng() {
            return infoEng;
        }

        public Double getKcal() {
            if (infoEng == null) return null;
            try {
                String n = infoEng.replaceAll("[^0-9.]", "");
                return n.isEmpty() ? null : Double.valueOf(n);
            } catch (Exception e) {
                return null;
            }
        }

        public Double getKcalOrNull() {
            return getKcal();
        }

        public String getHashTag() {
            return hashTag;
        }

        public String getWay() {
            return way;
        }

        public String getCategory() {
            return category;
        }

        public String getType() {
            if (category != null && !category.isBlank()) return category;
            if (way != null && !way.isBlank()) return way;
            return "기타";
        }

        public String getInstructions() {
            List<String> steps = new ArrayList<>();

            for (String s : List.of(
                    manual01, manual02, manual03, manual04, manual05,
                    manual06, manual07, manual08, manual09, manual10,
                    manual11, manual12, manual13, manual14, manual15,
                    manual16, manual17, manual18, manual19, manual20
            )) {
                if (s == null) continue;

                String t = s
                        .replace("\r\n", "\n")
                        .replace("\\n", "\n")
                        .replaceAll("[ \t]+", " ")
                        .trim();

                if (!t.isEmpty()) {
                    steps.add(t);
                }
            }

            return String.join("\n", steps);
        }
    }
}
