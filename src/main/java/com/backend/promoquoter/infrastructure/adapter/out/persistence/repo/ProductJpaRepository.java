package com.backend.promoquoter.infrastructure.adapter.out.persistence.repo;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByIdIn(List<UUID> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id in :ids")
    List<ProductEntity> findByIdInForUpdate(@Param("ids") List<UUID> ids);
}
