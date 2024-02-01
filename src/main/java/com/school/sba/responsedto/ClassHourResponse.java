package com.school.sba.responsedto;

import java.time.LocalTime;

import com.school.sba.entity.enums.ClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassHourResponse {

	private int classHourId;
	private LocalTime bginsAt;
	private LocalTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;

}