package com.montola.school.common.storage;

import com.montola.school.course.enums.StorageProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Fallback storage used when no cloud bucket is configured.
 * <p>
 * Files are not copied anywhere: the product keeps whatever reference an admin
 * supplied (for example a Google Drive file id), and that reference is returned
 * unchanged as the URL. This preserves the behaviour of the existing course
 * PDF content and lets the app run locally without AWS credentials.
 * </p>
 *
 * @author avidewan
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "external", matchIfMissing = true)
public class ExternalLinkFileStorageService implements FileStorageService {

    @Override
    public String store(byte[] content, String filename, String contentType) {
        throw new UnsupportedOperationException(
                "The 'external' storage provider cannot store files. "
                        + "Set app.storage.provider=s3 and provide AWS credentials, or supply an existing file reference.");
    }

    @Override
    public String url(String key, Duration ttl) {
        return key;
    }

    @Override
    public void delete(String key) {
        // Nothing to delete — the referenced file lives outside this application.
    }

    @Override
    public StorageProvider provider() {
        return StorageProvider.GOOGLE_DRIVE;
    }
}
