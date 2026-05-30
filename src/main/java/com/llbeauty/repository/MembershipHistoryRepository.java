package com.llbeauty.repository;

import com.llbeauty.entity.User;
import com.llbeauty.entity.MembershipHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MembershipHistoryRepository extends JpaRepository<MembershipHistory, Long> {
    List<MembershipHistory> findByUserOrderByStartDateDesc(User user);
}
