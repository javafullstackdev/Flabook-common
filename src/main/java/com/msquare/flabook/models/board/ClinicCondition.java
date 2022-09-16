package com.msquare.flabook.models.board;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicCondition implements Serializable {

    @Getter
    public enum ClinicConditionPlace {
        INDOOR("실내"), OUTDOOR("실외"), VERANDA("베란다");

        private final String name;
        ClinicConditionPlace(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ClinicConditionLight {
        SHADE("음지"), SUN("양지"), HALF("반양지/반음지");

        private final String name;
        ClinicConditionLight(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ClinicConditionWater {
        PERIOD("주기적으로 한 번씩"), LITTLE("조금씩 자주"), THINK("생각날 때마다 한 번 씩"), HARD_DRY("흙이 충분히 마른 후 듬뿍"), DRY("겉흙이 마른 후 듬뿍"), ETC("기타") ;

        private final String name;
        ClinicConditionWater(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ClinicDetailRepotting {
        UNDER_TWO_WEEKS("2주 이내"), UNDER_TWO_YEARS("2년 이내"), OVER_TWO_YEARS("2년 이후"), NONE("없음");

        private final String name;
        ClinicDetailRepotting(String name) {
            this.name = name;
        }
    }

//    @Enumerated(EnumType.STRING)
    private ClinicConditionPlace place;

//    @Enumerated(EnumType.STRING)
    private ClinicConditionLight light;

//    @Enumerated(EnumType.STRING)
    private ClinicConditionWater water;

//    @Enumerated(EnumType.STRING)
    private ClinicDetailRepotting repotting;


}
