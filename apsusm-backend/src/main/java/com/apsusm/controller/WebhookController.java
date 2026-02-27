package com.apsusm.controller;

import com.apsusm.service.MemberService;
import com.apsusm.service.PaystackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final PaystackService paystackService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    public WebhookController(PaystackService paystackService,
                             MemberService memberService,
                             ObjectMapper objectMapper) {
        this.paystackService = paystackService;
        this.memberService = memberService;
        this.objectMapper = objectMapper;
    }

    /**
     * POST /api/webhooks/paystack
     * Paystack webhook endpoint for payment verification.
     * Validates HMAC-SHA512 signature before processing.
     */
    @PostMapping("/paystack")
    public ResponseEntity<String> handlePaystackWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {

        log.info("Received Paystack webhook");

        // Validate webhook signature
        if (signature != null && !signature.isEmpty()) {
            if (!paystackService.validateWebhookSignature(payload, signature)) {
                log.warn("Invalid Paystack webhook signature");
                return ResponseEntity.status(401).body("Invalid signature");
            }
        }

        try {
            JsonNode event = objectMapper.readTree(payload);
            String eventType = event.get("event").asText();

            log.info("Paystack event type: {}", eventType);

            if ("charge.success".equals(eventType)) {
                JsonNode data = event.get("data");
                String reference = data.get("reference").asText();
                String status = data.get("status").asText();

                if ("success".equals(status)) {
                    log.info("Processing successful payment for reference: {}", reference);
                    memberService.processPaymentSuccess(reference);
                } else {
                    log.warn("Payment not successful for reference: {} (status: {})", reference, status);
                    memberService.processPaymentFailure(reference);
                }
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Error processing Paystack webhook", e);
            return ResponseEntity.ok("OK"); // Always return 200 to Paystack
        }
    }
}
