package com.llbeauty.service;

import com.llbeauty.entity.User;
import com.llbeauty.entity.WalletTransaction;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
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

    public BigDecimal getBalance(User user) {
        return user.getWalletBalance() != null ? user.getWalletBalance() : BigDecimal.ZERO;
    }

    @Transactional
    public void credit(User user, BigDecimal amount, String description, String source) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        BigDecimal currentBalance = getBalance(user);
        user.setWalletBalance(currentBalance.add(amount));
        userRepository.save(user);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setSource(source);
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    @Transactional
    public void credit(User user, Double amount, String description) {
        credit(user, BigDecimal.valueOf(amount), description, "GENERAL_CREDIT");
    }

    @Transactional
    public void credit(User user, BigDecimal amount, String description) {
        credit(user, amount, description, "GENERAL_CREDIT");
    }

    @Transactional
    public boolean debit(User user, BigDecimal amount, String description, String source) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal currentBalance = getBalance(user);
        if (currentBalance.compareTo(amount) < 0) {
            return false;
        }
        user.setWalletBalance(currentBalance.subtract(amount));
        userRepository.save(user);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setSource(source);
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
        return true;
    }

    @Transactional
    public boolean debit(User user, Double amount, String description) {
        return debit(user, BigDecimal.valueOf(amount), description, "GENERAL_DEBIT");
    }

    @Transactional
    public boolean debit(User user, BigDecimal amount, String description) {
        return debit(user, amount, description, "GENERAL_DEBIT");
    }

    public List<WalletTransaction> getTransactionHistory(User user) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<WalletTransaction> getRecentTransactions(User user) {
        return transactionRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    }
}
