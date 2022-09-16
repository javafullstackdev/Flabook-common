package com.msquare.flabook.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.util.AccessLogger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component("restAuthenticationEntryPoint")
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final String invalidResult;

	@SneakyThrows
	public RestAuthenticationEntryPoint() {
		ObjectMapper objectMapper = new ObjectMapper();
		invalidResult = objectMapper.writeValueAsString(new CommonResponse<String>(CommonResponseCode.TOKEN_INVALID.getResultCode(), null, "유효하지 않은 토큰입니다."));
	}

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException {

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().write(invalidResult);

		AccessLogger.log(request, response);
	}
}
