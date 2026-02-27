package com.apsusm.controller;

import com.apsusm.dto.MemberResponse;
import com.apsusm.model.Member;
import com.apsusm.model.MemberStatus;
import com.apsusm.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final MemberService memberService;

    public AdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /api/admin/dashboard
     * Get dashboard statistics.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(memberService.getDashboardStats());
    }

    /**
     * GET /api/admin/members
     * List all members (optionally filtered by status).
     */
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMembers(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            try {
                MemberStatus memberStatus = MemberStatus.valueOf(status.toUpperCase());
                return ResponseEntity.ok(memberService.getMembersByStatus(memberStatus));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    /**
     * GET /api/admin/members/{id}
     * Get a single member's details.
     */
    @GetMapping("/members/{id}")
    public ResponseEntity<?> getMember(@PathVariable Long id) {
        return memberService.findById(id)
                .map(member -> ResponseEntity.ok(memberService.toResponse(member)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/admin/members/{id}/regenerate-card
     * Regenerate a member's card.
     */
    @PostMapping("/members/{id}/regenerate-card")
    public ResponseEntity<?> regenerateCard(@PathVariable Long id) {
        try {
            Member member = memberService.regenerateCard(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Card regenerated successfully",
                    "member", memberService.toResponse(member)
            ));
        } catch (Exception e) {
            log.error("Card regeneration failed for member: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * GET /api/admin/members/{id}/card/front
     * Download the front card image for a member.
     */
    @GetMapping("/members/{id}/card/front")
    public ResponseEntity<byte[]> getCardFront(@PathVariable Long id) {
        return memberService.findById(id)
                .filter(m -> m.getCardFrontPath() != null)
                .map(member -> {
                    try {
                        byte[] data = java.nio.file.Files.readAllBytes(
                                java.nio.file.Paths.get(member.getCardFrontPath()));
                        return ResponseEntity.ok()
                                .contentType(MediaType.IMAGE_PNG)
                                .body(data);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().<byte[]>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/admin/members/{id}/card/back
     * Download the back card image for a member.
     */
    @GetMapping("/members/{id}/card/back")
    public ResponseEntity<byte[]> getCardBack(@PathVariable Long id) {
        return memberService.findById(id)
                .filter(m -> m.getCardBackPath() != null)
                .map(member -> {
                    try {
                        byte[] data = java.nio.file.Files.readAllBytes(
                                java.nio.file.Paths.get(member.getCardBackPath()));
                        return ResponseEntity.ok()
                                .contentType(MediaType.IMAGE_PNG)
                                .body(data);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().<byte[]>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
