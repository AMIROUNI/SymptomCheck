package com.symptomcheck.paymentservice.services;

import com.symptomcheck.paymentservice.repositories.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private   final PaymentTransactionRepository paymentTransactionRepository;
}
