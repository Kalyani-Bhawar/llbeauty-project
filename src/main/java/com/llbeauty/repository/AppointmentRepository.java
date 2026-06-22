package com.llbeauty.repository;

import com.llbeauty.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByUserMobile(String mobile);
    long countByStatus(String status);

    boolean existsByAppointmentDateAndTimeSlotAndStatusIn(LocalDate date, String slot, List<String> statuses);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(a.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR a.userMobile LIKE %:search% OR LOWER(a.serviceName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.services) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status)")
    Page<Appointment> searchAppointments(@Param("search") String search, @Param("status") String status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(a.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR a.userMobile LIKE %:search% OR LOWER(a.serviceName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.services) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status) ORDER BY a.createdAt DESC")
    List<Appointment> searchAppointmentsList(@Param("search") String search, @Param("status") String status);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(a.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR a.userMobile LIKE %:search% OR LOWER(a.serviceName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.services) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status) AND " +
           "(:startDate IS NULL OR a.appointmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.appointmentDate <= :endDate)")
    Page<Appointment> searchAppointmentsWithDate(
        @Param("search") String search, 
        @Param("status") String status, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(a.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR a.userMobile LIKE %:search% OR LOWER(a.serviceName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.services) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status) AND " +
           "(:startDate IS NULL OR a.appointmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.appointmentDate <= :endDate) ORDER BY a.createdAt DESC")
    List<Appointment> searchAppointmentsListWithDate(
        @Param("search") String search, 
        @Param("status") String status, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
}
