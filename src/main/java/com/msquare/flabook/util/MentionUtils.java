package com.msquare.flabook.util;

import com.msquare.flabook.dto.UserIdWithNicknameDto;
import com.msquare.flabook.models.User;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 언급된 사용자 id 추출, text build
 */
public class MentionUtils {

    private MentionUtils() throws IllegalAccessException{
        throw new IllegalAccessException("MentionUtils is util");
    }

    private static final String MENTIION_PATTERN;
    private static final Pattern PATTERN;

    static {
        MENTIION_PATTERN = "@\\[(\\d+)\\]";
        PATTERN = Pattern.compile(MENTIION_PATTERN);
    }


    /**
     * 언급된 사용자 id 리스트 추출
     * @param str 언급된사용자가 '@{id}' 로 변환된 본문
     * @return List<Long>
     */
    public static List<Long> extractMentions(String str) {
        Matcher matcher = PATTERN.matcher(str);
        List<Long> idList = new ArrayList<>();
        while(matcher.find()){
            idList.add(Long.parseLong(matcher.group(1)));
        }
        return idList;
    }

    /**
     *
     * @param format 빌드전 text
     * @param mentions 언급된 사용자
     * @return 빌드된 text
     */
    public static String build(String format, Set<? extends UserIdWithNicknameDto> mentions) {
        if(mentions == null) return format;
        return new Binder(format
                , mentions.stream().map(mention-> Tuple2.apply(mention.getId(), mention.getNickname())).collect(Collectors.toList())).bind();
    }

    /**
     *
     * @param format 빌드전 text
     * @param mentions 사용자 목록
     * @return 빌드된 text
     */
    @SuppressWarnings("unused")
    public static String buildFromEntity(String format, Set<User> mentions) {
        if(mentions == null) return format;
        return new Binder(format
                , mentions.stream().map(mention-> Tuple2.apply(mention.getId(), mention.getNickname())).collect(Collectors.toList())).bind();
    }

    private static class Binder {

        private static final String EMPTY_STRING = "";
        private final String format;
        List<Tuple2<Long, String>> mentions;
        private Binder(String format, List<Tuple2<Long, String>> mentions) {
            this.format = format;
            this.mentions = mentions;
        }

        public String bind() {
            if(format == null) {
                return EMPTY_STRING;
            }

            String temp = format;
            for(Tuple2<Long, String> user : mentions) {
                temp = temp.replaceAll("@\\[" + user._1() + "\\]", "@" + user._2());
            }
            //mentions 와 매칭되지 않은 @[\d+] 패턴 삭제
            return temp.replaceAll(MENTIION_PATTERN, "");
        }
    }




}
