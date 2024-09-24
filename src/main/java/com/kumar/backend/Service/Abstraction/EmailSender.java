package com.kumar.backend.Service.Abstraction;

import jakarta.mail.MessagingException;
import org.springframework.mail.javamail.JavaMailSender;

public interface EmailSender {

    void sendVerificationOtpEmail(String email, String otp) throws MessagingException;

}
