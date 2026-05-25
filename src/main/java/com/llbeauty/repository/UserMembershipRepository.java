package com.llbeauty.repository;

import com.llbeauty.entity.User;
import com.llbeauty.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    Optional<UserMembership> findByUserAndStatus(User user, String status);
    List<UserMembership> findByUser(User user);
    long countByStatus(String status);
}
