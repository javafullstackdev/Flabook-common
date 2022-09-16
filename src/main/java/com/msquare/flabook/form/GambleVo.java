package com.msquare.flabook.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GambleVo  {

    private GambleVo() throws IllegalAccessException {
        throw new IllegalAccessException("GambleVo is static");
    }

    @Data
    @NoArgsConstructor
    @Validated
    @ToString(callSuper = true)
    public static class CreateItem {
        private String name;
        private Integer amount;
        private Boolean blank = false;
        private Boolean address = false;
    }

    @Data
    @NoArgsConstructor
    @Validated
    @ToString(callSuper = true)
    public static class UpdateItem {
        private String name;
        private Integer amount;
        private Boolean blank;
        private Boolean address;
        private String id;
    }

    @Data
    @NoArgsConstructor
    @Validated
    @ToString(callSuper = true)
    public static class CreateGamble {

        @NotEmpty
        @Length(min = 1, max = 100)
        private String title;

        @NotNull
        private String url;

        @Range(min=0)
        private Integer maxAttempt;

        @Range(min=0, max=23)
        private Integer retryHour;

        @NotNull
        private Boolean active = false;

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private ZonedDateTime start;

        public void setStart(String start){
            this.start = ZonedDateTime.parse(start, DateTimeFormatter.RFC_1123_DATE_TIME);
        }

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private ZonedDateTime end;

        public void setEnd(String end){
            this.end = ZonedDateTime.parse(end, DateTimeFormatter.RFC_1123_DATE_TIME);
        }

        private List<CreateItem> items;

        private MultipartFile file;
    }

    @Data
    @NoArgsConstructor
    @Validated
    @ToString(callSuper = true)
    public static class UpdateGamble {

        @NotEmpty
        @Length(min = 1, max = 100)
        private String title;

        @NotNull
        private String url;

        @Range(min=0)
        private Integer maxAttempt;

        @Range(min=0, max=23)
        private Integer retryHour;

        private Boolean active = false;

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private ZonedDateTime start;

        public void setStart(String start){
            this.start = ZonedDateTime.parse(start, DateTimeFormatter.RFC_1123_DATE_TIME);
        }

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private ZonedDateTime end;

        public void setEnd(String end){
            this.end = ZonedDateTime.parse(end, DateTimeFormatter.RFC_1123_DATE_TIME);
        }

        private List<UpdateItem> items;

        private MultipartFile file;
    }

}
