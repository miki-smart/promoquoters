package com.backend.promoquoter.infrastructure.adapter.in.web;

import com.backend.promoquoter.application.port.in.CalculateCartQuoteUseCase;
import com.backend.promoquoter.application.port.in.ConfirmCartUseCase;
import com.backend.promoquoter.infrastructure.adapter.in.request.CartQuoteRequestDto;
import com.backend.promoquoter.infrastructure.adapter.in.response.CartConfirmResponseDto;
import com.backend.promoquoter.infrastructure.adapter.in.response.CartQuoteResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CalculateCartQuoteUseCase calculateCartQuoteUseCase;
    private final ConfirmCartUseCase confirmCartUseCase;

    public CartController(CalculateCartQuoteUseCase calculateCartQuoteUseCase, ConfirmCartUseCase confirmCartUseCase) {
        this.calculateCartQuoteUseCase = calculateCartQuoteUseCase;
        this.confirmCartUseCase = confirmCartUseCase;
    }
    @PostMapping("/quote")
    public ResponseEntity<CartQuoteResponseDto> quote(@Valid @RequestBody CartQuoteRequestDto request) {
        // Map DTO to command
        CalculateCartQuoteUseCase.CalculateCartQuoteCommand command =
                new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                        request.items().stream()
                                .map(item -> new CalculateCartQuoteUseCase.CartItemCommand(
                                        item.productId(),
                                        item.qty()
                                ))
                                .toList(),
                        request.customerSegment()
                );

        // Execute use case
        CalculateCartQuoteUseCase.CartQuoteResult result = calculateCartQuoteUseCase.calculateQuote(command);

        // Map result to DTO
        CartQuoteResponseDto response = new CartQuoteResponseDto(
                result.quoteId(),
                result.lineItems().stream()
                        .map(line -> new CartQuoteResponseDto.LineItemDto(
                                line.productId(),
                                line.productName(),
                                line.quantity(),
                                line.unitPrice(),
                                line.lineDiscount(),
                                line.lineTotal()
                        ))
                        .toList(),
                result.appliedPromotions().stream()
                        .map(promo -> new CartQuoteResponseDto.AppliedPromotionDto(
                                promo.promotionId(),
                                promo.type(),
                                promo.description(),
                                promo.amount(),
                                promo.priority()
                        ))
                        .toList(),
                result.subtotal(),
                result.totalDiscount(),
                result.total()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<CartConfirmResponseDto> confirm(
            @Valid @RequestBody CartConfirmRequestDto request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        // Map DTO to command
        ConfirmCartUseCase.ConfirmCartCommand command = 
                new ConfirmCartUseCase.ConfirmCartCommand(
                        request.items().stream()
                                .map(item -> new ConfirmCartUseCase.CartItemCommand(
                                        item.productId(),
                                        item.qty()
                                ))
                                .toList(),
                        request.customerSegment(),
                        Optional.ofNullable(idempotencyKey)
                );

        // Execute use case
        ConfirmCartUseCase.CartConfirmResult result = confirmCartUseCase.confirmCart(command);

        // Map result to DTO
        CartConfirmResponseDto response = new CartConfirmResponseDto(
                result.orderId(),
                result.lineItems().stream()
                        .map(line -> new CartConfirmResponseDto.LineItemDto(
                                line.productId(),
                                line.productName(),
                                line.quantity(),
                                line.unitPrice(),
                                line.lineDiscount(),
                                line.lineTotal()
                        ))
                        .toList(),
                result.appliedPromotions().stream()
                        .map(promo -> new CartConfirmResponseDto.AppliedPromotionDto(
                                promo.promotionId(),
                                promo.type(),
                                promo.description(),
                                promo.amount(),
                                promo.priority()
                        ))
                        .toList(),
                result.subtotal(),
                result.totalDiscount(),
                result.total(),
                result.confirmedAt(),
                result.status()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
     public record CartConfirmRequestDto(
            @NotEmpty List<ItemDto> items,
            @NotNull String customerSegment
    ) {
        public record ItemDto(@NotNull String productId, int qty) {}
    }
}
