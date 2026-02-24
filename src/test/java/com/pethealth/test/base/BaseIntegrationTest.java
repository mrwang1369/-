package com.pethealth.test.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.dto.AuthResponseDTO;
import com.pethealth.test.util.TestAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Integration Test Base Class
 * Provides common test setup and authentication support
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
    "logging.level.com.pethealth=DEBUG",
    "logging.level.org.springframework.web=DEBUG"
})
@Transactional // Ensure test data does not pollute database
public abstract class BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestAuthUtil testAuthUtil;

    protected String testUserToken;
    protected Long testUserId;

    /**
     * Test preparation: Get test user authentication information
     */
    public void setUp() {
        try {
            logger.info("=== Starting test preparation ===");
            
            // Get test user token
            AuthResponseDTO authResponse = testAuthUtil.registerAndLogin(
                    "13900139001",
                    "123456",
                    "Integration Test User"
            );
            
            this.testUserToken = authResponse.getAccessToken();
            this.testUserId = authResponse.getUserInfo().getUserId();
            
            logger.info("Test user preparation completed: userId={}, token={}",
                    testUserId,
                    testUserToken != null ? testUserToken.substring(0, 20) + "..." : "null");
                    
        } catch (Exception e) {
            logger.error("Test preparation failed", e);
            throw new RuntimeException("Test environment initialization failed", e);
        }
    }

    /**
     * Create authenticated HTTP headers
     */
    protected HttpHeaders createAuthHeaders() {
        return testAuthUtil.createAuthHeaders(testUserToken);
    }

    /**
     * Create authenticated HttpEntity
     */
    protected <T> HttpEntity<T> createAuthEntity(T body) {
        return testAuthUtil.createAuthEntity(testUserToken, body);
    }

    /**
     * Execute GET request
     */
    protected <T> ResponseEntity<T> doGet(String url, Class<T> responseType) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                responseType
        );
    }

    /**
     * Execute POST request
     */
    protected <T, R> ResponseEntity<R> doPost(String url, T requestBody, Class<R> responseType) {
        return restTemplate.postForEntity(
                url,
                createAuthEntity(requestBody),
                responseType
        );
    }

    /**
     * Execute PUT request
     */
    protected <T, R> ResponseEntity<R> doPut(String url, T requestBody, Class<R> responseType) {
        return restTemplate.exchange(
                url,
                HttpMethod.PUT,
                createAuthEntity(requestBody),
                responseType
        );
    }

    /**
     * Execute DELETE request
     */
    protected ResponseEntity<Void> doDelete(String url) {
        return restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(createAuthHeaders()),
                Void.class
        );
    }

    /**
     * Validate token validity
     */
    protected boolean isTokenValid() {
        return testAuthUtil.validateToken(testUserToken);
    }

    /**
     * Refresh test token
     */
    protected void refreshTestToken() {
        try {
            AuthResponseDTO authResponse = testAuthUtil.login("13900139001", "123456");
            this.testUserToken = authResponse.getAccessToken();
            this.testUserId = authResponse.getUserInfo().getUserId();
            logger.info("Token refresh successful");
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new RuntimeException("Token refresh failed", e);
        }
    }
}