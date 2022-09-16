package com.msquare.flabook.models.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.GoodsRanking;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class GoodsRankingDetailListConverter implements AttributeConverter<List<GoodsRanking.GoodsRankingDetail>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<GoodsRanking.GoodsRankingDetail> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
            return null;
        }
    }

    @Override
    public List<GoodsRanking.GoodsRankingDetail> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<GoodsRanking.GoodsRankingDetail>>() {});
        } catch (final IOException e) {
            log.error("JSON writing error", e);
            return null; //NOSONAR
        }
    }

}
