package com.montola.school.notice.model;

import com.montola.school.common.model.Persistent;
import com.montola.school.notice.enums.NoticeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * A notice shown on the homepage.
 *
 * @author avidewan
 */
@Entity
@Table(name = "notices", indexes = {
        @Index(name = "idx_notice_active_order", columnList = "active, order_index")
})
@Getter
@Setter
@SequenceGenerator(name = "notices_seq", sequenceName = "notices_seq", allocationSize = 1)
public class Notice extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notices_seq")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NoticeType type = NoticeType.INFO;

    @Column(length = 500)
    private String link;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
}
