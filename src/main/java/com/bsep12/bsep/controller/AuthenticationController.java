package com.bsep12.bsep.controller;

import com.bsep12.bsep.dto.EmailDTO;
import com.bsep12.bsep.dto.RecoveryDTO;
import com.bsep12.bsep.dto.UserDTO;
import com.bsep12.bsep.model.User;
import com.bsep12.bsep.model.UserTokenState;
import com.bsep12.bsep.security.TokenUtils;
import com.bsep12.bsep.security.auth.JwtAuthenticationRequest;
import com.bsep12.bsep.service.EmailService;
import com.bsep12.bsep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@PostMapping("/login")
	public ResponseEntity<UserTokenState> createAuthenticationToken(
			@RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {

		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authenticationRequest.getUsername(),
				authenticationRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		User user = (User) authentication.getPrincipal();
		String jwt = tokenUtils.generateToken(user.getUsername(), user.getRole());
		int expiresIn = tokenUtils.getExpiredIn();

		return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
	}

	@PostMapping("/signup")
	public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) {

		User existUser = userService.findByUsername(userDTO.getUsername());
		if (existUser != null)
			//TESTING ONLY - don't return user info
			return new ResponseEntity<>(existUser, HttpStatus.CONFLICT);

		User createdUser = userService.save(userDTO);
		try {
			emailService.sendMail(createdUser);
		} catch (MailException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	@GetMapping("/verify")
	public ResponseEntity<User> verifyUser(@RequestParam("token") String token) {
		User existUser = userService.findByToken(token);
		if (existUser == null || existUser.getExpiry().getTime() < new Date().getTime())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		existUser.setEnabled(true);
		existUser.setToken(null);
		existUser.setExpiry(null);
		userService.change(existUser);

		//TESTING ONLY
		return new ResponseEntity<>(existUser, HttpStatus.CREATED);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity forgotPassword(@RequestBody EmailDTO emailDTO) {
		User existUser = userService.findByUsername(emailDTO.getUsername());
		if (existUser == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		existUser.setToken(UUID.randomUUID().toString());
		existUser.setExpiry(new Date((new Date().getTime() + 300000)));
		userService.change(existUser);
		try {
			emailService.sendMail2(existUser);
		} catch (MailException e) {
			e.printStackTrace();
		}

		return new ResponseEntity(HttpStatus.CREATED);
	}

	@PostMapping("/reset-password")
	public ResponseEntity resetPassword(@RequestBody RecoveryDTO recoveryDTO) {
		User existUser = userService.findByToken(recoveryDTO.getToken());
		if (existUser == null || existUser.getExpiry().getTime() < new Date().getTime())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		existUser.setToken(null);
		existUser.setExpiry(null);
		userService.changePassword(existUser, recoveryDTO.getPassword());

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
