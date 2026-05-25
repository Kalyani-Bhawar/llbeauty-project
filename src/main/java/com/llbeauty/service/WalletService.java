package com.llbeauty.service;

import com.llbeauty.entity.User;
import com.llbeauty.entity.WalletTransaction;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(UserRepository userRepository, WalletTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Double getBalance(User user) {
        return user.getWalletBalance() != null ? user.getWalletBalance() : 0.0;
    }

    @Transactional
    public void credit(User user, Double amount, String description) {
        if (amount <= 0) return;
        Double currentBalance = getBalance(user);
        user.setWalletBalance(currentBalance + amount);
        userRepository.save(user);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    @Transactional
    public boolean debit(User user, Double amount, String description) {
        if (amount <= 0) return false;
        Double currentBalance = getBalance(user);
        if (currentBalance < amount) {
            return false;
        }
        user.setWalletBalance(currentBalance - amount);
        userRepository.save(user);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
        return true;
    }

    public List<WalletTransaction> getTransactionHistory(User user) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<WalletTransaction> getRecentTransactions(User user) {
        return transactionRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    }
}
