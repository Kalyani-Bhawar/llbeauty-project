package com.llbeauty.repository;

import com.llbeauty.entity.MemberProfile;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    Optional<MemberProfile> findByUser(User user);
    Optional<MemberProfile> findByUuid(String uuid);
    Optional<MemberProfile> findByMemberId(String memberId);
}
