package com.msquare.flabook.openapi.kakao;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Address {

    @Data
    @NoArgsConstructor
    public static class Meta {
        private int total_count; //NOSONAR
    }

    @Data
    @NoArgsConstructor
    public static class RoadAddress {
        private String address_name; //NOSONAR
        private String region_1depth_name; //NOSONAR
        private String region_2depth_name; //NOSONAR
        private String region_3depth_name; //NOSONAR
        private String road_name; //NOSONAR
        private String underground_yn; //NOSONAR
        private String main_building_no; //NOSONAR
        private String sub_building_no; //NOSONAR
        private String building_name; //NOSONAR
        private String zone_no; //NOSONAR

    }

    @Data
    @NoArgsConstructor
    public static class JibunAddress {
        private String address_name; //NOSONAR
        private String region_1depth_name; //NOSONAR
        private String region_2depth_name; //NOSONAR
        private String region_3depth_name; //NOSONAR
        private String mountain_yn; //NOSONAR
        private String main_address_no; //NOSONAR
        private String sub_address_no; //NOSONAR
        private String zip_code; //NOSONAR
    }

    @Data
    @NoArgsConstructor
    public static class Document {
        private RoadAddress road_address; //NOSONAR
        private JibunAddress address;
    }

    private Meta meta;
    private List<Document> documents;

}
