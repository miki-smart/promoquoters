package com.backend.promoquoter.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(
         UUID ProductId,
         int Quantity,
         BigDecimal Price
) {}
