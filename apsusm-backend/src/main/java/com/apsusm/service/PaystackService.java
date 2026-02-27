package com.apsusm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaystackService {

    private static final Logger log = LoggerFactory.getLogger(PaystackService.class);

    private final WebClient paystackWebClient;
    private final ObjectMapper objectMapper;

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Value("${paystack.callback-url}")
    private String callbackUrl;

    @Value("${app.membership.fee}")
    private Long membershipFee;

    @Value("${app.membership.currency}")
    private String currency;

    public PaystackService(WebClient paystackWebClient, ObjectMapper objectMapper) {
        this.paystackWebClient = paystackWebClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Initialize a Paystack transaction for membership payment.
     */
    public Map<String, Object> initializeTransaction(String email, String reference, Map<String, String> metadata) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("amount", membershipFee);
        body.put("currency", currency);
        body.put("reference", reference);
        body.put("callback_url", callbackUrl);
        body.put("metadata", metadata);

        try {
            String response = paystackWebClient.post()
                    .uri("/transaction/initialize")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode json = objectMapper.readTree(response);
            Map<String, Object> result = new HashMap<>();
            result.put("success", json.get("status").asBoolean());

            if (json.get("status").asBoolean()) {
                JsonNode data = json.get("data");
                result.put("authorization_url", data.get("authorization_url").asText());
                result.put("access_code", data.get("access_code").asText());
                result.put("reference", data.get("reference").asText());
            } else {
                result.put("message", json.get("message").asText());
            }

            return result;
        } catch (Exception e) {
            log.error("Paystack transaction initialization failed", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Payment initialization failed: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * Verify a Paystack transaction by reference.
     */
    public Map<String, Object> verifyTransaction(String reference) {
        try {
            String response = paystackWebClient.get()
                    .uri("/transaction/verify/" + reference)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode json = objectMapper.readTree(response);
            Map<String, Object> result = new HashMap<>();
            result.put("verified", false);

            if (json.get("status").asBoolean()) {
                JsonNode data = json.get("data");
                String status = data.get("status").asText();
                result.put("status", status);
                result.put("amount", data.get("amount").asLong());
                result.put("currency", data.get("currency").asText());
                result.put("reference", data.get("reference").asText());
                result.put("transaction_id", data.get("id").asText());

                if ("success".equals(status)) {
                    result.put("verified", true);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("Paystack transaction verification failed for ref: {}", reference, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("verified", false);
            errorResult.put("message", "Verification failed: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * Validate Paystack webhook signature using HMAC SHA-512.
     */
    public boolean validateWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString().equals(signature);
        } catch (Exception e) {
            log.error("Webhook signature validation failed", e);
            return false;
        }
    }

    /**
     * Generate a unique payment reference.
     */
    public String generateReference(Long memberId) {
        return "APSUSM-PAY-" + memberId + "-" + System.currentTimeMillis();
    }
}
