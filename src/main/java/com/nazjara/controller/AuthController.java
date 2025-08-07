package com.nazjara.controller;

import com.nazjara.dto.AuthDto;
import com.nazjara.model.request.LoginRequest;
import com.nazjara.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * Authenticates a user with the Calendis API and returns a session token.
   *
   * @param loginRequest the login request containing email, password, and remember flag
   * @return a ResponseEntity with the authentication details including the session token
   */
  @PostMapping("/login")
  public ResponseEntity<AuthDto> login(@RequestBody LoginRequest loginRequest) {
    log.info("Login request received for email: {}", loginRequest.getEmail());

    var authDto = authService.login(loginRequest);
    log.info("Login successful for email: {}", loginRequest.getEmail());
    return ResponseEntity.ok(authDto);
  }
}