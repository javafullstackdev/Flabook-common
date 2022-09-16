package com.msquare.flabook.exception;

import lombok.Getter;
import com.msquare.flabook.common.controllers.CommonResponseCode;

@Getter
public class CommonException extends RuntimeException {

	private final int code;
	private final String message;
	private final transient Object data;

	public CommonException(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

	public CommonException(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public CommonException(CommonResponseCode commonResponseCode, Object resultData) {
		this.code = commonResponseCode.getResultCode();
		this.message = commonResponseCode.getResultMessage();
		this.data = resultData;
	}
}
