package com.backend.promoquoter.infrastructure.adapter.in.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartQuoteRequestDto(
        @NotEmpty List<ItemDto> items,
        @NotNull String customerSegment
) {
    public record ItemDto(@NotNull String productId, int qty) {}
}

