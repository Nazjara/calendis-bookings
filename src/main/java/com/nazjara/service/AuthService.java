package com.nazjara.service;

import com.nazjara.config.BrowserHeadersHolder;
import com.nazjara.dto.AuthDto;
import com.nazjara.exception.AuthenticationException;
import com.nazjara.model.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private static final String LOGIN_PATH = "/api/login";
  private static final String CLIENT_SESSION_COOKIE = "client_session";

  private final RestClient restClient;
  private final BrowserHeadersHolder headersHolder;

  private AuthDto authDto;

  /**
   * Authenticates a user with the Calendis API and stores the session token.
   *
   * @param loginRequest the login request containing email, password, and remember flag
   * @return the authentication details including the session token
   * @throws AuthenticationException if authentication fails or session token cannot be extracted
   */
  public AuthDto login(LoginRequest loginRequest) {
    log.info("Attempting to login with email: {}", loginRequest.getEmail());

    var response = restClient.post()
        .uri(LOGIN_PATH)
        .headers(httpHeaders -> headersHolder.getHeaders().forEach(httpHeaders::add))
        .contentType(MediaType.APPLICATION_JSON)
        .body(loginRequest)
        .retrieve()
        .toBodilessEntity();

    var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

    if (cookies == null || cookies.isEmpty()) {
      log.error("No cookies found in response");
      throw new AuthenticationException("No cookies found in response");
    }

    for (var cookie : cookies) {
      if (cookie.contains(CLIENT_SESSION_COOKIE)) {
        var sessionValue = extractSessionValue(cookie);
        authDto = new AuthDto(sessionValue);
        log.info("Successfully logged in and obtained session: {}", sessionValue);
        return authDto;
      }
    }

    log.error("Failed to extract client_session from response cookies");
    throw new AuthenticationException("Failed to extract client_session from response cookies");
  }

  /**
   * Retrieves the current authentication details.
   *
   * @return the authentication details including the session token
   * @throws AuthenticationException if the user is not authenticated (login has not been called)
   */
  public AuthDto getAuthDto() {
    if (authDto == null) {
      throw new AuthenticationException("Not authenticated. Please login first.");
    }
    return authDto;
  }

  private String extractSessionValue(String cookie) {
    var parts = cookie.split(";");

    for (var part : parts) {
      var trimmed = part.trim();
      if (trimmed.startsWith(CLIENT_SESSION_COOKIE + "=")) {
        return trimmed.substring((CLIENT_SESSION_COOKIE + "=").length());
      }
    }

    throw new AuthenticationException(
        "Failed to extract client_session value from cookie: " + cookie);
  }
}