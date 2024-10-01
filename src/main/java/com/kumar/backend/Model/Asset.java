package com.kumar.backend.Model;

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
@Table(name = "asset")
public class Asset implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private double quantity;

    private double buyPrice;

    public Asset(User user, Coin coin, double quantity) {
        this.user = user;
        this.coin = coin;
        this.quantity = quantity;
    }

    @ManyToOne
    private Coin coin;

    @ManyToOne
    private User user;



}
