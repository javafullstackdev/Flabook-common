package com.msquare.flabook.models.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.models.*;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper=false)
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods extends BaseEntity implements INotification {

    private static final String RESOURCE_ID_PREFIX = "goods/";

    @Transient
    //@ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Id
    @Column(name = "id", length = 12)
    //상품번호
    private String goodsNo;

    //상품이름
    @Field(analyze = Analyze.YES, store = Store.YES, analyzer = @Analyzer(definition = "goods_korean_text_analyzer"))
    private String goodsNm;

    //판매여부 y,n
    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "char(1)")
    private String goodsSellFl;

    //짧은 설명
    private String shortDescription;

    //자체 상품코드
    @Field(analyze = Analyze.YES, store = Store.YES)
    private String goodsCd;

    //검색어
    @Field(analyze = Analyze.YES, store = Store.YES)
    private String goodsSearchWord;

    //품절여부 y,n
    @Column(columnDefinition = "char(1)")
    private String soldOutFl;

    @Embedded
    private ImageResource imageResource;

    //등록일
    @Field(analyze = Analyze.NO, store = Store.YES)
    private LocalDateTime regDt;

    //수정일
    @Field(analyze = Analyze.NO, store = Store.YES)
    private LocalDateTime modDt;

    //판매액
    private BigDecimal goodsPrice;

    //할인 원/%
    private BigDecimal goodsDiscount;

    //상품 할인 단위 ( percent=%, price=원)
    private String goodsDiscountUnit;

    //연결 카테고리 코드
    //* 구분자 : |
    @Field(analyze = Analyze.YES, store = Store.YES)
    private String allCateCd;

    //카테고리 코드
    @Field(analyze = Analyze.YES, store = Store.YES)
    private String cateCd;

    //공급사번호 (1=본사, 그외 공급사)
    private String scmNo;

    @Transient
    private String customText;

    @Embedded
    @IndexedEmbedded
    private GoodsEtc etc;

    @Column(columnDefinition = "int default 0")
    private Integer reviewCnt;
    @Column(columnDefinition = "int default 0")
    private Integer plusReviewCnt;
    @Column(columnDefinition = "int default 0")
    private Integer cremaReviewCnt;

    @Override
    @Transient
    public String getText() {
        return (customText != null) ? customText : this.getGoodsNm();
    }

    @Override
    @Transient
    public User getOwner() {
        return owner;
    }

    @Override
    @Transient
    public ImageResource getThumbnail() {
        return imageResource;
    }

    @Override
    public Resource asResource() {
        String resourceId = RESOURCE_ID_PREFIX + this.getGoodsNo();
        return new Resource(resourceId
            , Resource.ResourceType.shop
            , resourceId
            , Resource.ResourceType.shop);
    }

}
