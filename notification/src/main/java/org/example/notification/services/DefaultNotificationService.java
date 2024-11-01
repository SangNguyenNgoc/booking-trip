package org.example.notification.services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.notification.dtos.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class DefaultNotificationService {
    private final MailService mailService;
    private final TemplateEngine templateEngine;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${url.auth-url}")
    private String authUri;

    @Value("${url.verify-url}")
    private String verifyUrl;

    @KafkaListener(topics = "VerifyAccount")
    public void sendToVerify(Account user) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                    "name", user.getFullName(),
                    "url", authUri + verifyUrl + "?t=" + user.getVerifyToken()
            ));
            String text = templateEngine.process("mail", context);
            mailService.sendEmailHtml(user.getEmail(), "Xác minh địa chỉ email của bạn", text);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
