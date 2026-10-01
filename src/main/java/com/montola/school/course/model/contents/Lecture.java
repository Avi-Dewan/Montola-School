package com.montola.school.course.model.contents;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.enums.StorageProvider;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author avidewan
 * @date 9/29/25
 */
@Entity
@Table(name = "lectures")
@Getter
@Setter
@SequenceGenerator(name = "lectures_seq", sequenceName = "lectures_seq", allocationSize = 2)
public class Lecture extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lectures_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "content_item_id", nullable = false)
    private ContentItem contentItem;

    /**
     * A YouTube video id for GOOGLE_DRIVE lectures, or an object-storage key for
     * AWS_S3 ones.
     */
    @Column(length = 500)
    private String videoId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", length = 30)
    private StorageProvider storageProvider = StorageProvider.GOOGLE_DRIVE;
}
