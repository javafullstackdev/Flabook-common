package com.msquare.flabook.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@ControllerAdvice({"com.msquare.flabook"})
public class CommonExceptionHandler {
	private static final ObjectMapper objectWriter = new ObjectMapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    /**
     * {
     * "timestamp": "2020-04-20T03:23:58.244+0000",
     * "status": 400,
     * "error": "Bad Request",
     * "errors": [
     * {
     * "codes": [
     * "NotEmpty.createCommentVo.text",
     * "NotEmpty.text",
     * "NotEmpty.java.lang.String",
     * "NotEmpty"
     * ],
     * "arguments": [
     * {
     * "codes": [
     * "createCommentVo.text",
     * "text"
     * ],
     * "arguments": null,
     * "defaultMessage": "text",
     * "code": "text"
     * }
     * ],
     * "defaultMessage": "반드시 값이 존재하고 길이 혹은 크기가 0보다 커야 합니다.",
     * "objectName": "createCommentVo",
     * "field": "text",
     * "rejectedValue": null,
     * "bindingFailure": false,
     * "code": "NotEmpty"
     * }
     * ],
     * "message": "Validation failed for object='createCommentVo'. Error count: 1",
     * "path": "/postings/15520/comments"
     * }
     *
     */


    @ExceptionHandler({BindException.class})
    public ResponseEntity<CommonResponse<Map<String, String>>> paramViolationError(BindException ex) {
        String errorMessage = null;
        for (FieldError error : ex.getFieldErrors()) {
            errorMessage = error.getDefaultMessage();
            if(errorMessage != null)
               break;
        }

        if(errorMessage == null)
            errorMessage = ex.getMessage();

        log.error("BindException : {}", ex.getMessage());
        return ResponseEntity.ok(new CommonResponse<>(CommonResponseCode.PARAMETER_ERROR.getResultCode(),
                null, errorMessage));
    }

  //AuthorizationContoller Parameter Validation 에러
  	@ExceptionHandler({MethodArgumentNotValidException.class})
  	public <T> ResponseEntity<CommonResponse<T>> commonViolationError(MethodArgumentNotValidException ex) {

        StringBuilder stringBuffer = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
          stringBuffer.append(error.getDefaultMessage());
        }
        log.error("MethodArgumentNotValidException : {}", ex.getMessage());

        return ResponseEntity.ok(new CommonResponse<>(CommonResponseCode.PARAMETER_ERROR.getResultCode(),
              null, stringBuffer.toString()));
  	}

    @ExceptionHandler({Exception.class})
    public <T> ResponseEntity<CommonResponse<T>> paramViolationError(Exception ex) {
    	log.error("paramViolationError", ex);
        return ResponseEntity.ok(new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, ex.getMessage()));
    }
    @ExceptionHandler({IllegalStateException.class})
    public <T> ResponseEntity<CommonResponse<T>> multipartError(IllegalStateException ex) {
        log.error("multipartError", ex);
        return ResponseEntity.ok(new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, ex.getMessage()));
    }


    //AuthorizationContoller 비즈니스 로직 에러
    @ExceptionHandler({CommonException.class})
    public ResponseEntity<CommonResponse<Object>> commonViolationError(HttpServletRequest request, CommonException ex) throws JsonProcessingException {

    	StackTraceElement[] ste = ex.getStackTrace();
        String className = ste[0].getClassName();
        String methodName = ste[0].getMethodName();
        String fileName = ste[0].getFileName();
        int lineNumber = ste[0].getLineNumber();

    	Map<String, Object> accessLogMap = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> datas = new HashMap<>();

        Enumeration<String> element = request.getHeaderNames();
        Enumeration<String> param = request.getParameterNames();

        while (element.hasMoreElements()) {
            String headerName = element.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        while(param.hasMoreElements()) {
        	String paramName = param.nextElement();
            String paramValue = request.getParameter(paramName);
            datas.put(paramName, paramValue);
        }

        accessLogMap.put("headers", headers);
        accessLogMap.put("datas", datas);
        accessLogMap.put("client", request.getServerName());
        accessLogMap.put("method", request.getMethod());
        accessLogMap.put("host", request.getRemoteHost());

        String requestWithQueryString = (request.getQueryString() == null) ? request.getRequestURI() : request.getRequestURI() + "?" + request.getQueryString();
        accessLogMap.put("uri", requestWithQueryString);

        log.error(className + "." + methodName + "(" + fileName + ":" + lineNumber + ") : {}\n{}", ex.getMessage(), objectWriter.writeValueAsString(accessLogMap));

        return ResponseEntity.ok(new CommonResponse<>(ex.getCode(), ex.getData(), ex.getMessage()));
    }

}
