package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Service.Abstraction.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class EmailSenderImplementation implements EmailSender {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendVerificationOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,"utf8");

        String subject="OTP Verification";
        String message="Your OTP is "+otp;

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(message);
        mimeMessageHelper.setTo(email);

        try{
            javaMailSender.send(mimeMessage);
        }
        catch (MailException e){
            throw new MailSendException("Unable to send email");
        }


    }
}
