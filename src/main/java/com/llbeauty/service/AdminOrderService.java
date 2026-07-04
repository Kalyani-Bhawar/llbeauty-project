package com.llbeauty.service;

import com.llbeauty.entity.AuditLog;
import com.llbeauty.entity.Order;
import com.llbeauty.entity.Payment;
import com.llbeauty.repository.AuditLogRepository;
import com.llbeauty.repository.OrderRepository;
import com.llbeauty.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final AuditLogRepository auditLogRepository;

    public AdminOrderService(OrderRepository orderRepository,
                             PaymentRepository paymentRepository,
                             WalletService walletService,
                             PaymentService paymentService,
                             AuditLogRepository auditLogRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.walletService = walletService;
        this.paymentService = paymentService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void cancelOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if ("SUCCESS".equals(order.getStatus()) || "PENDING".equals(order.getStatus())) {
                order.setStatus("FAILED");
                orderRepository.save(order);
            } else {
                throw new IllegalArgumentException("Order cannot be cancelled in status: " + order.getStatus());
            }
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }

    @Transactional
    public void updateOrderStatus(Long id, String orderStatus, String currentEmail) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);

            AuditLog log = new AuditLog("ORDER_UPDATED", "Order #" + id + " status updated to " + orderStatus, currentEmail);
            auditLogRepository.save(log);
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }

    @Transactional
    public void refundOrder(Long id, String refundMethod) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (!"SUCCESS".equals(order.getStatus())) {
                throw new IllegalArgumentException("Only successful orders can be refunded.");
            }

            Payment payment = paymentRepository.findAll().stream()
                    .filter(p -> String.valueOf(order.getId()).equals(p.getReferenceId()) && "PRODUCT".equals(p.getPaymentFor()) && "SUCCESS".equals(p.getStatus()))
                    .findFirst().orElse(null);

            boolean refundSuccess = false;

            if ("WALLET".equalsIgnoreCase(refundMethod)) {
                walletService.credit(order.getUser(), BigDecimal.valueOf(order.getTotalAmount()), "Refund for Order #" + order.getId(), "REFUND");
                refundSuccess = true;
                if (payment != null) {
                    payment.setStatus("REFUNDED_WALLET");
                    paymentRepository.save(payment);
                }
            } else if ("RAZORPAY".equalsIgnoreCase(refundMethod)) {
                if (payment != null && payment.getRazorpayPaymentId() != null) {
                    refundSuccess = paymentService.processRefund(payment.getRazorpayOrderId(), "RAZORPAY", null);
                    if (!refundSuccess) {
                        throw new IllegalArgumentException("Razorpay API refund failed.");
                    }
                } else {
                    throw new IllegalArgumentException("No Razorpay payment found for this order.");
                }
            }

            if (refundSuccess) {
                order.setStatus("REFUNDED");
                orderRepository.save(order);
            }
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }
}
