package com.llbeauty.repository;

import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    Optional<UserMembership> findByUserAndStatus(User user, String status);
    List<UserMembership> findByUser(User user);
    long countByStatus(String status);
    Optional<UserMembership> findByUuid(String uuid);
    Optional<UserMembership> findByMemberId(String memberId);
    List<UserMembership> findByReferralCode(String referralCode);

    @Query("SELECT um FROM UserMembership um WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(um.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(um.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR um.user.mobile LIKE %:search% OR um.memberId LIKE %:search%) AND " +
           "(:status IS NULL OR :status = '' OR um.status = :status) AND " +
           "(:planName IS NULL OR :planName = '' OR um.membership.name = :planName)")
    Page<UserMembership> searchMemberships(@Param("search") String search, @Param("status") String status, @Param("planName") String planName, Pageable pageable);

    @Query("SELECT um FROM UserMembership um WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(um.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(um.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR um.user.mobile LIKE %:search% OR um.memberId LIKE %:search%) AND " +
           "(:status IS NULL OR :status = '' OR um.status = :status) AND " +
           "(:planName IS NULL OR :planName = '' OR um.membership.name = :planName) ORDER BY um.startDate DESC")
    List<UserMembership> searchMembershipsList(@Param("search") String search, @Param("status") String status, @Param("planName") String planName);
}
