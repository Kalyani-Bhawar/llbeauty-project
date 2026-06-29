package com.llbeauty.repository;
import com.llbeauty.entity.SystemWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemWalletRepository extends JpaRepository<SystemWallet, Long> {}