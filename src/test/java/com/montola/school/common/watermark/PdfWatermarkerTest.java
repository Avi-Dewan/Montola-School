package com.montola.school.common.watermark;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PdfWatermarkerTest {

    private final PdfWatermarker watermarker = new PdfWatermarker();

    @Test
    void stampsTheMarkAndKeepsTheDocumentReadable() throws Exception {
        byte[] original = onePagePdf();

        byte[] stamped = watermarker.stamp(original, "student@test.local · 42 · 2026-09-26");

        assertThat(stamped.length).isGreaterThan(original.length);
        try (PDDocument document = Loader.loadPDF(stamped)) {
            assertThat(document.getNumberOfPages()).isEqualTo(1);
        }
    }

    @Test
    void returnsTheOriginalBytesWhenTheMarkIsBlank() throws Exception {
        byte[] original = onePagePdf();

        assertThat(watermarker.stamp(original, "  ")).isSameAs(original);
    }

    @Test
    void returnsTheOriginalBytesWhenTheInputIsNotAPdf() {
        byte[] notAPdf = "definitely not a pdf".getBytes();

        assertThat(watermarker.stamp(notAPdf, "student@test.local")).isSameAs(notAPdf);
    }

    private byte[] onePagePdf() throws Exception {
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
