package com.msquare.flabook.common.configurations;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GeometryConfig {
    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }
}
