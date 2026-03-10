package com.apsusm.service;

import com.apsusm.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MemberIdGenerator {

    private final MemberRepository memberRepository;
    private final AtomicLong counter;

    public MemberIdGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.counter = new AtomicLong(memberRepository.findMaxId());
    }

    /**
     * Generates a unique member ID in format: APSUSM-DR-YYYY-XXXX.
     * e.g., APSUSM-DR-2026-0001
     */
    public synchronized String generateMemberId() {
        long nextVal = counter.incrementAndGet();
        int year = Year.now().getValue();
        return String.format("APSUSM-DR-%04d-%04d", year, nextVal);
    }
}
