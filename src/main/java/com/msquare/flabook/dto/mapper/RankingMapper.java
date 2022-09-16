package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.GoodsDto;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.dto.RankingDto;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.models.*;
import com.msquare.flabook.models.shop.Goods;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RankingMapper {

    RankingMapper INSTANCE = Mappers.getMapper( RankingMapper.class );

    @Mapping(target = "id", source = "target.id")
    @Mapping(target = "keyword", source = "target.nickname")
    @Mapping(target = "thumbnail", source = "target",  qualifiedByName = "UserThumbnail")
    RankingDto of(UserRanking ranking);

    @Mapping(target = "id", source = "target.goodsNo")
    @Mapping(target = "keyword", source = "target.goodsNm")
    @Mapping(target = "price", source = "target.goodsPrice")
    @Mapping(target = "thumbnail", source = "target",  qualifiedByName = "GoodsThumbnail")
    RankingDto of(GoodsRanking.GoodsRankingDetail ranking);

    @Mapping(target = "id", source = "target.id")
    @Mapping(target = "postingType", source = "target.postingType")
    @Mapping(target = "title", source = "target.title")
    @Mapping(target = "text", source = "target", qualifiedByName = "PostingText")
    @Mapping(target = "thumbnail", source = "target",  qualifiedByName = "PostingThumbnail")
    @Mapping(target = "readCount", source = "target.readCount")
    @Mapping(target = "commentCount", source = "target.commentCount")
    @Mapping(target = "likeCount", source = "target.likeCount")
    RankingDto of(PostingRanking ranking);


    @Named("PostingThumbnail")
    default String postingToPhotoUrl(Posting posting) {
        if(posting.isBlind() || posting.isDelete() || posting.getAttachments().isEmpty())
            return null;

        return PostingDto.of(posting).getAttachments().get(0).getPhotoUrl() + "?d=300x300";
    }

    @Named("PostingText")
    default String postingToText(Posting posting) {
        if(StringUtils.isNotBlank(posting.getTitle())) {
            return posting.getTitle();
        } else {
            return (posting.getText() != null) ? posting.getText().replaceAll("^[\n| ]+", "") : "";
        }
    }

    @Named("GoodsThumbnail")
    default String goodsToPhotoUrl(Goods goods) {
        if(goods.getImageResource() == null)
            return null;

        return GoodsDto.of(goods).getPhotoUrl() + "?d=300x300";
    }

    @Named("UserThumbnail")
    default String userToPhotoUrl(User user) {
        if(user.getImageResource() == null)
            return null;

        return UserDto.of(user).getPhotoUrl() + "?d=300x300";
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keyword", source = "target")
    RankingDto of(KeywordRanking ranking);

    default RankingDto of(RankingDto before, RankingDto after) {

        if(before == null) {
            after.setFluctuation(null);
        } else {
            after.setFluctuation(before.getRank() - after.getRank());
        }

        return after;
    }


}
