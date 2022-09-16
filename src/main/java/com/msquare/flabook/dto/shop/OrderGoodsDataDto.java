package com.msquare.flabook.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 상품주문 -> 개별 상품별 배송 정보
 */
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderGoodsDataDto implements Serializable {

    private String sno;
    private String goodsNo;

    private OrderDataDto.OrderStatus orderStatus;

    @JacksonXmlCData
    private LocalDateTime paymentDt;
    @JacksonXmlCData
    private LocalDateTime orderDate;
    @JacksonXmlCData
    private LocalDateTime cancelDt;
    @JacksonXmlCData
    private LocalDateTime deliveryDt;
    @JacksonXmlCData
    private LocalDateTime deliveryCompleteDt;

    public LocalDateTime getLastEventDate() {
        switch (orderStatus) {
            case p1 :
                return this.paymentDt;
            case d1 :
                return this.deliveryDt;
            case d2 :
                return this.deliveryCompleteDt;
            default :
                return null;
        }
    }

}
