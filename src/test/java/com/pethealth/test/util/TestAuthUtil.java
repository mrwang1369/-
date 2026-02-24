package com.pethealth.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.dto.AuthResponseDTO;
import com.pethealth.dto.LoginRequestDTO;
import com.pethealth.dto.RegisterRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

/**
 * Test Authentication Utility
 * Used to handle user authentication flows in integration tests
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Component
public class TestAuthUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestAuthUtil.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String AUTH_BASE_URL = "/api/auth";

    /**
     * User registration and get authentication token
     *
     * @param phone phone number
     * @param password password
     * @param nickname nickname
     * @return authentication response DTO
     */
    public AuthResponseDTO registerAndLogin(String phone, String password, String nickname) {
        try {
            // 1. User registration
            RegisterRequestDTO registerRequest = new RegisterRequestDTO();
            registerRequest.setPhone(phone);
            registerRequest.setPassword(password);
            registerRequest.setConfirmPassword(password);
            registerRequest.setNickname(nickname);

            HttpHeaders registerHeaders = new HttpHeaders();
            registerHeaders.setContentType(MediaType.APPLICATION_JSON);
            registerHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<RegisterRequestDTO> registerEntity = new HttpEntity<>(registerRequest, registerHeaders);

            logger.info("Executing user registration: phone={}", phone);
            ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                    AUTH_BASE_URL + "/register",
                    registerEntity,
                    String.class
            );

            if (registerResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("User registration failed: " + registerResponse.getStatusCode());
            }

            // 2. Parse registration response to get token
            AuthResponseDTO authResponse = objectMapper.readValue(
                    registerResponse.getBody(),
                    AuthResponseDTO.class
            );

            logger.info("User registration successful: userId={}, token={}", 
                    authResponse.getUserInfo().getUserId(), 
                    authResponse.getAccessToken().substring(0, 20) + "...");
            
            return authResponse;

        } catch (Exception e) {
            logger.error("Registration login process failed", e);
            throw new RuntimeException("Authentication process failed: " + e.getMessage(), e);
        }
    }

    /**
     * User login to get token
     *
     * @param phone phone number
     * @param password password
     * @return authentication response DTO
     */
    public AuthResponseDTO login(String phone, String password) {
        try {
            LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setLoginType("phone");
            loginRequest.setPhone(phone);
            loginRequest.setPassword(password);

            HttpHeaders loginHeaders = new HttpHeaders();
            loginHeaders.setContentType(MediaType.APPLICATION_JSON);
            loginHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<LoginRequestDTO> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

            logger.info("Executing user login: phone={}", phone);
            ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                    AUTH_BASE_URL + "/login",
                    loginEntity,
                    String.class
            );

            if (loginResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("User login failed: " + loginResponse.getStatusCode());
            }

            AuthResponseDTO authResponse = objectMapper.readValue(
                    loginResponse.getBody(),
                    AuthResponseDTO.class
            );

            logger.info("User login successful: userId={}, token={}",
                    authResponse.getUserInfo().getUserId(),
                    authResponse.getAccessToken().substring(0, 20) + "...");

            return authResponse;

        } catch (Exception e) {
            logger.error("Login process failed", e);
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create authenticated HTTP headers
     *
     * @param token JWT token
     * @return HttpHeaders
     */
    public HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    /**
     * Create authenticated HttpEntity
     *
     * @param token JWT token
     * @param body request body
     * @param <T> request body type
     * @return HttpEntity
     */
    public <T> HttpEntity<T> createAuthEntity(String token, T body) {
        return new HttpEntity<>(body, createAuthHeaders(token));
    }

    /**
     * Get test user token (reuse existing user)
     *
     * @return authentication token
     */
    public String getTestUserToken() {
        // Use fixed test account
        String testPhone = "13900139001";
        String testPassword = "123456";
        String testNickname = "Test User";

        try {
            return login(testPhone, testPassword).getAccessToken();
        } catch (Exception e) {
            logger.info("Test user does not exist, creating new user");
            return registerAndLogin(testPhone, testPassword, testNickname).getAccessToken();
        }
    }

    /**
     * Get admin token (if admin account exists)
     *
     * @return admin token
     */
    public String getAdminToken() {
        // Admin account logic can be implemented here based on actual requirements
        return getTestUserToken(); // Temporarily use regular user token
    }

    /**
     * Validate token validity
     *
     * @param token JWT token
     * @return whether valid
     */
    public boolean validateToken(String token) {
        try {
            HttpHeaders headers = createAuthHeaders(token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    AUTH_BASE_URL + "/profile",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}