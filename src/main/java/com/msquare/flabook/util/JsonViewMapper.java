package com.msquare.flabook.util;

import com.msquare.flabook.json.Views;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.function.BiFunction;

public class JsonViewMapper {

    private JsonViewMapper() {
        //
    }

    public static final BiFunction<Object, Class<? extends Views.BaseView>, MappingJacksonValue> MAPPER = (o, o2) -> {
        MappingJacksonValue jacksonValue = new MappingJacksonValue(o);
        jacksonValue.setSerializationView(o2);
        return jacksonValue;
    };
}
