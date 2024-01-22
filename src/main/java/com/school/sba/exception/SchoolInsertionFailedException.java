package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchoolInsertionFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;

}
