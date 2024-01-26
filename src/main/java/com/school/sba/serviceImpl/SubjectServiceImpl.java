package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.repository.IAcademicProgramRepository;
import com.school.sba.repository.ISubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.ISubjectService;
import com.school.sba.util.ResponseStructure;


@Service
public class SubjectServiceImpl implements ISubjectService{

	@Autowired
	private ISubjectRepository subjectRepository;

	@Autowired
	private IAcademicProgramRepository academicProgramRepository;

	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;
	
	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;

	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubject(int programId, SubjectRequest subjectRequest) {
		return academicProgramRepository.findById(programId)
				.map(academicProgram -> {
					List<Subject> listOfSubjects = (academicProgram.getListOfSubject() != null) ? academicProgram.getListOfSubject() : new ArrayList<Subject>();


					// to add the new project that are specified by the client
					subjectRequest.getSubjectNames().forEach(name -> {
						boolean isPresent = false;
						for(Subject subject : listOfSubjects) {
							isPresent = (name.equalsIgnoreCase(subject.getSubjectName())) ? true : false;
							if(isPresent) break;
						}
						if(!isPresent) {
							listOfSubjects.add(subjectRepository.findBySubjectName(name)
									.orElseGet(() -> subjectRepository.save(Subject.builder().subjectName(name).build())));
						}
					});


					//to remove the subject that are not specified by the client
					List<Subject> toBeRemoved = new ArrayList<Subject>();
					listOfSubjects.forEach(subject -> {
						boolean isPresent = false;
						for(String name : subjectRequest.getSubjectNames()) {
							isPresent = (subject.getSubjectName().equalsIgnoreCase(name)) ? true : false;
							if(isPresent) break;
						}
						if(!isPresent) toBeRemoved.add(subject);
					});

					listOfSubjects.removeAll(toBeRemoved);

					academicProgram.setListOfSubject(listOfSubjects);

					academicProgramRepository.save(academicProgram);

					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMessage("subjects have been updated successfully");
					structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));

					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);

				})
				.orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubject(int programId,
			SubjectRequest subjectRequest) {
		
		return academicProgramRepository.findById(programId)
		.map(academicProgram -> {
			
			List<Subject> listOfSubjects = new ArrayList<Subject>();
			
			List<Subject> listOfSubjectsFromDB = subjectRepository.findAll();
			
			List<String> subjectNames = subjectRequest.getSubjectNames();
			 
			Set<String> setOfSubjectNames = new HashSet<String>();

			subjectNames.forEach(name -> {
				setOfSubjectNames.add(name.toLowerCase());
			});
			
			listOfSubjectsFromDB.forEach(sub -> {
				
				boolean check = setOfSubjectNames.add(sub.getSubjectName().toLowerCase());
				if(check) {
					listOfSubjects.add(sub);
				}
				else {
					setOfSubjectNames.remove(sub.getSubjectName().toLowerCase());
				}
				
			});

            listOfSubjectsFromDB.forEach(sub -> {
                subjectNames.forEach(name -> {
                    boolean b = sub.getSubjectName().toLowerCase().equals(name.toLowerCase());

                    if(b) {
                    	listOfSubjects.add(sub);
                    }
                    if(b == false){
                        setOfSubjectNames.remove(sub.getSubjectName().toLowerCase());
                    }

                });
            });

			setOfSubjectNames.forEach(name -> {
				Subject subject = new Subject();
				subject.setSubjectName(name);
				listOfSubjects.add(subject);
				subjectRepository.save(subject);
			});
			System.out.println(setOfSubjectNames);
			
			academicProgram.setListOfSubject(listOfSubjects);
			academicProgramRepository.save(academicProgram);
			
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("subjects have been updated successfully");
			structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));
			
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
			
		})
		.orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));
	
	}
	


}