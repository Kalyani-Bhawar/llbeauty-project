package com.llbeauty.repository;

import com.llbeauty.entity.Invoice;
import com.llbeauty.entity.MerchantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByMerchantOrder(MerchantOrder merchantOrder);
}
