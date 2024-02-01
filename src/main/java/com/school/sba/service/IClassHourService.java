package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;

public interface IClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHourForProgram(int programId);
	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> assignPeriods(List<ClassHourRequest> classHourRequest);
	

}