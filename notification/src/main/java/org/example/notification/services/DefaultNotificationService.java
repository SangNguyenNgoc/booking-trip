package org.example.notification.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.notification.dtos.Account;
import org.example.notification.dtos.AccountCreatedGG;
import org.example.notification.dtos.ForgotPassword;
import org.example.notification.dtos.PaymentNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class DefaultNotificationService {
    private final MailService mailService;
    private final TemplateEngine templateEngine;

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

    @KafkaListener(topics = "AccountCreatedGGNotified")
    public void sendPassword(AccountCreatedGG accountCreatedGG){
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                    "fullName", accountCreatedGG.getFullName(),
                    "email", accountCreatedGG.getEmail(),
                    "password", accountCreatedGG.getPassword()
            ));
            String text = templateEngine.process("createAccount", context);
            mailService.sendEmailHtml(accountCreatedGG.getEmail(), "Thông tin đăng nhập", text);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "PaymentNotification")
    public void sendPayment(PaymentNotification paymentNotification){
        try {
            Context context = new Context();
            var total = paymentNotification.getTotalPrice();
            var bills = new ArrayList<>(List.of(paymentNotification));
            if (paymentNotification.getRoundTrip() != null){
                total += paymentNotification.getRoundTrip().getTotalPrice();
                bills.add(paymentNotification.getRoundTrip());
            }
            context.setVariables(Map.of(
                    "bills", bills,
                    "email", paymentNotification.getPassengerEmail(),
                    "name", paymentNotification.getPassengerName(),
                    "phone", paymentNotification.getPassengerPhone(),
                    "total", total
            ));
            String text = templateEngine.process("paymentNotification", context);
            mailService.sendEmailHtml(paymentNotification.getPassengerEmail(), "Thông tin thanh toán", text);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "ForgotPassword")
    public void forgotPassword(ForgotPassword forgotPassword){
        try {
            Context context = new Context();

            context.setVariables(Map.of(
                    "url", forgotPassword.getVerify(),
                    "email", forgotPassword.getEmail()
            ));
            String text = templateEngine.process("forgotPassword", context);
            mailService.sendEmailHtml(forgotPassword.getEmail(), "Quên mật khẩu", text);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "NotificationBillIsBooked")
    public void billBooked(PaymentNotification paymentNotification){
        try {
            Context context = new Context();
            var total = paymentNotification.getTotalPrice();
            var bills = new ArrayList<>(List.of(paymentNotification));
            if (paymentNotification.getRoundTrip() != null){
                total += paymentNotification.getRoundTrip().getTotalPrice();
                bills.add(paymentNotification.getRoundTrip());
            }
            context.setVariables(Map.of(
                    "bills", bills,
                    "email", paymentNotification.getPassengerEmail(),
                    "name", paymentNotification.getPassengerName(),
                    "phone", paymentNotification.getPassengerPhone(),
                    "total", total,
                    "url", paymentNotification.getPaymentUrl()
            ));
            String text = templateEngine.process("booked", context);
            mailService.sendEmailHtml(paymentNotification.getPassengerEmail(), "Thông tin thanh toán", text);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
