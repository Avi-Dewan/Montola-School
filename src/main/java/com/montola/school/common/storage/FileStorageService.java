package com.montola.school.common.storage;

import com.montola.school.course.enums.StorageProvider;

import java.time.Duration;

/**
 * Abstraction over wherever shop product files live.
 * <p>
 * Keeps the shop domain independent of the backend that stores the bytes, so
 * swapping Google Drive for S3 (or later putting CloudFront in front) is a
 * configuration change rather than a rewrite.
 * </p>
 *
 * @author avidewan
 */
public interface FileStorageService {

    /**
     * Persists a file and returns the reference that identifies it.
     *
     * @return the storage key, to be saved on the product
     */
    String store(byte[] content, String filename, String contentType);

    /**
     * Builds a time-limited URL for the given key.
     * For external providers this may simply return the stored reference.
     */
    String url(String key, Duration ttl);

    /**
     * Reads the raw bytes of a stored object, so the caller can process them
     * (for example watermark a PDF) before serving them.
     *
     * @throws UnsupportedOperationException when the provider does not hold the
     *                                       bytes itself, such as an external link
     */
    byte[] read(String key);

    void delete(String key);

    StorageProvider provider();
}
