package com.llbeauty.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nxl_system_wallet")
public class SystemWallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance = BigDecimal.valueOf(10000);

    // Admin ne ata paryant kitni tokens add keli - total history
    @Column(name = "total_added", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAdded = BigDecimal.ZERO;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal b){this.balance=b;}
    public BigDecimal getTotalAdded(){return totalAdded != null ? totalAdded : BigDecimal.ZERO;}
    public void setTotalAdded(BigDecimal v){this.totalAdded = v;}
}