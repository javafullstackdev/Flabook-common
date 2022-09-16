package com.msquare.flabook.common.configurations;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

    @SuppressWarnings("unused")
    @Value("${spring.redis.host}")
    private String redisHost;

    @SuppressWarnings("unused")
    @Value("${spring.redis.port}")
    private int redisPort;

    public RedisCacheConfig() {
        //
    }


    public static class PointToJsonSerializer extends JsonSerializer<Point> {

        @Override
        public void serialize(Point value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            String jsonValue = "null";
            try {
                if (value != null) {
                    double lat = value.getY();
                    double lon = value.getX();
                    jsonValue = String.format("POINT (%s %s)", lat, lon);
                }
            } catch (Exception e) {
                //
            }
            jgen.writeString(jsonValue);
        }
    }


    public static class JsonToPointDeserializer extends JsonDeserializer<Point> {

        private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 26910);

        @Override
        public Point deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            try {
                String text = jp.getText();
                if (text == null || text.length() <= 0)
                    return null;
                String[] coordinates = text.replaceFirst("POINT?\\(", "").replaceFirst("\\)", "").split("");
                double lat = Double.parseDouble(coordinates[0]);
                double lon = Double.parseDouble(coordinates[1]);

                return geometryFactory.createPoint(new Coordinate(lat, lon));
            } catch (Exception e) {
                return null;
            }
        }
    }

}
