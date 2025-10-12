package com.symptomcheck.aiservice.repositories;

import com.symptomcheck.aiservice.models.AiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRequestLogRepository extends JpaRepository<AiRequestLog, Integer>
{
}
