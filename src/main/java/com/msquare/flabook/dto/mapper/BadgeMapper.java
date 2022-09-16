package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.BadgeDto;
import com.msquare.flabook.models.Badge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BadgeMapper {
    BadgeMapper INSTANCE = Mappers.getMapper(BadgeMapper.class);

    BadgeDto of(Badge badge);

    @Mapping(target = "representable", expression = "java(false)")
    @Mapping(target = "trueImageUrl", source = "badge", qualifiedByName = "trueBadgeImage")
    @Mapping(target = "falseImageUrl", source = "badge", qualifiedByName = "falseBadgeImage")
    BadgeDto ofFalse(Badge badge);

    @Mapping(target = "representable", expression = "java(true)")
    @Mapping(target = "trueImageUrl", source = "badge", qualifiedByName = "trueBadgeImage")
    @Mapping(target = "falseImageUrl", source = "badge", qualifiedByName = "falseBadgeImage")
    BadgeDto ofTrue(Badge badge);

    @Named("trueBadgeImage")
    default String trueBadgeImageUrl(Badge badge){
        if(badge.getTrueImageResource() == null)
            return null;
        return BadgeDto.of(badge).getTrueImageUrl();
    }

    @Named("falseBadgeImage")
    default String falseBadgeImageUrl(Badge badge){
        if(badge.getFalseImageResource() == null)
            return null;
        return BadgeDto.of(badge).getFalseImageUrl();
    }
}
