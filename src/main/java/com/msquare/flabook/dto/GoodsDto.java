package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import com.msquare.flabook.dto.mapper.GoodsMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.shop.Goods;
import com.msquare.flabook.common.configurations.ServiceHost;
import org.mapstruct.Mapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties({"createdAt", "modifiedAt", "type", "tagType", "visibility"})
@JsonView(Views.BaseView.class)
@Mapper
public class GoodsDto implements Serializable {

    //https://blog.deliwind.com/posts/235
    @NonNull
    private String goodsNo;

    @JsonProperty("name")
    private String goodsNm;

    //판매액
    @JsonProperty("price")
    private BigDecimal goodsPrice;

    @JsonIgnore
    //할인 원/%
    private BigDecimal goodsDiscount;

    @JsonIgnore
    //상품 할인 단위 ( percent=%, price=원)
    private String goodsDiscountUnit;

    private String photoUrl;

    private Integer reviewCnt;

    @JsonIgnore
    private ImageResource imageResource;

    @JsonIgnore
    private List<String> mainImageDatas;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private ResourceDto resource;

    public Integer getReviewCnt() {
        if(reviewCnt == null || reviewCnt < 0) {
            return 0;
        }

        return reviewCnt;
    }

    @JsonProperty("discountedPrice")
    public BigDecimal getGoodsDiscountedPrice() {
        if(goodsDiscount.intValue() == 0)
            return goodsPrice;
        if("percent".equals(goodsDiscountUnit)) {
            return goodsPrice.subtract(goodsPrice.multiply(goodsDiscount.divide(HUNDRED, 0, RoundingMode.UP))).setScale(0, RoundingMode.UP);
        } else {
            return goodsPrice.subtract(goodsDiscount);
        }

    }

    public String getPhotoUrl() {
        if (photoUrl != null)
            return photoUrl;

        if(imageResource != null)
            return ServiceHost.getS3Url(imageResource.getFilekey());

        return ServiceHost.getLogoUrl();
    }

    public static GoodsDto of(Goods goods) {
        return GoodsMapper.INSTANCE.of(goods);
    }



}
