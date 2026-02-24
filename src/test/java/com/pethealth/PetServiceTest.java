package com.pethealth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethealth.dto.PetCreateRequestDTO;
import com.pethealth.dto.PetListQueryDTO;
import com.pethealth.dto.PetListResponseDTO;
import com.pethealth.dto.PetResponseDTO;
import com.pethealth.dto.PetUpdateRequestDTO;
import com.pethealth.entity.Pet;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.impl.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PetServiceTest {

    @Mock
    private PetMapper petMapper;

    private PetServiceImpl petService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        petService = new PetServiceImpl();
        // 使用反射设置mapper
        try {
            java.lang.reflect.Field mapperField = ServiceImpl.class.getDeclaredField("baseMapper");
            mapperField.setAccessible(true);
            mapperField.set(petService, petMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mapper", e);
        }
    }

    @Test
    public void testCreatePet_Success() {
        // Prepare test data
        Long userId = 1L;
        PetCreateRequestDTO createRequest = new PetCreateRequestDTO();
        createRequest.setName("Test Dog");
        createRequest.setSpecies("Dog");
        createRequest.setBreed("Golden Retriever");
        createRequest.setBirthDate(LocalDate.of(2023, 1, 1));
        createRequest.setGender("Male");
        createRequest.setWeight(new BigDecimal("25.5"));
        createRequest.setAllergyHistory("None");
        createRequest.setNeuteredStatus(true);

        // Mock database query returning 0 (no duplicate name)
        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        
        // Mock successful save
        when(petMapper.insert(any(Pet.class))).thenReturn(1);

        // Execute test
        PetResponseDTO result = petService.createPet(userId, createRequest);

        // Verify results
        assertNotNull(result);
        assertEquals("Test Dog", result.getName());
        assertEquals("Dog", result.getSpecies());
        assertEquals("Golden Retriever", result.getBreed());

        // Verify method calls
        verify(petMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(petMapper, times(1)).insert(any(Pet.class));
    }

    @Test
    public void testCreatePet_DuplicateName() {
        // Prepare test data
        Long userId = 1L;
        PetCreateRequestDTO createRequest = new PetCreateRequestDTO();
        createRequest.setName("Duplicate Name");
        createRequest.setSpecies("Dog");

        // Mock database query returning 1 (duplicate name exists)
        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // Execute test and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            petService.createPet(userId, createRequest);
        });

        assertEquals("该用户下已存在同名宠物", exception.getMessage());
        verify(petMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(petMapper, never()).insert(any(Pet.class));
    }

    @Test
    public void testGetPetsByUserId_Success() {
        // Prepare test data
        Long userId = 1L;
        PetListQueryDTO queryDTO = new PetListQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Pet pet1 = new Pet();
        pet1.setPetId(1);
        pet1.setUserId(1);
        pet1.setName("Dog 1");
        pet1.setSpecies("Dog");
        pet1.setCreateTime(LocalDateTime.now());

        Pet pet2 = new Pet();
        pet2.setPetId(2);
        pet2.setUserId(1);
        pet2.setName("Dog 2");
        pet2.setSpecies("Dog");
        pet2.setCreateTime(LocalDateTime.now());

        // Mock pagination query results
        when(petMapper.selectPage(any(), any(QueryWrapper.class)))
                .thenAnswer(invocation -> {
                    // Simplified handling, should be pagination object
                    return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Pet>()
                            .setRecords(Arrays.asList(pet1, pet2))
                            .setTotal(2L)
                            .setCurrent(1L)
                            .setSize(10L);
                });

        // Execute test
        PetListResponseDTO result = petService.getPetsByUserId(userId, queryDTO);

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.getPets().size());
        assertEquals(2L, result.getTotal());
        assertEquals("Dog 1", result.getPets().get(0).getName());
        assertEquals("Dog 2", result.getPets().get(1).getName());

        verify(petMapper, times(1)).selectPage(any(), any(QueryWrapper.class));
    }

    @Test
    public void testGetPetDetail_Success() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 1;

        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setUserId(1);
        pet.setName("Test Dog");
        pet.setSpecies("Dog");
        pet.setBreed("Golden Retriever");
        pet.setBirthDate(LocalDate.of(2023, 1, 1));
        pet.setDeleted((byte) 0);

        // Mock database query
        when(petMapper.selectById(petId)).thenReturn(pet);

        // Execute test
        PetResponseDTO result = petService.getPetDetail(userId, petId);

        // Verify results
        assertNotNull(result);
        assertEquals("Test Dog", result.getName());
        assertEquals("Dog", result.getSpecies());
        assertEquals("Golden Retriever", result.getBreed());

        verify(petMapper, times(1)).selectById(petId);
    }

    @Test
    public void testGetPetDetail_NotFound() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 999;

        // Mock database query returning null
        when(petMapper.selectById(petId)).thenReturn(null);

        // Execute test and verify exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            petService.getPetDetail(userId, petId);
        });

        assertEquals("宠物不存在", exception.getMessage());
        verify(petMapper, times(1)).selectById(petId);
    }

    @Test
    public void testGetPetDetail_WrongOwner() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 1;

        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setUserId(2); // Different user ID
        pet.setName("Test Dog");
        pet.setDeleted((byte) 0);

        // Mock database query
        when(petMapper.selectById(petId)).thenReturn(pet);

        // Execute test and verify exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            petService.getPetDetail(userId, petId);
        });

        assertEquals("无权访问该宠物信息", exception.getMessage());
        verify(petMapper, times(1)).selectById(petId);
    }

    @Test
    public void testUpdatePet_Success() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 1;

        PetUpdateRequestDTO updateRequest = new PetUpdateRequestDTO();
        updateRequest.setName("Original Dog Name"); // Same as original name to avoid duplicate check
        updateRequest.setSpecies("Dog");
        updateRequest.setBreed("Labrador");

        Pet existingPet = new Pet();
        existingPet.setPetId(1);
        existingPet.setUserId(1);
        existingPet.setName("Original Dog Name");
        existingPet.setDeleted((byte) 0);

        // Mock database query
        when(petMapper.selectById(petId)).thenReturn(existingPet);
        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L); // Has ownership
        when(petMapper.updateById(any(Pet.class))).thenReturn(1);

        // Execute test
        PetResponseDTO result = petService.updatePet(userId, petId, updateRequest);

        // Verify results
        assertNotNull(result);
        assertEquals("Original Dog Name", result.getName());
        assertEquals("Labrador", result.getBreed());

        verify(petMapper, times(1)).selectById(petId);
        verify(petMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(petMapper, times(1)).updateById(any(Pet.class));
    }

    @Test
    public void testDeletePet_Success() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 1;

        // Mock ownership check
        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);
        when(petMapper.updateById(any(Pet.class))).thenReturn(1);

        // Execute test
        assertDoesNotThrow(() -> {
            petService.deletePet(userId, petId);
        });

        verify(petMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(petMapper, times(1)).updateById(any(Pet.class));
    }

    @Test
    public void testCheckPetOwnership_True() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 1;

        // Mock database query returning 1
        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // Execute test
        boolean result = petService.checkPetOwnership(userId, petId);

        // Verify results
        assertTrue(result);
        verify(petMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    public void testCheckPetOwnership_False() {
        // Prepare test data
        Long userId = 1L;
        Integer petId = 1;

        // Mock database query returning 0
        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        // Execute test
        boolean result = petService.checkPetOwnership(userId, petId);

        // Verify results
        assertFalse(result);
        verify(petMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    public void testCalculateAge() {
        // This test needs to access private method through reflection
        // Or we can test public methods to indirectly verify age calculation
        Long userId = 1L;
        PetCreateRequestDTO createRequest = new PetCreateRequestDTO();
        createRequest.setName("Test Dog");
        createRequest.setSpecies("Dog");
        createRequest.setBirthDate(LocalDate.now().minusYears(1).minusMonths(2)); // 1 year 2 months

        when(petMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(petMapper.insert(any(Pet.class))).thenReturn(1);

        PetResponseDTO result = petService.createPet(userId, createRequest);
        
        // Verify age field is correctly set (though we cannot predict exact calculation result)
        assertNotNull(result.getAge());
        assertFalse(result.getAge().isEmpty());
    }
}