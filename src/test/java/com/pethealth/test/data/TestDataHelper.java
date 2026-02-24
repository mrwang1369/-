package com.pethealth.test.data;

import com.pethealth.entity.Pet;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Test Data Management Tool
 * Used to create and clean test data
 */
@Component
public class TestDataHelper {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private PetService petService;

    /**
     * Create test pet
     */
    public Pet createTestPet(Long userId) {
        Pet pet = new Pet();
        pet.setUserId(userId.intValue());
        pet.setName("Test Pet");
        pet.setSpecies("Dog");
        pet.setBreed("Golden Retriever");
        pet.setBirthDate(LocalDate.now().minusYears(1));
        pet.setGender("Male");
        pet.setWeight(new BigDecimal("25.5"));
        pet.setAllergyHistory("None");
        pet.setNeuteredStatus(true);
        
        petMapper.insert(pet);
        return pet;
    }

    /**
     * Clean up test data for specified user
     */
    public void cleanupTestData(Long userId) {
        // Delete all pet data for this user
        petMapper.deleteByUserId(userId);
    }

    /**
     * Clean up all test data (use with caution)
     */
    public void cleanupAllTestData() {
        // Can be used in test environment, disabled in production
        petMapper.deleteAllTestData();
    }
}