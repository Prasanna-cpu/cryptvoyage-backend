package com.kumar.backend.Model;

import com.kumar.backend.Utils.Enums.PaymentMethod;
import com.kumar.backend.Utils.Enums.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_order")
public class PaymentOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private PaymentOrderStatus status;


    private PaymentMethod paymentMethod;

    @ManyToOne
    private User user;

}
