package com.kumar.backend.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.kumar.backend.Utils.Enums.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(name = "email" , nullable = false ,unique = true)
    private String email;

    @Column(name = "mobile" , nullable = false ,unique = true)
    private String mobile;

    @Column(name = "password" , nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role=USER_ROLE.ROLE_CUSTOMER;

    @Embedded
    private TwoFactorAuthentication twoFactorAuthentication=new TwoFactorAuthentication();



}
