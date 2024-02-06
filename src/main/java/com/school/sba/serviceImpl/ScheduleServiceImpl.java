package com.school.sba.serviceImpl;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.exception.InvalidScheduleBreakTimeException;
import com.school.sba.exception.InvalidScheduleClassStartsException;
import com.school.sba.exception.InvalidScheduleCloseTimeException;
import com.school.sba.exception.InvalidScheduleLunchTimeException;
import com.school.sba.exception.ScheduleAlreadyPresentException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.IScheduleRepository;
import com.school.sba.repository.ISchoolRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.IScheduleService;
import com.school.sba.util.ResponseStructure;



@Service
public class ScheduleServiceImpl implements IScheduleService{

	@Autowired
	private IScheduleRepository scheduleRepository;

	@Autowired
	private ISchoolRepository schoolRepository;

	@Autowired
	private ResponseStructure<ScheduleResponse> structure;


	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {		
		return ScheduleResponse.builder()
				.scheduleId(schedule.getScheduleId())
				.opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt())
				.classHoursPerDay(schedule.getClassHoursPerDay())
				.classHoursLengthInMinutes((int)
						(Duration.ofMinutes(schedule.getClassHoursLengthInMin().toMinutes())
								.toMinutes()))
				.breakTime(schedule.getBreakTime())
				.breakLengthInMinutes(((int)
						(Duration.ofMinutes(schedule.getBreakLengthInMin().toMinutes())
								.toMinutes())))
				.lunchLengthInMinutes((int)
						(Duration.ofMinutes(schedule.getLunchLengthInMin().toMinutes())
								.toMinutes()))
				.lunchTime(schedule.getLunchTime())
				.build();
	}

	private Schedule mapToSchedule(ScheduleRequest scheduleRequest) {
		return Schedule.builder()
				.opensAt(scheduleRequest.getOpensAt())
				.closesAt(scheduleRequest.getClosesAt())
				.classHoursPerDay(scheduleRequest.getClassHoursPerDay())
				.classHoursLengthInMin(Duration.ofMinutes(scheduleRequest.getClassHoursLengthInMinutes()))
				.breakTime(scheduleRequest.getBreakTime())
				.breakLengthInMin(Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()))
				.lunchLengthInMin(Duration.ofMinutes(scheduleRequest.getLunchLengthInMinutes()))
				.lunchTime(scheduleRequest.getLunchTime())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> saveSchedule(int schoolId,
			ScheduleRequest scheduleRequest) {

		return schoolRepository.findById(schoolId)
				.map(school -> {
					if(school.getSchedule() == null) {
						LocalTime opensAt = scheduleRequest.getOpensAt();
						LocalTime closesAt = scheduleRequest.getClosesAt();
						long classHourLength = Duration.ofMinutes(scheduleRequest.getClassHoursLengthInMinutes()).toMinutes();
						long breakHourLength = Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()).toMinutes();
						long lunchHourLength = Duration.ofMinutes(scheduleRequest.getLunchLengthInMinutes()).toMinutes();
						LocalTime lunchTime = scheduleRequest.getLunchTime();
						LocalTime breakTime = scheduleRequest.getBreakTime();

						if (closesAt.isBefore(opensAt) || closesAt.isBefore(breakTime) || closesAt.isBefore(lunchTime))
							throw new InvalidScheduleCloseTimeException("Close Time must be after Open Time");

						LocalTime classStarts = null;
						for (int i = 0; i < scheduleRequest.getClassHoursPerDay() + 2; i++) {
							classStarts = opensAt;
							LocalTime classEnds = classStarts.plusMinutes(classHourLength);

							if (breakTime.isBefore(classEnds) && breakTime.isAfter(classStarts))
								throw new InvalidScheduleBreakTimeException("break time should be before Close time");
							else {
								if (breakTime.equals(classEnds)) {
									opensAt = breakTime.plusMinutes(breakHourLength);
									continue;
								}
							}

							if (lunchTime.isBefore(classEnds) && lunchTime.isAfter(classStarts))
								throw new InvalidScheduleLunchTimeException("lunch time should be before Close time");
							else {
								if (lunchTime.equals(classEnds)) {
									opensAt = lunchTime.plusMinutes(lunchHourLength);
									continue;
								}
							}

							opensAt = classEnds;
						}

						if (!classStarts.minusHours(1).equals(closesAt))
							throw new InvalidScheduleClassStartsException("Closes At is not valid for the classes");


						Schedule saveSchedule = scheduleRepository.save(mapToSchedule(scheduleRequest));
						school.setSchedule(saveSchedule);
						schoolRepository.save(school);

						structure.setStatus(HttpStatus.CREATED.value());
						structure.setMessage("schedule added successfully");
						structure.setData(mapToScheduleResponse(saveSchedule));

						return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.CREATED);
					}else {
						throw new ScheduleAlreadyPresentException("Schedule is already added");
					}
				})
				.orElseThrow(() -> new SchoolNotFoundByIdException("school not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId) {

		School school = schoolRepository.findById(schoolId)
				.orElseThrow(() -> new SchoolNotFoundByIdException("School not found"));

		return scheduleRepository.findById(school.getSchedule().getScheduleId()).map(schedule -> {
					structure.setStatus(HttpStatus.FOUND.value());
					structure.setMessage("schedule found");
					structure.setData(mapToScheduleResponse(schedule));

					return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.FOUND);
				})
				.orElseThrow(() -> new ScheduleNotFoundException("schedule not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId,
			ScheduleRequest scheduleRequest) {

		return scheduleRepository.findById(scheduleId)
				.map(schedule -> {
					Schedule mapToSchedule = mapToSchedule(scheduleRequest);
					mapToSchedule.setScheduleId(scheduleId);
					schedule = scheduleRepository.save(mapToSchedule);

					structure.setStatus(HttpStatus.OK.value());
					structure.setMessage("schedule updated successfully");
					structure.setData(mapToScheduleResponse(schedule));

					return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.OK);
				})
				.orElseThrow(() -> new ScheduleNotFoundException("schedule not found"));

	}


}