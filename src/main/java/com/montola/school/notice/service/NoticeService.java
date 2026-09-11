package com.montola.school.notice.service;

import com.montola.school.notice.dto.NoticeRequestDto;
import com.montola.school.notice.dto.NoticeResponseDto;

import java.util.List;

/**
 * Homepage notices.
 *
 * @author avidewan
 */
public interface NoticeService {

    List<NoticeResponseDto> getActiveNotices();

    List<NoticeResponseDto> getAllNotices();

    NoticeResponseDto createNotice(NoticeRequestDto request);

    NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto request);

    void deleteNotice(Long noticeId);
}
