package com.llbeauty.repository;

import com.llbeauty.entity.MatrimonyMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatrimonyMatchRepository extends JpaRepository<MatrimonyMatch, Long> {

    boolean existsByUserOneIdAndUserTwoId(Long userOneId, Long userTwoId);

    List<MatrimonyMatch> findByUserOneIdOrUserTwoId(Long userOneId, Long userTwoId);
}
