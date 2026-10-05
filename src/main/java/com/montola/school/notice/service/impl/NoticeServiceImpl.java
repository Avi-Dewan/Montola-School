package com.montola.school.notice.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.notice.dto.NoticeRequestDto;
import com.montola.school.notice.dto.NoticeResponseDto;
import com.montola.school.notice.enums.NoticeType;
import com.montola.school.notice.model.Notice;
import com.montola.school.notice.repository.NoticeRepository;
import com.montola.school.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author avidewan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getActiveNotices() {
        return noticeRepository.findByIsDeletedFalseAndActiveTrueOrderByOrderIndexAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getAllNotices() {
        return noticeRepository.findByIsDeletedFalseOrderByOrderIndexAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NoticeResponseDto createNotice(NoticeRequestDto request) {
        Notice notice = new Notice();
        notice.setOrderIndex(0);
        apply(notice, request);

        return toResponse(noticeRepository.save(notice));
    }

    @Override
    @Transactional
    public NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto request) {
        Notice notice = findNotice(noticeId);
        apply(notice, request);

        return toResponse(noticeRepository.save(notice));
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = findNotice(noticeId);
        notice.setDeleted(true);
        noticeRepository.save(notice);

        log.info("Soft-deleted notice {}", noticeId);
    }

    private void apply(Notice notice, NoticeRequestDto request) {
        notice.setTitle(request.getTitle());
        notice.setMessage(request.getMessage());
        notice.setLink(request.getLink());

        if (request.getType() != null) {
            notice.setType(request.getType());
        }
        if (request.getActive() != null) {
            notice.setActive(request.getActive());
        }
        if (request.getOrderIndex() != null) {
            notice.setOrderIndex(request.getOrderIndex());
        }
    }

    private Notice findNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("notice.notfound"));
    }

    private NoticeResponseDto toResponse(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .message(notice.getMessage())
                .type(notice.getType() != null ? notice.getType() : NoticeType.INFO)
                .link(notice.getLink())
                .active(notice.isActive())
                .orderIndex(notice.getOrderIndex())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
