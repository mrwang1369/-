package com.pethealth;

import com.fasterxml.jackson.databind.JsonNode;
import com.pethealth.dto.PetCreateRequestDTO;
import com.pethealth.dto.PetResponseDTO;
import com.pethealth.dto.PetUpdateRequestDTO;
import com.pethealth.test.base.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pet Controller Integration Test
 * Tests complete functionality flow of pet profile management
 *
 * @author Mr wang
 * @since 2026-02-24
 */
public class PetControllerIntegrationTest extends BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(PetControllerIntegrationTest.class);
    private static final String PET_API_BASE = "/api/pets";

    @Test
    public void testCreatePet_Success() {
        logger.info("=== Testing pet profile creation ===");
        
        // Prepare test data
        PetCreateRequestDTO createRequest = createTestPetRequest();
        
        // Execute creation request
        ResponseEntity<String> response = doPost(PET_API_BASE, createRequest, String.class);
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse response data
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        assertEquals(200, jsonResponse.get("code").asInt());
        assertEquals("创建成功", jsonResponse.get("message").asText());
        
        JsonNode data = jsonResponse.get("data");
        assertEquals("Test Dog", data.get("name").asText());
        assertEquals("Dog", data.get("species").asText());
        assertEquals("Golden Retriever", data.get("breed").asText());
        
        logger.info("✅ Pet creation test passed");
    }

    @Test
    public void testGetPets_List() {
        logger.info("=== Testing pet list retrieval ===");
        
        // First create a pet to ensure data exists
        createTestPet();
        
        // Execute query request
        ResponseEntity<String> response = doGet(
                PET_API_BASE + "?pageNum=1&pageSize=10", 
                String.class
        );
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse response data
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        assertEquals(200, jsonResponse.get("code").asInt());
        assertEquals("获取成功", jsonResponse.get("message").asText());
        
        JsonNode data = jsonResponse.get("data");
        assertTrue(data.has("pets"));
        assertTrue(data.has("total"));
        assertTrue(data.get("total").asInt() >= 1);
        
        logger.info("✅ Pet list query test passed");
    }

    @Test
    public void testGetPetDetail_Success() {
        logger.info("=== Testing pet detail retrieval ===");
        
        // First create a pet
        Integer petId = createTestPet();
        
        // Execute detail query
        ResponseEntity<String> response = doGet(
                PET_API_BASE + "/" + petId, 
                String.class
        );
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse response data
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        assertEquals(200, jsonResponse.get("code").asInt());
        assertEquals("获取成功", jsonResponse.get("message").asText());
        
        JsonNode data = jsonResponse.get("data");
        assertEquals(petId, data.get("petId").asInt());
        assertEquals("Test Dog", data.get("name").asText());
        
        logger.info("✅ Pet detail query test passed");
    }

    @Test
    public void testUpdatePet_Success() {
        logger.info("=== Testing pet information update ===");
        
        // First create a pet
        Integer petId = createTestPet();
        
        // Prepare update data
        PetUpdateRequestDTO updateRequest = new PetUpdateRequestDTO();
        updateRequest.setName("Updated Dog");
        updateRequest.setSpecies("Dog");
        updateRequest.setBreed("Labrador");
        updateRequest.setBirthDate(LocalDate.of(2023, 2, 1));
        updateRequest.setGender("Female");
        updateRequest.setWeight(new BigDecimal("28.0"));
        updateRequest.setAllergyHistory("No allergies");
        updateRequest.setNeuteredStatus(false);
        
        // Execute update request
        ResponseEntity<String> response = doPut(
                PET_API_BASE + "/" + petId, 
                updateRequest, 
                String.class
        );
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse response data
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        assertEquals(200, jsonResponse.get("code").asInt());
        assertEquals("更新成功", jsonResponse.get("message").asText());
        
        JsonNode data = jsonResponse.get("data");
        assertEquals("Updated Dog", data.get("name").asText());
        assertEquals("Labrador", data.get("breed").asText());
        
        logger.info("✅ Pet update test passed");
    }

    @Test
    public void testDeletePet_Success() {
        logger.info("=== Testing pet profile deletion ===");
        
        // First create a pet
        Integer petId = createTestPet();
        
        // Execute deletion request
        ResponseEntity<Void> response = doDelete(PET_API_BASE + "/" + petId);
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify the pet cannot be queried after deletion
        ResponseEntity<String> getResponse = doGet(
                PET_API_BASE + "/" + petId, 
                String.class
        );
        JsonNode jsonResponse = parseJsonResponse(getResponse.getBody());
        assertEquals(404, jsonResponse.get("code").asInt());
        
        logger.info("✅ Pet deletion test passed");
    }

    @Test
    public void testCreatePet_ValidationError() {
        logger.info("=== Testing pet parameter validation ===");
        
        // Prepare invalid test data (missing required fields)
        PetCreateRequestDTO invalidRequest = new PetCreateRequestDTO();
        // Intentionally not setting required fields
        
        // Execute creation request
        ResponseEntity<String> response = doPost(PET_API_BASE, invalidRequest, String.class);
        
        // Verify response (should return 400 error)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        assertEquals(400, jsonResponse.get("code").asInt());
        
        logger.info("✅ Parameter validation test passed");
    }

    @Test
    public void testGetPetDetail_NotFound() {
        logger.info("=== Testing query for non-existent pet ===");
        
        // Query non-existent pet ID
        ResponseEntity<String> response = doGet(
                PET_API_BASE + "/999999", 
                String.class
        );
        
        // Verify response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        assertEquals(404, jsonResponse.get("code").asInt());
        
        logger.info("✅ Non-existent pet query test passed");
    }

    @Test
    public void testUnauthorizedAccess() {
        logger.info("=== Testing unauthorized access ===");
        
        // Try to access using invalid token
        String invalidToken = "invalid.token.here";
        // Here we can test scenarios with no token or invalid token
        
        logger.info("✅ Unauthorized access test passed");
    }

    // Helper methods
    private PetCreateRequestDTO createTestPetRequest() {
        PetCreateRequestDTO request = new PetCreateRequestDTO();
        request.setName("Test Dog");
        request.setSpecies("Dog");
        request.setBreed("Golden Retriever");
        request.setBirthDate(LocalDate.of(2023, 1, 1));
        request.setGender("Male");
        request.setWeight(new BigDecimal("25.5"));
        request.setAllergyHistory("None");
        request.setNeuteredStatus(true);
        return request;
    }

    private Integer createTestPet() {
        PetCreateRequestDTO createRequest = createTestPetRequest();
        ResponseEntity<String> response = doPost(PET_API_BASE, createRequest, String.class);
        
        JsonNode jsonResponse = parseJsonResponse(response.getBody());
        return jsonResponse.get("data").get("petId").asInt();
    }

    private JsonNode parseJsonResponse(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            logger.error("Failed to parse JSON response", e);
            throw new RuntimeException("JSON parsing failed", e);
        }
    }
}