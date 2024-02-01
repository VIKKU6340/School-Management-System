package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.util.ResponseStructure;

public interface IClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHourForProgram(int programId);

}