package com.snaplink.api.repository;

import com.snaplink.api.domain.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {

    long countByUrlId(Long urlId);
}
