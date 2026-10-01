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
        /**
         * Custom endpoint for an S3-compatible provider such as Cloudflare R2.
         * Blank means the real AWS S3 endpoints are used.
         */
        private String endpoint;

        /**
         * Path-style addressing ({@code endpoint/bucket/key}); most S3-compatible
         * providers need this, real AWS prefers virtual-hosted style.
         */
        private boolean pathStyle = true;

        private String bucket;
        private String region = "ap-south-1";
        private long urlTtlSeconds = 300;

        /**
         * Video URLs must outlive playback, so they are signed for much longer
         * than document downloads.
         */
        private long videoTtlSeconds = 7200;

        private String prefix = "shop/";
    }
}
