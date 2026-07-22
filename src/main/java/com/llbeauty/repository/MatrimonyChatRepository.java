package com.llbeauty.repository;

import com.llbeauty.entity.MatrimonyChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatrimonyChatRepository extends JpaRepository<MatrimonyChatMessage, Long> {

    List<MatrimonyChatMessage> findByMatchIdOrderByTimestampAsc(Long matchId);
}
