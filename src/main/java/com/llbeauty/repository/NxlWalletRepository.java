package com.llbeauty.repository;

import com.llbeauty.entity.NxlWallet;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NxlWalletRepository extends JpaRepository<NxlWallet, Long> {
    Optional<NxlWallet> findByUser(User user);
}
