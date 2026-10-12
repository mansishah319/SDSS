/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.custom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.questions.ReportSdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.dto.surveys.round.ReportSdssSurveyRoundResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to generate Excel reports for SDSS surveys.
 *
 * @author Mohammed Abdu
 * @version 0.1
 */
@Log4j2
public final class CustomSdssSurveyReportExcel {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private CustomSdssSurveyReportExcel() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }

    private static final short HEADER_BACKGROUND_COLOR = IndexedColors.GREEN.getIndex();
    private static final short HEADER_FONT_COLOR = IndexedColors.WHITE.getIndex();

    /**
     * Generates an Excel report for the given survey round response.
     *
     * @param response the survey round response
     * @return ByteArrayInputStream containing the Excel report
     */
    public static ByteArrayInputStream generateExcelSheetReport(ReportSdssSurveyRoundResponse response) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = createHeaderCellStyle(workbook);
            CellStyle dataStyle = createDataCellStyle(workbook);

            createSurveySheet(workbook, response, headerStyle, dataStyle);
            createQuestionsSheet(workbook, response.getQuestions(), headerStyle, dataStyle);

            workbook.write(bos);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            log.error("Error while writing data to Excel file: {}", e.getMessage(), e);
            throw new CustomServiceException("3001", e.getMessage());
        }
    }

    private static void createSurveySheet(Workbook workbook, ReportSdssSurveyRoundResponse response, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet(getLocalizedString("Survey Details", "تفاصيل التحدي"));

        // Set the sheet direction based on the user's language
        if (!DpCommonUtil.isCurrentUserLanguageIsEnglish()) {
            sheet.setRightToLeft(true);
        }

        int rowIndex = 0;

        // Create header row
        createHeaderRow(sheet, new String[]{
                getLocalizedString("Field Name", "إسم الحقل"),
                getLocalizedString("Field Details", "تفاصيل القيمة")
        }, rowIndex, headerStyle);
        rowIndex++;

        // Add rows for survey details
        addRow(sheet, rowIndex++, getLocalizedString("Title (EN)", "العنوان (EN)"), response.getTitleEn(), dataStyle);
        addRow(sheet, rowIndex++, getLocalizedString("Title (AR)", "العنوان (AR)"), response.getTitleAr(), dataStyle);
        addRow(sheet, rowIndex++, getLocalizedString("Description (EN)", "الوصف (EN)"), response.getDescriptionEn(), dataStyle);
        addRow(sheet, rowIndex++, getLocalizedString("Description (AR)", "الوصف (AR)"), response.getDescriptionAr(), dataStyle);
        addRow(sheet, rowIndex++, getLocalizedString("Status", "الحالة"), Objects.toString(response.getStatus(), ""), dataStyle);
        addRow(sheet, rowIndex, getLocalizedString("Total Invited Users", "إجمالي المستخدمين المدعوين"),
                Objects.toString(response.getTotalNumberOfInvitedUsers(), ""), dataStyle);
        // Apply borders only to content cells and hide gridlines
        applyBordersToContentOnly(sheet, rowIndex + 1, 2, headerStyle, dataStyle); // 2 columns
    }

    private static void createQuestionsSheet(Workbook workbook, List<ReportSdssSurveyQuestionResponse> questions, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet(getLocalizedString("Questions", "الاسئلة"));

        // Set the sheet direction based on the user's language
        if (!DpCommonUtil.isCurrentUserLanguageIsEnglish()) {
            sheet.setRightToLeft(true);
        }

        int rowIndex = 0;

        String[] headers = {
                getLocalizedString("Question ID", "معرف السؤال"),
                getLocalizedString("Title (EN)", "العنوان (EN)"),
                getLocalizedString("Title (AR)", "العنوان (AR)"),
                getLocalizedString("Question Type", "تصنيف السؤال"),
                getLocalizedString("Show Public", "إظهار للعامة"),
                getLocalizedString("Option (EN)", "الخيار (EN)"),
                getLocalizedString("Option (AR)", "الخيار (AR)"),
                getLocalizedString("User Statistics Key", "مفتاح احصائيات المستخدم"),
                getLocalizedString("User Statistics Value", "قيمة احصائيات المستخدم")
        };
        createHeaderRow(sheet, headers, rowIndex++, headerStyle);

        for (ReportSdssSurveyQuestionResponse question : questions) {
            rowIndex = writeQuestionData(sheet, question, rowIndex, dataStyle);
        }

        autoSizeColumns(sheet, headers.length);
        // Apply borders only to content cells and hide gridlines
        applyBordersToContentOnly(sheet, rowIndex, headers.length, headerStyle, dataStyle);
    }

    private static int writeQuestionData(Sheet sheet, ReportSdssSurveyQuestionResponse question, int rowIndex, CellStyle dataStyle) {
        Row questionRow = sheet.createRow(rowIndex++);
        createCell(questionRow, 0, question.getId(), dataStyle);
        createCell(questionRow, 1, question.getTitleEn(), dataStyle);
        createCell(questionRow, 2, question.getTitleAr(), dataStyle);
        createCell(questionRow, 3, question.getQuestionType().getAr(), dataStyle);
        createCell(questionRow, 4, question.getShowPublicQuestions(), dataStyle);

        if (question.getOptions() != null) {
            for (SdssSurveyQuestionOptionsResponse option : question.getOptions()) {
                Row optionRow = sheet.createRow(rowIndex++);
                createCell(optionRow, 5, option.getOptionEn(), dataStyle);
                createCell(optionRow, 6, option.getOptionAr(), dataStyle);
            }
        }

        if (question.getUserAnswersStatistics() != null) {
            for (Map.Entry<String, Serializable> entry : question.getUserAnswersStatistics().entrySet()) {
                Row statRow = sheet.createRow(rowIndex++);
                writeStatisticsData(statRow, entry, dataStyle);
            }
        }

        return rowIndex;
    }

    private static void writeStatisticsData(Row statRow, Map.Entry<String, Serializable> entry, CellStyle dataStyle) {
        try {
            // Serialize the key as usual
            String keyJson = OBJECT_MAPPER.writeValueAsString(entry.getKey());

            // Serialize the value to a JsonNode first
            JsonNode rootNode = OBJECT_MAPPER.valueToTree(entry.getValue());

            // If the value is a list, clean each object in the list
            cleanJsonFields(rootNode);

            // Serialize the modified JsonNode to a JSON string
            String valueJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

            // Write the key and value JSON to the respective cells
            createCell(statRow, 7, keyJson, dataStyle);
            createCell(statRow, 8, valueJson, dataStyle);
        } catch (Exception e) {
            log.error("Error converting userAnswersStatistics to JSON: {}", e.getMessage(), e);
        }
    }

    private static void cleanJsonFields(JsonNode rootNode) {
        if (rootNode.isArray()) {
            // Loop through each element in the array
            for (JsonNode item : rootNode) {
                removeFields(item);
            }
        } else {
            // If it's not an array, handle the single object case
            removeFields(rootNode);
        }
    }

    private static void removeFields(JsonNode node) {
        if (node.has("updatedAt")) {
            ((ObjectNode) node).remove("updatedAt");
        }
        if (node.has("createdAt")) {
            ((ObjectNode) node).remove("createdAt");
        }
        if (node.has("updatedBy")) {
            ((ObjectNode) node).remove("updatedBy");
        }
        if (node.has("createdBy")) {
            ((ObjectNode) node).remove("createdBy");
        }
    }

    private static void createHeaderRow(Sheet sheet, String[] headers, int rowIndex, CellStyle headerStyle) {
        Row header = sheet.createRow(rowIndex);
        header.setHeightInPoints(25); // Increased height
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);

            // Calculate and set column width to fit header content
            int headerLength = headers[i].length();
            int adjustedWidth = (headerLength + 2) * 256; // Add padding for better fit
            sheet.setColumnWidth(i, Math.min(adjustedWidth, 10000)); // Cap width at 10,000 to avoid overly wide columns
        }
    }

    private static void addRow(Sheet sheet, int rowIndex, String field, Object value, CellStyle dataStyle) {
        Row row = sheet.createRow(rowIndex);
        createCell(row, 0, field, dataStyle);
        createCell(row, 1, value, dataStyle);
    }

    private static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        font.setColor(HEADER_FONT_COLOR);
        // Set the font size (e.g., 14 points)
        font.setFontHeightInPoints((short) 17);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(HEADER_BACKGROUND_COLOR);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Dynamically set alignment based on language
        if (DpCommonUtil.isCurrentUserLanguageIsEnglish()) {
            style.setAlignment(HorizontalAlignment.LEFT);
        } else {
            style.setAlignment(HorizontalAlignment.RIGHT);
        }
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static String getLocalizedString(String english, String arabic) {
        return DpCommonUtil.isCurrentUserLanguageIsEnglish() ? english : arabic;
    }

    private static void applyBordersToContentOnly(Sheet sheet, int rowCount, int columnCount, CellStyle headerStyle, CellStyle dataStyle) {
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    if (cell == null) {
                        cell = row.createCell(colIndex);
                    }

                    // Apply headerStyle to the first row (assumed to be the header), otherwise apply dataStyle
                    if (rowIndex == 0) {
                        cell.setCellStyle(headerStyle);
                    } else {
                        cell.setCellStyle(dataStyle);
                    }
                }
            }
        }
        // Hide gridlines
        sheet.setDisplayGridlines(false);
    }
}
