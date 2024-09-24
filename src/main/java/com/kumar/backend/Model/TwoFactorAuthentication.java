package com.kumar.backend.Model;

import com.kumar.backend.Utils.Enums.VerificationType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TwoFactorAuthentication {
    private boolean isEnabled=false;

    private VerificationType sendTo;
}
