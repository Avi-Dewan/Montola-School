package com.montola.school.shop.service.impl;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.payment.enums.PaymentStatus;
import com.montola.school.shop.dto.ShopPaymentDto;
import com.montola.school.shop.dto.ShopPaymentRequestDto;
import com.montola.school.shop.enums.EntitlementSource;
import com.montola.school.shop.model.ShopBundle;
import com.montola.school.shop.model.ShopEntitlement;
import com.montola.school.shop.model.ShopPayment;
import com.montola.school.shop.model.ShopProduct;
import com.montola.school.shop.repository.ShopBundleRepository;
import com.montola.school.shop.repository.ShopEntitlementRepository;
import com.montola.school.shop.repository.ShopPaymentRepository;
import com.montola.school.shop.repository.ShopProductRepository;
import com.montola.school.shop.service.ShopDtoAssembler;
import com.montola.school.shop.service.ShopPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author avidewan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopPaymentServiceImpl implements ShopPaymentService {

    private final ShopPaymentRepository paymentRepository;
    private final ShopProductRepository productRepository;
    private final ShopBundleRepository bundleRepository;
    private final ShopEntitlementRepository entitlementRepository;
    private final UserRepository userRepository;
    private final ShopDtoAssembler assembler;

    @Override
    @Transactional
    public ShopPaymentDto submitPayment(Long userId, ShopPaymentRequestDto request) {
        log.info("Shop payment submitted by user {} for product={} bundle={}",
                userId, request.getProductId(), request.getBundleId());

        if (request.getProductId() == null && request.getBundleId() == null) {
            throw new IllegalArgumentException("Either productId or bundleId is required.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));

        ShopProduct product = request.getProductId() != null
                ? productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("shop.product.notfound"))
                : null;

        ShopBundle bundle = request.getBundleId() != null
                ? bundleRepository.findById(request.getBundleId())
                        .orElseThrow(() -> new ResourceNotFoundException("shop.bundle.notfound"))
                : null;

        ShopPayment payment = new ShopPayment();
        payment.setUser(user);
        payment.setProduct(product);
        payment.setBundle(bundle);
        // Price is taken from the catalog, never from the request, so a client
        // cannot submit an arbitrary amount.
        payment.setAmount(product != null ? product.getPrice() : bundle.getPrice());
        payment.setSenderNumber(request.getSenderNumber());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentMethod(
                request.getPaymentMethod() != null ? request.getPaymentMethod() : "BKASH");
        payment.setStatus(PaymentStatus.PENDING);

        ShopPayment saved = paymentRepository.save(payment);

        return assembler.toPaymentDto(saved, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopPaymentDto> getMyPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(payment -> assembler.toPaymentDto(payment, false))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopPaymentDto> getAllPayments() {
        return paymentRepository.findAllByOrderByIdDesc().stream()
                .map(payment -> assembler.toPaymentDto(payment, true))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopPaymentDto> getUnverifiedPayments() {
        return paymentRepository.findByStatusOrderByIdDesc(PaymentStatus.PENDING).stream()
                .map(payment -> assembler.toPaymentDto(payment, true))
                .toList();
    }

    @Override
    @Transactional
    public ShopPaymentDto verifyPayment(Long paymentId, Long adminUserId) {
        ShopPayment payment = findPayment(paymentId);

        if (payment.getStatus() == PaymentStatus.VERIFIED) {
            log.warn("Shop payment {} is already verified", paymentId);
            return assembler.toPaymentDto(payment, true);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.admin.notfound"));

        payment.setStatus(PaymentStatus.VERIFIED);
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());
        ShopPayment saved = paymentRepository.save(payment);

        grantEntitlement(saved);

        return assembler.toPaymentDto(saved, true);
    }

    @Override
    @Transactional
    public ShopPaymentDto rejectPayment(Long paymentId, Long adminUserId) {
        ShopPayment payment = findPayment(paymentId);

        if (payment.getStatus() == PaymentStatus.REJECTED) {
            log.warn("Shop payment {} is already rejected", paymentId);
            return assembler.toPaymentDto(payment, true);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.admin.notfound"));

        payment.setStatus(PaymentStatus.REJECTED);
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());

        return assembler.toPaymentDto(paymentRepository.save(payment), true);
    }

    /**
     * Grants access for a verified payment. Idempotent, so re-verifying never
     * creates a duplicate entitlement.
     */
    private void grantEntitlement(ShopPayment payment) {
        Long userId = payment.getUser().getId();

        if (payment.getProduct() != null) {
            Long productId = payment.getProduct().getId();
            if (entitlementRepository.existsByUserIdAndProductId(userId, productId)) {
                return;
            }

            ShopEntitlement entitlement = new ShopEntitlement();
            entitlement.setUser(payment.getUser());
            entitlement.setProduct(payment.getProduct());
            entitlement.setSource(EntitlementSource.PAYMENT);
            entitlement.setGrantedAt(LocalDateTime.now());
            entitlementRepository.save(entitlement);

            log.info("Granted shop product {} to user {}", productId, userId);

        } else if (payment.getBundle() != null) {
            Long bundleId = payment.getBundle().getId();
            if (entitlementRepository.existsByUserIdAndBundleId(userId, bundleId)) {
                return;
            }

            ShopEntitlement entitlement = new ShopEntitlement();
            entitlement.setUser(payment.getUser());
            entitlement.setBundle(payment.getBundle());
            entitlement.setSource(EntitlementSource.PAYMENT);
            entitlement.setGrantedAt(LocalDateTime.now());
            entitlementRepository.save(entitlement);

            log.info("Granted shop bundle {} to user {}", bundleId, userId);
        }
    }

    private ShopPayment findPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.payment.notfound"));
    }
}
