package com.msquare.flabook.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
public class GoodsDataDto implements Serializable {

    //상품번호
    private String goodsNo;

    @JacksonXmlProperty(isAttribute = true)
    private String idx;

    //상품이름
    @JacksonXmlCData
    private String goodsNm;

    public void setGoodsNm(String goodsNm) {
        this.goodsNm = goodsNm.replace("\\\\", "");
    }

    //판매여부 y,n
    private String goodsSellFl;

    //짧은 설명
    @JacksonXmlCData
    private String shortDescription;

    //자체 상품코드
    @JacksonXmlCData
    private String goodsCd;

    //검색어
    @JacksonXmlCData
    private String goodsSearchWord;

    //품절여부 y,n
    @JacksonXmlCData
    private String soldOutFl;

    @Transient
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "mainImageData")
    private List<String> mainImageDatas;

    @Transient
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "magnifyImageData")
    private List<String> magnifyImageDatas;

    //등록일
    @JacksonXmlCData
    private LocalDateTime regDt;

    //수정일
    @JacksonXmlCData
    private LocalDateTime modDt;

    //판매액
    @JacksonXmlCData
    private BigDecimal goodsPrice;

    //할인 원/%
    @JacksonXmlCData
    private BigDecimal goodsDiscount;

    //상품 할인 단위 ( percent=%, price=원)
    @JacksonXmlCData
    private String goodsDiscountUnit;

    //연결 카테고리 코드
    //* 구분자 : |
    @JacksonXmlCData
    private String allCateCd;

    //카테고리 코드
    private String cateCd;

    //공급사번호 (1=본사, 그외 공급사)
    private String scmNo;

    private Integer reviewCnt;
    private Integer plusReviewCnt;
    private Integer cremaReviewCnt;

}
