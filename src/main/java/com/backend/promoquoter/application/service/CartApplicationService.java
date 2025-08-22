package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.CalculateCartQuoteUseCase;
import com.backend.promoquoter.application.port.out.IPromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartApplicationService  implements CalculateCartQuoteUseCase {
    private final IPromotionRepository promotionRepository;

    public CartApplicationService(IPromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }
    @Override
    public CartQuoteResult calculateQuote(CalculateCartQuoteCommand command) {
        return null;
    }
}
