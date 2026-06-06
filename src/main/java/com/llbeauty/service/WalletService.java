package com.llbeauty.service;

import com.llbeauty.entity.User;
import com.llbeauty.entity.Wallet;
import com.llbeauty.entity.WalletTransaction;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.repository.WalletRepository;
import com.llbeauty.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public WalletService(UserRepository userRepository, WalletTransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    private void syncWallet(User user, BigDecimal balance) {
        Wallet w = walletRepository.findByUser(user).orElseGet(() -> {
            Wallet newWallet = new Wallet();
            newWallet.setUser(user);
            return newWallet;
        });
        w.setBalance(balance);
        walletRepository.save(w);
    }

    public BigDecimal getBalance(User user) {
        if (user == null) return BigDecimal.ZERO;
        BigDecimal bal = user.getWalletBalance() != null ? user.getWalletBalance() : BigDecimal.ZERO;
        // Ensure wallet entity exists and is in sync
        syncWallet(user, bal);
        return bal;
    }

    @Transactional
    public void credit(User user, BigDecimal amount, String description, String source) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        BigDecimal currentBalance = getBalance(user);
        BigDecimal newBalance = currentBalance.add(amount);
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        // Sync with Wallet table
        syncWallet(user, newBalance);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setSource(source);
        tx.setDescription(description);
        tx.setStatus("SUCCESS");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    @Transactional
    public void creditWithDetails(User user, BigDecimal amount, String description, String source, Long paymentId, Long orderId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        BigDecimal currentBalance = getBalance(user);
        BigDecimal newBalance = currentBalance.add(amount);
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        syncWallet(user, newBalance);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setSource(source);
        tx.setDescription(description);
        tx.setPaymentId(paymentId);
        tx.setOrderId(orderId);
        tx.setStatus("SUCCESS");
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
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        // Sync with Wallet table
        syncWallet(user, newBalance);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setSource(source);
        tx.setDescription(description);
        tx.setStatus("SUCCESS");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
        return true;
    }

    @Transactional
    public boolean debitWithDetails(User user, BigDecimal amount, String description, String source, Long paymentId, Long orderId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal currentBalance = getBalance(user);
        if (currentBalance.compareTo(amount) < 0) {
            return false;
        }
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        syncWallet(user, newBalance);

        WalletTransaction tx = new WalletTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setSource(source);
        tx.setDescription(description);
        tx.setPaymentId(paymentId);
        tx.setOrderId(orderId);
        tx.setStatus("SUCCESS");
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
