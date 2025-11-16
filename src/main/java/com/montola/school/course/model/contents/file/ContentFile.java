package com.montola.school.course.model.contents.file;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.enums.StorageProvider;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * @author avidewan
 * @date 11/16/25
 */
@MappedSuperclass
@Getter
@Setter
public abstract class ContentFile extends Persistent {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", nullable = false, length = 30)
    private StorageProvider storageProvider;

}

