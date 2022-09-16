package com.msquare.flabook.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품주문데이터
 */
@SuppressWarnings("java:S115")
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDataDto implements Serializable {

    public enum OrderStatus {
    o1 ("주문완료", true/*"입금대기"*/),
    p1 ("결제완료", true)
            ,g1 ("상품준비중")
            ,g2 ("구매발주")
            ,g3 ("상품입고")
            ,g4 ("상품출고")
    ,d1 ("배송중", true)
    ,d2 ("배송완료", true)
            ,s1 ("구매확정")
            ,c1 ("자동취소")
            ,c2 ("품절취소")
            ,c3 ("관리자취소")
            ,c4 ("고객요청취소")
            ,f1 ("결제시도")
            ,f2 ("고객결제중")
            ,f3 ("결제실패")
            ,b1 ("반품접수")
            ,b2 ("반품반송중")
            ,b3 ("반품보류")
            ,b4 ("반품회수완료")
            ,e1 ("교환접수")
            ,e2 ("교환반송중")
            ,e3 ("재배송중")
            ,e4 ("교환보류")
            ,e5 ("교환완료")
            ,r1 ("환불접수")
            ,r2 ("환불보류")
            ,r3 ("환불완료")
            ,z1 ("추가입금대기")
            ,z2 ("추가결제완료")
            ,z3 ("추가배송중")
            ,z4 ("추가배송완료")

        ;


        @Getter
        private final String description;

        //상태 수집여부
        @Getter
        private final boolean use;

        OrderStatus(String description) {
            this.description = description;
            this.use = false;
        }

        OrderStatus(String description, boolean use) {
            this.description = description;
            this.use = use;
        }

    }

    @JacksonXmlCData
    private String memId;
    @JacksonXmlCData
    private String orderNo;
    @JacksonXmlCData
    private String orderGoodsNm;


    //주문일시
    @JacksonXmlCData
    private LocalDateTime orderDate;


    public LocalDateTime getModDt() {
        LocalDateTime temp = LocalDateTime.of(1970, 1, 1, 0, 0);

        if (OrderStatus.o1.equals(this.orderStatus)) {
            return this.getOrderDate();
        }

        for(OrderGoodsDataDto data : orderGoodsDatas) {
            if(data.getLastEventDate() != null && data.getLastEventDate().isAfter(temp)) {
                temp = data.getLastEventDate();
            }
        }
        return temp;
    }

    private OrderStatus orderStatus;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "orderGoodsData")
    private List<OrderGoodsDataDto> orderGoodsDatas;

}
