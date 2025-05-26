package com.example.skala.common;

import com.example.skala.config.Error;
import lombok.Getter;

@Getter
public class ResponseException extends RuntimeException {
	private final int code;

	public ResponseException(Error error) {
		this(error.getCode(), error.getMessage());
	}

	public ResponseException(int code, String message) {
		super(message);
		this.code = code;
	}

}
