package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.CreatePromotionUseCase;
import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.domain.model.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Promotion Application Service Test")
class PromotionApplicationServiceTest {
    @Mock
    private IPromotionRepository promotionRepository;
    private PromotionApplicationService promotionApplicationService;

    @BeforeEach
    void setUp() {
        promotionApplicationService = new PromotionApplicationService(promotionRepository);
    }

    @Test
    @DisplayName("Should create a promotion successfully")
    void shouldCreatePromotionSuccessfully() {
        Promotion promotion=new Promotion(UUID.randomUUID(),"PERCENT_OFF_CATEGORY",Map.of("category", "Electronics", "percent", 10),1,true);
        when(promotionRepository.savePromotion(any(Promotion.class))).thenReturn(promotion);

        Map<String, Object> config = Map.of("category", "Electronics", "percent", 10);
        CreatePromotionUseCase.CreatePromotionCommand command = new CreatePromotionUseCase.CreatePromotionCommand("PERCENT_OFF_CATEGORY", config, 1, true);
        // Add assertions and verifications here

        //When
       var promotions= promotionApplicationService.createPromotions(List.of(command));

       //Then
assertNotNull(promotions);
assertEquals(1, promotions.size());
assertEquals("PERCENT_OFF_CATEGORY", promotions.get(0).type());
assertEquals(config, promotions.get(0).config());
assertEquals(1, promotions.get(0).priority());
assertEquals(true, promotions.get(0).active());
    }
    @Test
    @DisplayName("Should validate promotion type is supported")
    void shouldValidatePromotionTypeIsSupported() {
        // Given
        CreatePromotionUseCase.CreatePromotionCommand commandWithInvalidType =
                new CreatePromotionUseCase.CreatePromotionCommand(
                        "INVALID_PROMOTION_TYPE",
                        Map.of("percentage", "10.0"),
                        1,
                        true
                );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> promotionApplicationService.createPromotions(List.of(commandWithInvalidType)));

        verify(promotionRepository, never()).savePromotion(any(Promotion.class));
    }
    @Test
    @DisplayName("Should throw exception for invalid promotion type")
    void shouldThrowExceptionForInvalidPromotionType() {

        //Given
        Map<String, Object> config = Map.of("category", "Electronics", "percent", 10);

        CreatePromotionUseCase.CreatePromotionCommand command = new CreatePromotionUseCase.CreatePromotionCommand("INVALID_TYPE", config, 1, true);


        //When & Then
       try {
           promotionApplicationService.createPromotions(List.of(command));
       } catch (IllegalArgumentException e) {
           assertEquals("Unknown promotion type: INVALID_TYPE. Valid types: [PERCENT_OFF_CATEGORY, BUY_X_GET_Y]", e.getMessage());
       }

    }
    @Test
    @DisplayName("Should validate promotion type is not null or empty")
    void shouldValidatePromotionTypeIsNotNullOrEmpty() {
        // Given
        CreatePromotionUseCase.CreatePromotionCommand commandWithNullType =
                new CreatePromotionUseCase.CreatePromotionCommand(
                        null,
                        Map.of("percentage", "10.0"),
                        1,
                        true
                );

        CreatePromotionUseCase.CreatePromotionCommand commandWithEmptyType =
                new CreatePromotionUseCase.CreatePromotionCommand(
                        "",
                        Map.of("percentage", "10.0"),
                        1,
                        true
                );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> promotionApplicationService.createPromotions(List.of(commandWithNullType)));

        assertThrows(IllegalArgumentException.class,
                () -> promotionApplicationService.createPromotions(List.of(commandWithEmptyType)));

        verify(promotionRepository, never()).savePromotion(any(Promotion.class));
    }
}
