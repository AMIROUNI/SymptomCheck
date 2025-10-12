package com.symptomcheck.paymentservice.controllers;

import com.symptomcheck.paymentservice.services.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment/transaction")
@RequiredArgsConstructor
public class PaymentTransactionController {
    private final PaymentTransactionService paymentTransactionService;
}

