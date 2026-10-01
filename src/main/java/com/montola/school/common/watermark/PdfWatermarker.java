package com.montola.school.common.watermark;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Stamps a per-user mark (usually the buyer's email and id) diagonally across
 * every page of a PDF before it leaves the application, so a leaked copy can be
 * traced back to the account it was issued to.
 *
 * @author avidewan
 */
@Component
@Slf4j
public class PdfWatermarker {

    private static final float FONT_SIZE = 60f;
    private static final float OPACITY = 0.25f;

    /**
     * Returns a copy of {@code pdf} with {@code mark} drawn across each page.
     * <p>
     * Never throws: if the document cannot be read or stamped, the original
     * bytes are returned unchanged so a download is never broken by a
     * watermarking failure.
     * </p>
     */
    public byte[] stamp(byte[] pdf, String mark) {
        if (pdf == null || pdf.length == 0 || mark == null || mark.isBlank()) {
            return pdf;
        }

        try (PDDocument document = Loader.loadPDF(pdf)) {
            PDFont font = new PDType1Font(FontName.HELVETICA);
            for (PDPage page : document.getPages()) {
                applyMark(document, page, font, mark);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.warn("Could not watermark PDF ({} bytes): {}", pdf.length, e.getMessage());
            return pdf;
        }
    }

    private void applyMark(PDDocument document, PDPage page, PDFont font, String mark) throws IOException {
        try (PDPageContentStream content = new PDPageContentStream(
                document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

            float width = page.getMediaBox().getWidth();
            float height = page.getMediaBox().getHeight();

            // A rotated page needs the same rotation applied to the mark so it
            // stays diagonal in the reader's view.
            switch (page.getRotation()) {
                case 90 -> {
                    width = page.getMediaBox().getHeight();
                    height = page.getMediaBox().getWidth();
                    content.transform(Matrix.getRotateInstance(Math.toRadians(90), height, 0));
                }
                case 180 -> content.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
                case 270 -> {
                    width = page.getMediaBox().getHeight();
                    height = page.getMediaBox().getWidth();
                    content.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));
                }
                default -> {
                    // page is not rotated
                }
            }

            float diagonalLength = (float) Math.sqrt(width * width + height * height);
            float fontSize = FONT_SIZE;
            float stringWidth = font.getStringWidth(mark) / 1000 * fontSize;

            // Long marks would run off the page at the default size, so shrink to fit.
            if (stringWidth > diagonalLength * 0.9f) {
                fontSize = fontSize * (diagonalLength * 0.9f) / stringWidth;
                stringWidth = font.getStringWidth(mark) / 1000 * fontSize;
            }

            float angle = (float) Math.atan2(height, width);
            float x = (diagonalLength - stringWidth) / 2;
            float y = -fontSize / 4;

            content.transform(Matrix.getRotateInstance(angle, 0, 0));

            PDExtendedGraphicsState state = new PDExtendedGraphicsState();
            state.setNonStrokingAlphaConstant(OPACITY);
            content.setGraphicsStateParameters(state);

            content.setFont(font, fontSize);
            content.setNonStrokingColor(Color.DARK_GRAY);
            content.beginText();
            content.newLineAtOffset(x, y);
            content.showText(mark);
            content.endText();
        }
    }
}
