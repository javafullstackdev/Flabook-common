package com.msquare.flabook.models.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ListConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
            return null;
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (final IOException e) {
            log.error("JSON writing error", e);
            return null; //NOSONAR
        }
    }
}
