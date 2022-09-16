package com.msquare.flabook.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.exceptions.AuthenticationException;
import com.msquare.flabook.api.exceptions.AuthorizationException;
import com.msquare.flabook.api.service.AuthorizationService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.models.User;
import com.msquare.flabook.util.AccessLogger;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class TokenAuthorizationFilter extends OncePerRequestFilter {

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";

    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;

    public TokenAuthorizationFilter(AuthorizationService authorizationService, ObjectMapper objectMapper) {
        this.authorizationService = authorizationService;
        this.objectMapper = objectMapper;
    }


    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException, NullPointerException, CommonException{
        String header = request.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }


        String token = request.getHeader(HEADER_STRING);
        String accessToken = token.substring(7); //Bearer 제거

        Long userId;

        try {

            AuthorizationService.Expires expires = authorizationService.findUserIdByAccessToken(accessToken);
            userId = expires.valid();

        } catch(AuthenticationException e) {
            log.error("Expired Token[{}]", token);

            response.setContentType(JSON_CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.writeValueAsString(new CommonResponse<String>(CommonResponseCode.TOKEN_EXPIRE, null)));

            AccessLogger.log(request, response);
            return;
        } catch(DataAccessResourceFailureException | CannotCreateTransactionException | JDBCConnectionException e) {
            response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            response.setContentType(JSON_CONTENT_TYPE);
            response.getWriter().write(objectMapper.writeValueAsString(new CommonResponse<String>(CommonResponseCode.FAIL.getResultCode(), null, "잠시 후 다시 시도해주세요.")));

            AccessLogger.log(request, response);
            return;
        }

        try {
            User user = authorizationService.findUserById(userId);
            Authentication authentication = getUsernamePasswordAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //사용자 Status 체크
        } catch(AuthorizationException e) {
        	log.error("Not allowed Token[{}], userId : {}", token, userId);

    		response.setContentType(JSON_CONTENT_TYPE);
    		response.setStatus(HttpServletResponse.SC_OK);
    		response.getWriter().write(objectMapper.writeValueAsString(new CommonResponse<String>(CommonResponseCode.USER_LOGIN_AUTH_FAIL, null)));

    		AccessLogger.log(request, response);
    		return;
        } catch(DataAccessResourceFailureException | CannotCreateTransactionException | JDBCConnectionException e ) {
            response.setContentType(JSON_CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            response.getWriter().write(objectMapper.writeValueAsString(new CommonResponse<String>(CommonResponseCode.FAIL.getResultCode(), null, "잠시 후 다시 시도해주세요.")));

            AccessLogger.log(request, response);
            return;
        }

        //권한 체크
        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(User user){
        List<SimpleGrantedAuthority> roles = Collections.singletonList((new SimpleGrantedAuthority(user.getRole().name())));
    	return new UsernamePasswordAuthenticationToken(user, null, roles);
    }

}
