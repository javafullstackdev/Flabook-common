package com.msquare.flabook.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagUtils {

    private HashtagUtils() throws IllegalAccessException {
        throw new IllegalAccessException("HashtagUtils is static");
    }

    private static final String REG_EX_TAG = "(#[가-힣|a-z|A-Z|_]+).*?"; //NOSONAR
    private static final Pattern tagMatcher = Pattern.compile(REG_EX_TAG);

    public static List<String> extract(String text) {
        Matcher m = tagMatcher.matcher(text);
        List<String> list = new ArrayList<>();
        while(m.find())
        {
            list.add(m.group(1));
        }
        return list;
    }
}
