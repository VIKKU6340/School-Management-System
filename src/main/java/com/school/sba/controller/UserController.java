package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.lUserService;
import com.school.sba.util.ResponseStructure;



@RestController
public class UserController {
	
	@Autowired
	private lUserService userService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(@RequestBody UserRequest userRequest) {
		ResponseEntity<ResponseStructure<UserResponse>> rs = userService.saveUser(userRequest);
		return rs;
	}
	
	@GetMapping("users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> findUser(@PathVariable Integer userId){
		
		ResponseEntity<ResponseStructure<UserResponse>> findUser = null;

		findUser = userService.findUser(userId);
		return findUser;
	}
	
	@DeleteMapping("users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(@PathVariable Integer userId){
		ResponseEntity<ResponseStructure<UserResponse>> deleteUser = null;
		
		deleteUser = userService.softDeleteUser(userId);
		
		return deleteUser;
	}
	
	@PutMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> updateUser(@PathVariable("userId") int userId,
			@RequestBody UserRequest userRequest){
		return userService.updateUser(userId, userRequest);
	}

	
	@PutMapping(" /academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToProgram(@PathVariable Integer programId,
			@PathVariable Integer userId){
		ResponseEntity<ResponseStructure<UserResponse>> AssignUser=null;;
		AssignUser = userService.addUserToProgram(programId,userId);
		return AssignUser;
			
	}
}
