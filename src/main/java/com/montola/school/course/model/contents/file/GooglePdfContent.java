package com.montola.school.course.model.contents.file;

import com.montola.school.course.model.ContentItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static com.montola.school.course.enums.StorageProvider.GOOGLE_DRIVE;

/**
 * @author avidewan
 * @date 11/16/25
 */
@Entity
@Table(name = "pdf_google_contents")
@Getter
@Setter
@SequenceGenerator(name = "google_pdf_seq", sequenceName = "google_pdf_seq", allocationSize = 2)
public class GooglePdfContent extends ContentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "google_pdf_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "content_item_id", nullable = false)
    private ContentItem contentItem;

    @Column(name = "file_id", nullable = false, length = 200)
    private String googleFileId;

    @Column(name = "page_count")
    private Integer pageCount;

    public GooglePdfContent() {
        setStorageProvider(GOOGLE_DRIVE);
        setMimeType("application/pdf");
    }
}
