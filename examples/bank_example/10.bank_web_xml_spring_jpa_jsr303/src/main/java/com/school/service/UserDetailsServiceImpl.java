package com.school.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.school.domain.UserRole;
import com.school.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	UserRepository repository;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

		com.school.domain.User user = repository.findByUserId(username);
		//Set userRoles = user.getUserRole();
		//if(user .getUserRole().size() ==0 ) return null;
		List<GrantedAuthority> authorities = buildUserAuthority(user .getUserRole());
		return buildUserForAuthentication(user, authorities);

	}

	private User buildUserForAuthentication(com.school.domain.User user, List<GrantedAuthority> authorities) {
		return new User(user.getUserId(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
	}

	private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {
		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
		for (UserRole userRole : userRoles) { setAuths.add(new SimpleGrantedAuthority(userRole.getRole())); }
		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>( setAuths);
		return Result;
	}

}
