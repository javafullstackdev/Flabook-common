package com.msquare.flabook.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.exception.CommonException;

@SuppressWarnings("java:S116")
@Slf4j
@RequiredArgsConstructor
public class SendAuthMtMessage {
	/*
	 * TODO : java conntor 버전 확인, 임시로 버전 내려놈
	 * <artifactId>mysql-connector-java</artifactId>
	 * <version>5.1.22</version>
	 */

	@Value("${infobank.mt.datasource.driverClassName:#{null}}")
	private String DRIVER;
	@Value("${infobank.mt.datasource.url:#{null}}")
	private String URL;
	@Value("${infobank.mt.datasource.username:#{null}}")
	private String USER;
	@Value("${infobank.mt.datasource.password:#{null}}")
	private String PW;
	@Value("${infobank.mt.callback:#{null}}")
	private String callback;
	@Value("${infobank.mt.msg:#{null}}")
	private String msg;

	/*직접 발송 요청 테이블에 적재*/
	public void insertIntoSmtTran(String phoneNumber, String authCode) throws Exception{

		Class.forName(DRIVER);

		//String mtMsg = "[모야모] 휴대폰 인증번호 [" + authCode + "]를 입력해 주세요.";

		String mtMsg = msg.replace("#authCode", authCode);
		StringBuffer sbSQL = null;
		String sSQL = null;
		PreparedStatement ps = null;
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(URL, USER, PW);

			sbSQL = new StringBuffer();
			sbSQL.append(
					" INSERT INTO em_smt_tran (mt_refkey, date_client_req, content, callback, service_type, broadcast_yn, msg_status, recipient_num) ");
			sbSQL.append(" VALUES ");
			sbSQL.append(" ('ARS', NOW(), ?, ?, '0', 'N', '1', ?)");
			sSQL = sbSQL.toString();

			ps = conn.prepareStatement(sSQL);

			int idx = 1;

			ps.setString(idx++, mtMsg);
			ps.setString(idx++, callback);
			ps.setString(idx++, phoneNumber);

			ps.executeUpdate();

			log.info("휴대폰 인증번호 발송 {}", callback);

		} catch (Exception e) {
			log.error("휴대폰 인증번호 발송 실패 {}/{}", callback, e.getMessage());

		} finally {
			conn.close();

		}
	}

	@Value("${infobank.emma.test:false}")
	public boolean EMMA_TEST;

	@Value("${infobank.emma.server.url:http://localhost:8085}")
    public String EMMA_DOMAIN;


	public Boolean isTestMode() {
		return EMMA_TEST;
	}

	/*모야모 MT 발송 서버로 인증 문자 발송 요청*/
	public Boolean send(String phoneNumber, String authCode) throws Exception {

		if(EMMA_TEST) {
			log.info("SendAuthMtMessage TEST : {}", EMMA_TEST);
			return true;
		}

		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(EMMA_DOMAIN + "/send");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> map = new HashMap<String, String>();
		map.put("recipientNum", phoneNumber);
		map.put("code", authCode);

		HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				log.info("EmmaServer, 문자메세지 전송 성공 : {}", response.getBody());
				return true;
			} else {
				log.error("EmmaServer, 문자메세지 전송 실패 : {}", response.getBody());
				throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "문자메세지 전송 실패", null);
			}

		} catch (Exception e) {
			log.error("EmmaServer, 문자메세지 전송 실패 : 서버 로그 확인 필요", e);
			throw new CommonException(CommonResponseCode.FAIL.getResultCode(), "Connection refused", null);
		}
	}

}
