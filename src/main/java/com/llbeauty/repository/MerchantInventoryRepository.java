package com.llbeauty.repository;

import com.llbeauty.entity.MerchantInventory;
import com.llbeauty.entity.Product;
import com.llbeauty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MerchantInventoryRepository extends JpaRepository<MerchantInventory, Long> {
    List<MerchantInventory> findByMerchant(User merchant);
    Optional<MerchantInventory> findByMerchantAndProduct(User merchant, Product product);
}
