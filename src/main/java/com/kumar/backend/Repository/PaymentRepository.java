package com.kumar.backend.Repository;

import com.kumar.backend.Model.PaymentDetails;
import com.kumar.backend.Model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentOrder, Long> {
}
