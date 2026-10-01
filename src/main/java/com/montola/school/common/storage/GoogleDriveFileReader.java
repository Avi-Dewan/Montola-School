package com.montola.school.common.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Fetches the bytes of a link-shared Google Drive file.
 * <p>
 * Content that still lives on Drive is pulled server-side so it can be
 * watermarked before it is served, instead of handing the client a permanent
 * Drive id that it embeds directly.
 * </p>
 *
 * @author avidewan
 */
@Component
@Slf4j
public class GoogleDriveFileReader {

    private static final String DOWNLOAD_URL = "https://drive.google.com/uc?export=download&id=";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public byte[] fetch(String fileId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DOWNLOAD_URL + fileId))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "Google Drive returned HTTP " + response.statusCode() + " for file " + fileId);
            }

            log.debug("Fetched {} bytes from Google Drive file {}", response.body().length, fileId);
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Could not fetch Google Drive file " + fileId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching Google Drive file " + fileId, e);
        }
    }
}
