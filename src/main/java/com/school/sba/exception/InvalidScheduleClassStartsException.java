package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidScheduleClassStartsException extends RuntimeException {
	
	private String message;

}