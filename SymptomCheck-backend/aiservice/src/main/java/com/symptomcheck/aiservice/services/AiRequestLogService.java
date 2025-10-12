package com.symptomcheck.aiservice.services;


import com.symptomcheck.aiservice.repositories.AiRequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiRequestLogService {
    private AiRequestLogRepository  aiRequestLogRepository;
}
