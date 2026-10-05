package com.montola.school.common.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for shop product file storage.
 *
 * @author avidewan
 */
@Component
@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageProperties {

    /**
     * Active storage backend: {@code s3} or {@code external}.
     * {@code external} keeps whatever reference was supplied (e.g. a Google Drive id)
     * and requires no cloud credentials, which is the safe local/CI default.
     */
    private String provider = "external";

    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
        private String region = "ap-south-1";
        private long urlTtlSeconds = 300;
        private String prefix = "shop/";
    }
}
