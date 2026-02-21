package com.montola.school.payment.service;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.learner.model.Enrollment;
import com.montola.school.learner.repository.EnrollmentRepository;
import com.montola.school.payment.dto.PaymentRequestDto;
import com.montola.school.payment.dto.PaymentResponseDto;
import com.montola.school.payment.enums.PaymentStatus;
import com.montola.school.payment.model.Payment;
import com.montola.school.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of PaymentService.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;

    @Override
    @Transactional
    public PaymentResponseDto submitPayment(Long userId, PaymentRequestDto request) {
        log.info("Submitting payment: user={}, chapter={}, amount={}", userId, request.getChapterId(), request.getAmount());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user.notfound"));

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("course.chapter.notfound"));

        // See if user submitted already for this transaction and gracefully delete and replace
        // or do something -> Soft delete ?

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setChapter(chapter);
        payment.setTransactionId(request.getTransactionId());
        payment.setSenderNumber(request.getSenderNumber());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BKASH");

        Payment saved = paymentRepository.save(payment);

        return mapToDto(saved, false); // Student view
    }

    @Override
    @Transactional
    public PaymentResponseDto verifyPayment(Long paymentId, Long adminUserId) {
        log.info("Verifying payment {} by admin {}", paymentId, adminUserId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("payment.notfound"));

        if (payment.getStatus() == PaymentStatus.VERIFIED) {
            log.warn("Payment {} is already verified", paymentId);
            return mapToDto(payment, true);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.admin.notfound"));

        // 1. Update Payment Status
        payment.setStatus(PaymentStatus.VERIFIED);
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // 2. Create Enrollment
        if (!enrollmentRepository.existsByUserIdAndChapterId(payment.getUser().getId(), payment.getChapter().getId())) {
            Enrollment enrollment = new Enrollment();

            enrollment.setUser(payment.getUser());
            enrollment.setChapter(payment.getChapter());
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setProgressPercentage(0.0);
            enrollmentRepository.save(enrollment);

            log.info("Enrollment created for user {} in chapter {}", payment.getUser().getId(), payment.getChapter().getId());
        }

        return mapToDto(savedPayment, true); // Admin view
    }

    @Override
    @Transactional
    public PaymentResponseDto rejectPayment(Long paymentId, Long adminUserId) {
        log.info("Rejecting payment {} by admin {}", paymentId, adminUserId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("payment.notfound"));

        if (payment.getStatus() == PaymentStatus.REJECTED) {
            log.warn("Payment {} is already rejected", paymentId);
            return mapToDto(payment, true);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.admin.notfound"));

        payment.setStatus(PaymentStatus.REJECTED);
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        return mapToDto(savedPayment, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(p -> mapToDto(p, true))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getUnverifiedPayments() {
        return paymentRepository.findByStatus(PaymentStatus.PENDING).stream()
                .map(p -> mapToDto(p, true))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getMyPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(p -> mapToDto(p, false))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getMyPaymentForChapter(Long userId, Long chapterId) {
        return paymentRepository.findByUserIdAndChapterId(userId, chapterId)
                .map(p -> mapToDto(p, false))
                .orElse(null);
    }

    private PaymentResponseDto mapToDto(Payment payment, boolean isAdmin) {
        PaymentResponseDto.PaymentResponseDtoBuilder builder = PaymentResponseDto.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .userName(isAdmin ? payment.getUser().getFullName() : null) // Only admin sees user details if needed, simplfied here
                .chapterId(payment.getChapter().getId())
                .chapterTitle(payment.getChapter().getTitle())
                .senderNumber(payment.getSenderNumber())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .verifiedAt(payment.getVerifiedAt());

        if (isAdmin && payment.getVerifiedBy() != null) {
            builder.verifiedByUserId(payment.getVerifiedBy().getId());
            builder.verifiedByName(payment.getVerifiedBy().getEmail()); // Using email as name mock
        }

        return builder.build();
    }
}
