package com.msquare.flabook.models.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.models.board.ClinicCondition;

import javax.persistence.AttributeConverter;
import java.io.IOException;

@Slf4j
public class ClinicDetailConverter implements AttributeConverter<ClinicCondition, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(ClinicCondition detail) {
        if(detail == null)
            return null;

        String customerInfoJson = null;
        try {
            customerInfoJson = objectMapper.writeValueAsString(detail);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    public ClinicCondition convertToEntityAttribute(String customerInfoJSON) {

        if(customerInfoJSON == null || customerInfoJSON.isEmpty())
            return null;

        ClinicCondition customerInfo = null;
        try {
            customerInfo = objectMapper.readValue(customerInfoJSON, ClinicCondition.class);
        } catch (final IOException e) {
            log.error("JSON writing error", e);
        }

        return customerInfo;
    }

}
