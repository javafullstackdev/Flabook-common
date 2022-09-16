package com.msquare.flabook.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponseDto implements Serializable {
    private HeaderDto header;

    @JacksonXmlElementWrapper(localName = "return")
    @JacksonXmlProperty(localName = "order_data")
    private List<OrderDataDto> orderDatas;
}
