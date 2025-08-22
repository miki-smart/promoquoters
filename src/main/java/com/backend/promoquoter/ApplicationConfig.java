package com.backend.promoquoter;

import com.backend.promoquoter.application.port.out.IProductRepository;
import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.adapter.ProductJpaAdapter;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.adapter.PromotionJpaAdapter;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.ProductJpaRepository;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.PromotionJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public IProductRepository productRepository(ProductJpaRepository productJpaRepository) {
        return new ProductJpaAdapter(productJpaRepository);
    }

    @Bean
    public IPromotionRepository promotionRepository(PromotionJpaRepository promotionJpaRepository) {
        return new PromotionJpaAdapter(promotionJpaRepository);
    }
}
