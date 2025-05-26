package com.example.skala.common;

import com.example.skala.config.Error;
import lombok.Getter;

@Getter
public class ParameterException extends RuntimeException {
	private final int code;

	public ParameterException(String... parameters) {
		super(Error.PARAMETER_MISSED.getMessage() + ": " + StringTool.join(parameters));
		this.code = Error.PARAMETER_MISSED.getCode();
	}
}
