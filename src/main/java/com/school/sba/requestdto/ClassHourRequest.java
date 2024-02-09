package com.school.sba.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassHourRequest {

	private int classHourId;
	private int subjectId;
	private int roomNo;
	private int userId;

}
