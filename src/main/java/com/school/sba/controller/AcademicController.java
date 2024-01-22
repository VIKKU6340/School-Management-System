package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.IAcademicProgramService;
import com.school.sba.util.ResponseStructure;



@RestController
public class AcademicController {
	
	@Autowired
	private IAcademicProgramService academicProgramService;
	
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> createProgram(@PathVariable("schoolId") int schoolId,
			@RequestBody AcademicProgramRequest academicProgramRequest){
		return academicProgramService.createProgram(schoolId, academicProgramRequest);
	}
	
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(@PathVariable("schoolId") int schoolId){
		return academicProgramService.findAllAcademicProgram(schoolId);
	}
	
	@PutMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubject(@PathVariable("programId") int programId,
			@RequestBody SubjectRequest subjectRequest){
		return academicProgramService.updateSubject(programId, subjectRequest);
	}
	
}