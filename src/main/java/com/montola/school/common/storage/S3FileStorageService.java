package com.montola.school.common.storage;

import com.montola.school.course.enums.StorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * S3-backed storage for shop product files.
 * <p>
 * The bucket is expected to be <b>private</b> (Block Public Access on, SSE on);
 * files are only ever exposed through short-lived presigned URLs. Credentials
 * come from the AWS SDK default provider chain, so an instance/task role can be
 * used in production without any keys in configuration.
 * </p>
 *
 * @author avidewan
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3")
@Slf4j
public class S3FileStorageService implements FileStorageService {

    private final StorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public S3FileStorageService(StorageProperties properties) {
        this.properties = properties;

        Region region = Region.of(properties.getS3().getRegion());
        this.s3Client = S3Client.builder().region(region).build();
        this.presigner = S3Presigner.builder().region(region).build();
    }

    @Override
    public String store(byte[] content, String filename, String contentType) {
        String key = buildKey(filename);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getS3().getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(content));
        log.info("Stored {} bytes in s3://{}/{}", content.length, properties.getS3().getBucket(), key);

        return key;
    }

    @Override
    public String url(String key, Duration ttl) {
        if (key == null || key.isBlank()) {
            return null;
        }

        Duration effectiveTtl = (ttl != null && !ttl.isZero()) ? ttl
                : Duration.ofSeconds(properties.getS3().getUrlTtlSeconds());

        GetObjectRequest getObject = GetObjectRequest.builder()
                .bucket(properties.getS3().getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(effectiveTtl)
                .getObjectRequest(getObject)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(properties.getS3().getBucket())
                .key(key)
                .build());
    }

    @Override
    public StorageProvider provider() {
        return StorageProvider.AWS_S3;
    }

    /**
     * Keeps objects under a single configured prefix and gives each upload a
     * unique name so re-uploading the same filename never overwrites a file.
     */
    private String buildKey(String filename) {
        String prefix = properties.getS3().getPrefix();
        String safeName = (filename == null || filename.isBlank()) ? "file" : filename.replaceAll("[^a-zA-Z0-9._-]", "_");

        return (prefix == null ? "" : prefix) + UUID.randomUUID() + "-" + safeName;
    }
}
