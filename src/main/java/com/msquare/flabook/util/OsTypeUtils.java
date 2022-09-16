package com.msquare.flabook.util;

import io.micrometer.core.instrument.util.StringUtils;

public class OsTypeUtils {

    private OsTypeUtils() throws IllegalAccessException {
        throw new IllegalAccessException("OsTypeUtils is static");
    }

    @SuppressWarnings("unused")
    public static boolean isIOS(String osType) {
        return StringUtils.isBlank(osType) || "ios".equalsIgnoreCase(osType);
    }

    public static boolean withNotification(String osType) {
        return !(StringUtils.isNotBlank(osType) && "android".equalsIgnoreCase(osType));
    }
}
