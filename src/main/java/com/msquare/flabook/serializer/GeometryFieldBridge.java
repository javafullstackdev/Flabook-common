package com.msquare.flabook.serializer;

import com.vividsolutions.jts.geom.Geometry;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.bridge.StringBridge;

@Slf4j
public class GeometryFieldBridge implements StringBridge {
    public String objectToString(Object object) {

        Geometry value = (Geometry) object;
        if (value == null)
            return null;

        return value.toText();
    }
}
