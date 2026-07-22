package com.llbeauty.repository;

import com.llbeauty.entity.MatrimonyInterest;
import com.llbeauty.enums.InterestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatrimonyInterestRepository extends JpaRepository<MatrimonyInterest, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, InterestStatus status);

    List<MatrimonyInterest> findByReceiverId(Long receiverId);

    List<MatrimonyInterest> findBySenderId(Long senderId);

    List<MatrimonyInterest> findByReceiverIdAndStatus(Long receiverId, InterestStatus status);
}
