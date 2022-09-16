package com.msquare.flabook.util;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class CommonUtils {

	private CommonUtils() throws IllegalAccessException {
		throw new IllegalAccessException("CommonUtils is static");
	}

	/**
	 * 유니크 토큰 생성
	 * @return 신규 토큰
	 */
	public static String createUniqueToken() {

		// UUID 랜덤 생성
		return UUID.randomUUID().toString().replace("-", "");
    }


	/**
	 * 헤더에서 사용자 토큰 가져오기
	 * @param request 리퀘스트
	 * @return 파싱된 토큰
	 */
	@SuppressWarnings("unused")
	public static String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}


	/**
	 * 헤더에서 사용자 locale 가져오기
	 * @param request 리퀘스트
	 * @return 사용자 로케일 정보
	 */
	@SuppressWarnings("unused")
	public static String getLocaleFromRequest(HttpServletRequest request) {
		return request.getHeader("Accept-Language");
	}

	/**
	 * LocalDateTime 기반
	 * Parameter 시간이 현재 시간보다 이후인 경우 true
	 * @param compareDateTime 비교시간
	 * @return Valid 여부
	 */
	@SuppressWarnings("unused")
	public static boolean isLocalDateTimeValid(LocalDateTime compareDateTime) {
		LocalDateTime localDateTime = LocalDateTime.now();

		// 비교 시간이 현재 시간 보다 이후인 경우, True
		return localDateTime.isBefore(compareDateTime);
	}


	/**
	 * 인자값 만큼의 랜덤 int 생성
	 * @param length 길이
	 * @return 랜덤값
	 */
	public static int createRandomNumber(int length) {
        Random random = new Random(System.currentTimeMillis());

        int range = (int)Math.pow(10, length);
        int trim = (int)Math.pow(10, length - 1.0D);
        int result = random.nextInt(range)+trim;

        if(result>range){
            result = result - trim;
        }

        return result;
    }

    @SuppressWarnings("unused")
	public static String createRandomPassword(int len) {
		char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z' };

		Random random = new Random(System.currentTimeMillis());
		int idx;
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < len; i++) {
			idx = (charSet.length * random.nextInt());
			buffer.append(charSet[idx]);
		}

		return buffer.toString();
	}



	/**
	 * String to Locale 변환 함수
	 * @param locale 지역명
	 * @return Locale Object
	 */
	@SuppressWarnings("unused")
	public static Locale localeStringToLocale(String locale) {
	    String[] parts = locale.split("_", -1);
	    if (parts.length == 1) return new Locale(parts[0]);
	    else if (parts.length == 2
	            || (parts.length == 3 && parts[2].startsWith("#")))
	        return new Locale(parts[0], parts[1]);
	    else return new Locale(parts[0], parts[1], parts[2]);
	}

	/**
	 * 문자열 특정 길이 이상 생략 처리
	 * @param str 문자열
	 * @param length 길이
	 * @return 생략 처리 문자열
	 */
	public static String convertDotString(String str, int length) {
		if(str == null) return null;

		str = str.trim();

		if(str.length() > length) {
			return str.substring(0, length-1) + "...";
		} else {
			return str;
		}

	}


	/**
	 * 해당 이미지 파일 확장자 가져오기
	 * Default "jpeg"
	 * @param mediaType 미디어 타입
	 *
	 * @return 파일 확장자
	 */
	@SuppressWarnings("unused")
	public static String getFileExtension(MediaType mediaType) {

		if(mediaType == MediaType.IMAGE_JPEG) {
			return "jpeg";
		} else if(mediaType == MediaType.IMAGE_PNG) {
			return "png";
		} else if(mediaType == MediaType.IMAGE_GIF) {
			return "gif";
		} else {
			return "jpeg";
		}

	}

	/**
	 * 헤더에서 클라이언트 타입 가져오기
	 * @param request 리퀘스트
	 * @return 파싱된 타입
	 */
	@SuppressWarnings("unused")
	public static String getClientType(HttpServletRequest request) {
		return request.getHeader("ClientType");
	}

	/**
	 * 인증번호 재발송 남은 시간 구하기
	 * @param minute 남은 시간을 구하기 위한 기준
	 * @param start 시작시간
	 * @param end 종료시간
	 * @return long
	 */
	public static long getAuthRemainingTime(int minute, ZonedDateTime start, ZonedDateTime end) {
		//재발송 남은 시간 구하기
		Duration duration = Duration.between(start, end);
		return (minute * 60L) - (duration.toMillis()/1000);

	}

	/**
	 * ZonedDateTime format -> yyyy-MM-dd HH:mm:ss
	 */
	public static String convertDateToString(ZonedDateTime zonedDateTime) {
		if(zonedDateTime == null) {
			return null;
		}
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zonedDateTime);
	}
}
