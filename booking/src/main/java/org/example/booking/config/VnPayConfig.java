package org.example.booking.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Configuration
public class VnPayConfig {
    @Value("${vn_pay.vn_pay_url}")
    public String vnp_PayUrl;

    @Value("${vn_pay.vn_pay_return_url}")
    public String vnp_ReturnUrl;

    @Value("${vn_pay.tmn_code}")
    public String vnp_TmnCode;

    @Value("${vn_pay.key}")
    public String secretKey;

    @Value("${vn_pay.time_out}")
    public Integer timeOut;


//    public String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

//    public static String md5(String message) {
//        String digest = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] hash = md.digest(message.getBytes("UTF-8"));
//            StringBuilder sb = new StringBuilder(2 * hash.length);
//            for (byte b : hash) {
//                sb.append(String.format("%02x", b & 0xff));
//            }
//            digest = sb.toString();
//        } catch (UnsupportedEncodingException ex) {
//            digest = "";
//        } catch (NoSuchAlgorithmException ex) {
//            digest = "";
//        }
//        return digest;
//    }

    public static String Sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    //Util for VNPAY
    public String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(this.secretKey, sb.toString());
    }

    public String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
