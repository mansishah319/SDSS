/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.helper.pdf.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.helper.pdf.PdfGenerator;
import com.mgdsw.sdss.common.helper.pdf.constants.CustomFontConstants;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import com.mgdsw.sdss.enums.Language;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;
import static com.mgdsw.sdss.common.helper.pdf.constants.HtmlTemplateConstants.REPORTS;

@Slf4j
@Component
@Qualifier("htmlToPdfGenerator")
@AllArgsConstructor
public class HtmlToPdfGenerator implements PdfGenerator {
    private static final String UTF_8 = "UTF-8";
    private final TemplateEngine templateEngine;

    @Override
    public byte[] createPdf(String template, Object data, Language language) {
        String reportTemplatePath = REPORTS + template;
        log.info("reportTemplatePath : {}", reportTemplatePath);
        Context context = new Context();
        context.setVariable("data", data);
        String renderedHtmlContent = templateEngine.process(reportTemplatePath, context);
        log.info("renderedHtmlContent : {}", renderedHtmlContent);

        if (language != null && language.equals(Language.AR)) {
            return this.generateArabicPdf(renderedHtmlContent);
        } else {
            return this.generateEnglishPdf(renderedHtmlContent);
        }
    }

    private byte[] generateArabicPdf(String renderedHtmlContent) {
        try {
            log.info("Generating Arabic PDF with content: {}", renderedHtmlContent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            document.setMargins(10, 10, 10, 10);
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            XMLWorkerHelper.getInstance().parseXHtml(
                    writer,
                    document,
                    new ByteArrayInputStream(renderedHtmlContent.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8,
                    new UnicodeFontProvider()
            );
            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error creating Arabic PDF", e);
            throw new CustomServiceException("Error creating Arabic PDF.", ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode());
        }
    }

    private static class UnicodeFontProvider extends XMLWorkerFontProvider {
        public UnicodeFontProvider() {
            super(XMLWorkerFontProvider.DONTLOOKFORFONTS);
            loadAllFonts();
            this.defaultEncoding = BaseFont.IDENTITY_H;
        }

        private void loadAllFonts() {
            FontLoader.loadFonts(this::registerFontSafe);
        }

        private void registerFontSafe(String fontPath) {
            FontLoader.loadFont(fontPath,
                    path -> {
                        try {
                            this.register(path);
                            log.info("Successfully loaded font as file: {}", path);
                        } catch (Exception e) {
                            log.error("Failed to load font as file: {}", path, e);
                        }
                    },
                    (path, tempFile) -> {
                        this.register(tempFile.getAbsolutePath());
                        log.info("Successfully loaded font from classpath: {}", path);
                    }
            );
        }
    }

    private byte[] generateEnglishPdf(String renderedHtmlContent) {
        try {
            String xHtml = convertToXhtml(renderedHtmlContent);
            ITextRenderer renderer = new ITextRenderer();
            loadFontsToRenderer(renderer);
            renderer.setDocumentFromString(xHtml);
            renderer.layout();
            log.info("xHtml English content : {}", xHtml);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (IOException | DocumentException e) {
            log.error("Error creating English PDF", e);
            throw new CustomServiceException("Error creating English PDF.", ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode());
        }
    }

    private void loadFontsToRenderer(ITextRenderer renderer) {
        try {
            renderer.getFontResolver().addFont("Code39.ttf", IDENTITY_H, EMBEDDED);
            FontLoader.loadFonts(fontPath -> loadFontToRenderer(renderer, fontPath));
        } catch (Exception e) {
            log.error("Error loading fonts to renderer", e);
        }
    }

    private void loadFontToRenderer(ITextRenderer renderer, String fontPath) {
        FontLoader.loadFont(fontPath,
                path -> {
                    try {
                        renderer.getFontResolver().addFont(path, IDENTITY_H, EMBEDDED);
                        log.info("Successfully loaded font as file to renderer: {}", path);
                    } catch (Exception e) {
                        log.error("Failed to load font to renderer as file: {}", path, e);
                    }
                },
                (path, tempFile) -> {
                    try {
                        renderer.getFontResolver().addFont(tempFile.getAbsolutePath(), IDENTITY_H, EMBEDDED);
                        log.info("Successfully loaded font to renderer from classpath: {}", path);
                    } catch (Exception e) {
                        log.error("Failed to load font to renderer from classpath: {}", path, e);
                    }
                }
        );
    }

    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setOutputEncoding(UTF_8);
        tidy.setInputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }
}

// New utility class to handle font loading
@Log4j2
class FontLoader {
    private static final String[] FONT_PATHS = {
            CustomFontConstants.NOTO_ARABIC_FONT,
            CustomFontConstants.VERDANA_FONT,
            CustomFontConstants.DUBAI_BOLD,
            CustomFontConstants.DUBAI_REGULAR,
            CustomFontConstants.ARIAL
    };

    @FunctionalInterface
    public interface FileFontHandler {
        void handle(String fontPath);
    }

    @FunctionalInterface
    public interface ClasspathFontHandler {
        void handle(String fontPath, java.io.File tempFile);
    }

    public static void loadFonts(FileFontHandler fontHandler) {
        for (String fontPath : FONT_PATHS) {
            fontHandler.handle(fontPath);
        }
    }

    public static void loadFont(String fontPath, FileFontHandler fileHandler, ClasspathFontHandler classpathHandler) {
        try {
            String classpathPath = fontPath.startsWith("/") ? fontPath.substring(1) : fontPath;
            try (java.io.InputStream fontStream = FontLoader.class.getClassLoader().getResourceAsStream(classpathPath)) {
                if (fontStream != null) {
                    java.io.File tempFile = createTempFontFile(fontStream, fontPath);
                    classpathHandler.handle(fontPath, tempFile);
                } else {
                    fileHandler.handle(fontPath);
                }
            }
        } catch (Exception e) {
            log.error("Error loading font: {}", fontPath, e);
        }
    }

    private static java.io.File createTempFontFile(java.io.InputStream fontStream, String fontPath) throws IOException {
        String extension = getFileExtension(fontPath);
        java.nio.file.Path tempDirectory = createPrivateTempDirectory();
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile(tempDirectory, "font_", extension);
        try (java.io.OutputStream out = java.nio.file.Files.newOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fontStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        }
        java.io.File fontFile = tempFile.toFile();
        fontFile.deleteOnExit();
        return fontFile;
    }

    private static java.nio.file.Path createPrivateTempDirectory() throws IOException {
        java.nio.file.Path baseDirectory = java.nio.file.Paths.get(
                System.getProperty("java.io.tmpdir"), "sdss-font-cache");

        java.nio.file.Files.createDirectories(baseDirectory);

        try {
            java.nio.file.Files.setPosixFilePermissions(baseDirectory, java.util.EnumSet.of(
                    java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                    java.nio.file.attribute.PosixFilePermission.OWNER_WRITE,
                    java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE));
        } catch (UnsupportedOperationException ignored) {
            java.io.File directory = baseDirectory.toFile();

            if (!directory.setReadable(false, false)
                    || !directory.setWritable(false, false)
                    || !directory.setExecutable(false, false)
                    || !directory.setReadable(true, true)
                    || !directory.setWritable(true, true)
                    || !directory.setExecutable(true, true)) {
                throw new IOException("Failed to set secure permissions on temporary directory: " + baseDirectory);
            }
        }

        return baseDirectory;
    }

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? ".ttf" : filename.substring(lastDotIndex);
    }
}
