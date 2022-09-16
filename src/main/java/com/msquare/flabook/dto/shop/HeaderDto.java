package com.msquare.flabook.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeaderDto implements Serializable {
    private String code;
    private String msg;
    private int total;

    @JacksonXmlProperty(localName="max_page")
    private String maxPage;

    @JacksonXmlProperty(localName="now_page")
    private int nowPage;
}
