package com.llbeauty.repository;

import com.llbeauty.entity.MatrimonyPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MatrimonyPreferenceRepository extends JpaRepository<MatrimonyPreference, Long> {

    Optional<MatrimonyPreference> findByUserId(Long userId);
}
