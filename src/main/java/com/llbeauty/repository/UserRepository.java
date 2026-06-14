package com.llbeauty.repository;

import com.llbeauty.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);
    Optional<User> findByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR u.mobile LIKE %:search%) AND " +
           "(:blocked IS NULL OR u.isBlocked = :blocked)")
    Page<User> searchUsers(@Param("search") String search, @Param("blocked") Boolean blocked, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR u.mobile LIKE %:search%) AND " +
           "(:blocked IS NULL OR u.isBlocked = :blocked) ORDER BY u.createdAt DESC")
    List<User> searchUsersList(@Param("search") String search, @Param("blocked") Boolean blocked);
}
