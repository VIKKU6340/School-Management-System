package com.school.sba.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.school.sba.repository.IUserRepository;
import com.school.sba.service.lUserService;

@Service
public class CustomUserDetailService implements lUserService{

	@Autowired
	private IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUserName(username)
				.map(user -> new CustomUserDetail(user))
				.orElseThrow(() -> new UsernameNotFoundException("Failed to fetch the user"));
	}



}
