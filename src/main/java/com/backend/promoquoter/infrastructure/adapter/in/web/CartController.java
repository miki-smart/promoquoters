package com.backend.promoquoter.infrastructure.adapter.in.web;

import com.backend.promoquoter.application.port.in.CalculateCartQuoteUseCase;
import com.backend.promoquoter.infrastructure.adapter.in.request.CartQuoteRequestDto;
import com.backend.promoquoter.infrastructure.adapter.in.response.CartConfirmResponseDto;
import com.backend.promoquoter.infrastructure.adapter.in.response.CartQuoteResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CalculateCartQuoteUseCase calculateCartQuoteUseCase;
    public CartController(CalculateCartQuoteUseCase calculateCartQuoteUseCase) {
        this.calculateCartQuoteUseCase = calculateCartQuoteUseCase;
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
            @Valid @RequestBody CartQuoteRequestDto request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new CartConfirmResponseDto("not-implemented", List.of(), List.of(), 0.0));
    }
}
