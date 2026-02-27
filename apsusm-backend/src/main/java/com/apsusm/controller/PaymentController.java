package com.apsusm.controller;

import com.apsusm.model.Member;
import com.apsusm.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final MemberService memberService;

    public PaymentController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /api/payment/verify/{reference}
     * Manual payment verification endpoint (called after Paystack redirect).
     */
    @GetMapping("/verify/{reference}")
    public ResponseEntity<?> verifyPayment(@PathVariable String reference) {
        try {
            Member member = memberService.verifyPayment(reference);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment verified successfully",
                    "member", memberService.toResponse(member)
            ));
        } catch (Exception e) {
            log.error("Payment verification failed for reference: {}", reference, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
