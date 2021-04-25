package com.bsep12.bsep.service;

import com.bsep12.bsep.dto.UserDTO;
import com.bsep12.bsep.model.Authority;
import com.bsep12.bsep.model.User;
import com.bsep12.bsep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthorityService authService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}

	public User findByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}

	public User findByToken(String token) throws UsernameNotFoundException {
		return userRepository.findByToken(token);
	}

	public User save(UserDTO userDTO) {
		User u = new User();
		u.setUsername(userDTO.getUsername());
		//TODO add salt
		u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		u.setEnabled(false);
		u.setToken(UUID.randomUUID().toString());
		u.setRole("USER");

		List<Authority> auth = authService.findByName("ROLE_USER");
		u.setRoles(auth);

		u = userRepository.save(u);
		return u;
	}

	public User change(User user) {
		userRepository.save(user);
		return user;
	}
}
