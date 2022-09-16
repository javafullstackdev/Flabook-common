package com.msquare.flabook.service.elasticsearch;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchResponse implements Serializable {

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HitBody implements Serializable {
        @JsonProperty("total")
        private int total;
        @JsonProperty("max_score")
        private float maxScore;

        private List<Hit> hits;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hit implements Serializable {
        @JsonProperty("_index")
        private String index;
        @JsonProperty("_type")
        private String type;
        @JsonProperty("_source")
        private Hit.Source source;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Source implements Serializable {
            private Long id;
        }
    }

    private int took;
    private boolean timedOut;
    private HitBody hits;
}
