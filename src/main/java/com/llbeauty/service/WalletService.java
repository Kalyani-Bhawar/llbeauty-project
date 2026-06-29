package com.llbeauty.service;

import com.llbeauty.entity.AdminNxlTransaction;
import com.llbeauty.entity.NxlWallet;
import com.llbeauty.entity.NxlWalletTransaction;
import com.llbeauty.entity.SystemWallet;
import com.llbeauty.repository.AdminNxlTransactionRepository;
import com.llbeauty.repository.NxlWalletRepository;
import com.llbeauty.repository.NxlWalletTransactionRepository;
import com.llbeauty.repository.SystemWalletRepository;
import com.llbeauty.exception.NxlException;
import com.llbeauty.constants.NxlConstants;
import org.springframework.data.domain.PageRequest;

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

    private final NxlWalletRepository nxlWalletRepository;
    private final NxlWalletTransactionRepository nxlTxRepository;
    private final SystemWalletRepository systemWalletRepository;
    private final AdminNxlTransactionRepository adminNxlTransactionRepository;
    private final UserRepository userRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public static final String SOURCE_REFERRAL = "REFERRAL";
    public static final String SOURCE_MERCHANT = "MERCHANT_APPROVAL";
    public static final String SOURCE_BOOKING = "BOOKING";
    public static final String SOURCE_MEMBERSHIP = "MEMBERSHIP";
    public static final String SOURCE_PAYMENT = "PAYMENT";
    
    // ⚠️ Constructor madhe 3 navin repos add kelet (NxlWalletRepository, NxlWalletTransactionRepository, SystemWalletRepository)
    public WalletService(UserRepository userRepository,
            WalletTransactionRepository transactionRepository,
            WalletRepository walletRepository,
            NxlWalletRepository nxlWalletRepository,
            NxlWalletTransactionRepository nxlTxRepository,
            SystemWalletRepository systemWalletRepository,
            AdminNxlTransactionRepository adminNxlTransactionRepository) {
this.userRepository = userRepository;
this.transactionRepository = transactionRepository;
this.walletRepository = walletRepository;
this.nxlWalletRepository = nxlWalletRepository;
this.nxlTxRepository = nxlTxRepository;
this.systemWalletRepository = systemWalletRepository;
this.adminNxlTransactionRepository = adminNxlTransactionRepository;
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

    // =========================================================
    //  ⬇️ NXL TOKEN SYSTEM LOGIC (NXL_Token_System se port kela)
    // =========================================================

    private SystemWallet getOrCreateAdminWallet() {
        List<SystemWallet> all = systemWalletRepository.findAll();
        if (!all.isEmpty()) return all.get(0);
        SystemWallet sw = new SystemWallet();
        sw.setBalance(BigDecimal.valueOf(10000));
        return systemWalletRepository.save(sw);
    }

    private NxlWallet getOrCreateNxlWallet(User user) {
        return nxlWalletRepository.findByUser(user).orElseGet(() ->
                nxlWalletRepository.save(new NxlWallet(user, BigDecimal.ZERO)));
    }

    @Transactional
    public NxlWalletTransaction creditNxl(User user, BigDecimal amount, String source) {
        String referenceId = source + "_" + System.currentTimeMillis();
        return creditNxl(user, amount, source, referenceId, "NXL Credit");
    }

    @Transactional
    public NxlWalletTransaction creditNxl(User user, BigDecimal amount, String source, String referenceId, String note) {
        if (!NxlConstants.ALLOWED_SOURCES.contains(source))
            throw new NxlException("Invalid source: " + source, "BAD_REQUEST", 400);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new NxlException("Amount must be positive", "BAD_REQUEST", 400);
        if (nxlTxRepository.existsBySourceAndTransactionId(source, referenceId))
            throw new NxlException("Duplicate transaction for this source + referenceId", "BAD_REQUEST", 400);

        NxlWallet userWallet = getOrCreateNxlWallet(user);
        SystemWallet adminWallet = getOrCreateAdminWallet();

        if (adminWallet.getBalance().compareTo(amount) < 0)
            throw new NxlException("Insufficient tokens in system admin wallet", "INSUFFICIENT_FUNDS", 400);

        BigDecimal adminBefore = adminWallet.getBalance();
        BigDecimal userBefore = userWallet.getBalance();

        adminWallet.setBalance(adminBefore.subtract(amount));
        systemWalletRepository.save(adminWallet);

        userWallet.setBalance(userBefore.add(amount));
        userWallet.setTotalEarned(userWallet.getTotalEarned().add(amount));
        nxlWalletRepository.save(userWallet);

        NxlWalletTransaction tx = new NxlWalletTransaction(
                user, referenceId, NxlConstants.CREDIT, amount, note, userWallet.getBalance());
        tx.setSource(source);
        tx.setAdminBalanceBefore(adminBefore);
        tx.setAdminBalanceAfter(adminWallet.getBalance());
        tx.setUserBalanceBefore(userBefore);
        return nxlTxRepository.save(tx);
    }

    @Transactional
    public NxlWalletTransaction debitNxl(User user, BigDecimal amount, String source, String referenceId, String note) {
        if (!NxlConstants.ALLOWED_SOURCES.contains(source))
            throw new NxlException("Invalid source: " + source, "BAD_REQUEST", 400);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new NxlException("Amount must be positive", "BAD_REQUEST", 400);
        if (nxlTxRepository.existsBySourceAndTransactionId(source, referenceId))
            throw new NxlException("Duplicate transaction for this source + referenceId", "BAD_REQUEST", 400);
        NxlWallet userWallet = nxlWalletRepository.findByUser(user)
                .orElseThrow(() -> new NxlException("Wallet not found", "WALLET_NOT_FOUND", 404));

        if (userWallet.getBalance().compareTo(amount) < 0)
            throw new NxlException("Insufficient tokens. Available: " + userWallet.getBalance(), "INSUFFICIENT_FUNDS", 400);

        SystemWallet adminWallet = getOrCreateAdminWallet();
        BigDecimal adminBefore = adminWallet.getBalance();
        BigDecimal userBefore = userWallet.getBalance();

        userWallet.setBalance(userBefore.subtract(amount));
        userWallet.setTotalSpent(userWallet.getTotalSpent().add(amount));
        nxlWalletRepository.save(userWallet);

        adminWallet.setBalance(adminBefore.add(amount));
        systemWalletRepository.save(adminWallet);

        NxlWalletTransaction tx = new NxlWalletTransaction(
                user, referenceId, NxlConstants.DEBIT, amount, note, userWallet.getBalance());
        tx.setSource(source);
        tx.setAdminBalanceBefore(adminBefore);
        tx.setAdminBalanceAfter(adminWallet.getBalance());
        tx.setUserBalanceBefore(userBefore);
        return nxlTxRepository.save(tx);
    }
    
   

    public BigDecimal getNxlBalance(User user) {
        if (user == null) return BigDecimal.ZERO;
        return nxlWalletRepository.findByUser(user).map(NxlWallet::getBalance).orElse(BigDecimal.ZERO);
    }

    public List<NxlWalletTransaction> getNxlHistory(User user) {
        return nxlTxRepository.findByUserOrderByDateTimeDesc(user);
    }

    public BigDecimal getAdminBalance() {
        return getOrCreateAdminWallet().getBalance();
    }

    public List<NxlWalletTransaction> getAllNxlTransactions(int page, int limit) {
        return nxlTxRepository.findAllByOrderByDateTimeDesc(PageRequest.of(Math.max(page - 1, 0), limit));
    }

    /**
     * Admin kadun system wallet madhe tokens add karne.
     * Example: Admin 500 tokens add karto → balance 1000 → 1500 hoto.
     */
    @Transactional
    public AdminNxlTransaction addAdminTokens(BigDecimal amount, String note) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new NxlException("Amount must be positive", "BAD_REQUEST", 400);

        SystemWallet adminWallet = getOrCreateAdminWallet();
        BigDecimal balanceBefore = adminWallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        adminWallet.setBalance(balanceAfter);
        adminWallet.setTotalAdded(adminWallet.getTotalAdded().add(amount));
        systemWalletRepository.save(adminWallet);

        AdminNxlTransaction tx = new AdminNxlTransaction(
            "CREDIT", amount, balanceBefore, balanceAfter,
            (note != null && !note.isEmpty()) ? note : "Admin manually added tokens"
        );
        return adminNxlTransactionRepository.save(tx);
    }

    /**
     * Admin NXL transaction history list.
     */
    public List<AdminNxlTransaction> getAdminNxlTransactions() {
        return adminNxlTransactionRepository.findAllByOrderByCreatedAtDesc();
    }

}