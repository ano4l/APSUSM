package com.apsusm.repository;

import com.apsusm.model.Member;
import com.apsusm.model.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByPaystackReference(String reference);

    List<Member> findByStatus(MemberStatus status);

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    @Query("SELECT COALESCE(MAX(m.id), 0) FROM Member m")
    Long findMaxId();

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = :status")
    long countByStatus(MemberStatus status);

    @Query("SELECT COUNT(m) FROM Member m WHERE YEAR(m.registeredAt) = :year")
    long countByYear(int year);

    List<Member> findAllByOrderByRegisteredAtDesc();
}
