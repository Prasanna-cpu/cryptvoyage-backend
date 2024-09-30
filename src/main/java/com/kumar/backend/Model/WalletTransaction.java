package com.kumar.backend.Model;

import com.kumar.backend.Utils.Enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="wallet_transaction")
public class WalletTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Wallet wallet;

    private WalletTransactionType type;

    private LocalDate date;

    private String transferId;

    private String purpose;

    private Long amount;



}
