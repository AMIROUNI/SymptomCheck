package com.symptomcheck.paymentservice.repositories;

import com.symptomcheck.paymentservice.models.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer>
{
}
