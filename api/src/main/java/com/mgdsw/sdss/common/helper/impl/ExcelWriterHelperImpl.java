/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.helper.impl;

import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.helper.ExcelWriterHelper;
import com.mgdsw.sdss.common.helper.annotations.ExcelCompositeField;
import com.mgdsw.sdss.common.helper.annotations.ExcelSheet;
import com.mgdsw.sdss.common.helper.annotations.ExcelSingleField;
import com.mgdsw.sdss.common.helper.dto.XlsxField;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
@Slf4j
@Component
public class ExcelWriterHelperImpl implements ExcelWriterHelper {

    @Override
    public <T> void write(List<T> data, ByteArrayOutputStream bos, String[] columnTitles, Workbook workbook) {

        long start = System.currentTimeMillis();
        this.checkDataExist(data);

        try {
            ExcelSheet annotation = data.get(0).getClass().getAnnotation(ExcelSheet.class);
            Sheet sheet = workbook.createSheet(annotation.value());
            List<XlsxField> xlsColumnFields = getFieldNamesForClass(data.get(0).getClass());
            int tempRowNo = 0;
            int recordBeginRowNo;
            int recordEndRowNo = 0;
            Cell columnTitleCell;
            Row row = sheet.createRow(tempRowNo);
            for (int i = 0; i < columnTitles.length; i++) {
                columnTitleCell = row.createCell(i);
                columnTitleCell.setCellValue(columnTitles[i]);
                columnTitleCell.setCellStyle(this.createHeaderCellStyle(workbook));
            }
            recordEndRowNo++;
            Class<?> clazz = data.get(0).getClass();
            for (T t : data) {
                tempRowNo = recordEndRowNo;
                recordBeginRowNo = tempRowNo;
                int maxListSize = getMaxListSize(t, xlsColumnFields, clazz);
                createCurrentRecord(xlsColumnFields, workbook, sheet, t, clazz, tempRowNo, recordBeginRowNo);
                recordEndRowNo = maxListSize + recordBeginRowNo;
            }
            autoSizeColumns(sheet, xlsColumnFields.size());
            workbook.write(bos);
            log.info("Xls file generated in [{}] seconds", processTime(start));
        } catch (Exception e) {
            log.info("Xls file write failed", e);
        }
    }


    /**
     * @param objectValue             objectValue
     * @param xlsCompositeColumnField xlsCompositeColumnField
     * @param childRow                childRow
     * @param workbook                workbook
     */
    private void writeCompositeFieldRow(Object objectValue, XlsxField xlsCompositeColumnField, Row childRow, Workbook workbook)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method nestedCompositeXlsMethod = getMethod(objectValue.getClass(), xlsCompositeColumnField);
        Object nestedCompositeValue = nestedCompositeXlsMethod.invoke(objectValue, (Object[]) null);
        Cell compositeNewCell = childRow.createCell(xlsCompositeColumnField.getCellIndex());
        setCellValue(compositeNewCell, nestedCompositeValue, workbook);

    }

    /**
     * @param childRow       childRow
     * @param xlsColumnField xlsColumnField
     * @param objectValue    objectValue
     * @param workbook       workbook
     */
    private void writeArrayFieldRow(Row childRow, XlsxField xlsColumnField, Object objectValue, Workbook workbook) {
        Cell newCell = childRow.createCell(xlsColumnField.getCellIndex());
        setCellValue(newCell, objectValue, workbook);
    }

    /**
     * @param mainRow        mainRow
     * @param xlsColumnField xlsColumnField
     * @param clazz          clazz
     * @param t              t
     * @param workbook       workbook
     * @param <T>            <T>
     */
    private <T> void writeSingleFieldRow(Row mainRow, XlsxField xlsColumnField, Class<?> clazz, T t, Workbook workbook)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Cell newCell = mainRow.createCell(xlsColumnField.getCellIndex());
        Method xlsMethod = getMethod(clazz, xlsColumnField);
        Object xlsObjValue = xlsMethod.invoke(t, (Object[]) null);
        setCellValue(newCell, xlsObjValue, workbook);

    }

    /**
     * @param xlsColumnFields xlsColumnFields
     * @param xlsColumnField  xlsColumnField
     * @param clazz           clazz
     * @param t               t
     * @param <T>             <T>
     * @return <T>
     */
    private <T> boolean isNextColumnAnArray(List<XlsxField> xlsColumnFields, XlsxField xlsColumnField, Class<?> clazz, T t)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        XlsxField nextXlsColumnField;
        int fieldsSize = xlsColumnFields.size();
        Method nestedXlsMethod;
        Object nestedObjValue;
        List<?> nestedObjValueList;

        if (xlsColumnFields.indexOf(xlsColumnField) < (fieldsSize - 1)) {
            nextXlsColumnField = xlsColumnFields.get(xlsColumnFields.indexOf(xlsColumnField) + 1);
            if (nextXlsColumnField.isAnArray()) {
                nestedXlsMethod = getMethod(clazz, nextXlsColumnField);
                nestedObjValue = nestedXlsMethod.invoke(t, (Object[]) null);
                nestedObjValueList = (List<?>) nestedObjValue;
                return nestedObjValueList.size() > 1;
            }
        }
        return xlsColumnFields.indexOf(xlsColumnField) == (fieldsSize - 1);
    }


    /**
     * @param cell     cell
     * @param objValue objValue
     * @param workbook workbook
     */
    private void setCellValue(Cell cell, Object objValue, Workbook workbook) {
        if (objValue != null) {
            setCellValueConditions(cell, workbook, objValue, workbook.getCreationHelper().createHyperlink(HyperlinkType.URL));
        }
    }

    /**
     * @param cell     cell
     * @param workbook workbook
     * @param objValue objValue
     * @param link     link
     */
    private void setCellValueConditions(Cell cell, Workbook workbook, Object objValue, Hyperlink link) {
        if (objValue instanceof String) {
            String cellValue = (String) objValue;
            cell.setCellStyle(getLeftAlignedCellStyle(workbook, getGenericFont(workbook)));
            if (cellValue.contains("https://") || cellValue.contains("http://")) {
                link.setAddress(cellValue);
                cell.setCellValue(cellValue);
                cell.setHyperlink(link);
            } else {
                cell.setCellValue(cellValue);
            }
        } else if (objValue instanceof Long) {
            cell.setCellValue((Long) objValue);
        } else if (objValue instanceof Integer) {
            cell.setCellValue((Integer) objValue);
        } else if (objValue instanceof Double) {
            Double cellValue = (Double) objValue;
            cell.setCellStyle(setCurrencyCellStyle(workbook));
            cell.setCellValue(cellValue);
        } else if (objValue instanceof Boolean) {
            cell.setCellStyle(getCenterAlignedCellStyle(workbook));
            if (objValue.equals(true)) {
                cell.setCellValue(1);
            } else {
                cell.setCellValue(0);
            }
        }
    }

    /**
     * @param clazz clazz
     * @return List<XlsxField>
     */
    private static List<XlsxField> getFieldNamesForClass(Class<?> clazz) {
        List<XlsxField> xlsColumnFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            XlsxField xlsColumnField = new XlsxField();
            if (Collection.class.isAssignableFrom(field.getType())) {
                xlsColumnField.setAnArray(true);
                ExcelCompositeField xlsCompositeField = field.getAnnotation(ExcelCompositeField.class);
                if (xlsCompositeField != null) {
                    xlsColumnField.setCellIndexFrom(xlsCompositeField.from());
                    xlsColumnField.setCellIndexTo(xlsCompositeField.to());
                    xlsColumnField.setComposite(true);
                } else {
                    ExcelSingleField xlsField = field.getAnnotation(ExcelSingleField.class);
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                }
            } else {
                ExcelSingleField xlsField = field.getAnnotation(ExcelSingleField.class);
                xlsColumnField.setAnArray(false);
                if (xlsField != null) {
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                    xlsColumnField.setComposite(false);
                }
            }
            xlsColumnField.setFieldName(field.getName());
            xlsColumnFields.add(xlsColumnField);
        }
        return xlsColumnFields;
    }

    /**
     * @param s string
     * @return String
     */
    private static String capitalize(String s) {
        if (s.isEmpty())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    /**
     * @param t               t
     * @param xlsColumnFields xlsColumnFields
     * @param aClass          aClass
     * @param <T>             generic pram
     * @return <T>
     */
    private <T> int getMaxListSize(T t, List<XlsxField> xlsColumnFields, Class<?> aClass)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<Integer> listSizes = new ArrayList<>();

        for (XlsxField xlsColumnField : xlsColumnFields) {
            if (xlsColumnField.isAnArray()) {
                Method method = getMethod(aClass, xlsColumnField);
                Object value = method.invoke(t, (Object[]) null);
                List<?> objects = (List<?>) value;
                if (objects.size() > 1) {
                    listSizes.add(objects.size());
                }
            }
        }

        if (listSizes.isEmpty()) {
            return 1;
        } else {
            return Collections.max(listSizes);
        }

    }

    /**
     * @param clazz          clazz
     * @param xlsColumnField xlsColumnField
     * @return Method
     */
    private Method getMethod(Class<?> clazz, XlsxField xlsColumnField) throws NoSuchMethodException {
        Method method;
        try {
            method = clazz.getMethod("get" + capitalize(xlsColumnField.getFieldName()));
        } catch (NoSuchMethodException nme) {
            method = clazz.getMethod(xlsColumnField.getFieldName());
        }

        return method;
    }

    /**
     * @param start start
     * @return long
     */
    private long processTime(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    /**
     * @param sheet       sheet
     * @param noOfColumns noOfColumns
     */
    private void autoSizeColumns(Sheet sheet, int noOfColumns) {
        for (int i = 0; i < noOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * @param sheet sheet
     * @param rowNo rowNo
     * @return Row
     */
    private Row getOrCreateNextRow(Sheet sheet, int rowNo) {
        Row row;
        if (sheet.getRow(rowNo) != null) {
            row = sheet.getRow(rowNo);
        } else {
            row = sheet.createRow(rowNo);
        }
        return row;
    }

    /**
     * @param workbook workbook
     * @return CellStyle
     */
    private CellStyle setCurrencyCellStyle(Workbook workbook) {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setWrapText(true);
        DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#0.00"));
        return currencyStyle;
    }

    /**
     * @param workbook workbook
     * @return Font
     */
    private Font getGenericFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    /**
     * @param workbook workbook
     * @return CellStyle
     */
    private CellStyle getCenterAlignedCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }

    /**
     * @param workbook workbook
     * @param font     font
     * @return CellStyle
     */
    private CellStyle getLeftAlignedCellStyle(Workbook workbook, Font font) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }

    /**
     * @param data data
     * @param <T>  List<T>
     */
    private <T> void checkDataExist(List<T> data) {
        if (data.isEmpty()) {
            log.error("No data received to write Xls file..");
            throw new CustomServiceException(ErrorCodes.EMPTY_LIST.getErrorCode(), ErrorCodes.EMPTY_LIST.getMessage());
        }
    }


    /**
     * @param xlsColumnFields  xlsColumnFields
     * @param workbook         workbook
     * @param sheet            sheet
     * @param t                t
     * @param clazz            clazz
     * @param tempRowNo        tempRowNo
     * @param recordBeginRowNo recordBeginRowNo
     * @param <T>              <T>
     */
    private <T> void createCurrentRecord(List<XlsxField> xlsColumnFields, Workbook workbook, Sheet sheet, T t, Class<?> clazz,
                                         int tempRowNo, int recordBeginRowNo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Row mainRow = sheet.createRow(tempRowNo++);
        boolean isRowNoToDecrease = false;

        for (XlsxField xlsColumnField : xlsColumnFields) {
            if (isNextColumnAnArray(xlsColumnFields, xlsColumnField, clazz, t)) {
                isRowNoToDecrease = true;
                tempRowNo = recordBeginRowNo + 1;
            }
            if (!xlsColumnField.isAnArray() && !xlsColumnField.isComposite()) {
                writeSingleFieldRow(mainRow, xlsColumnField, clazz, t, workbook);
            } else if (xlsColumnField.isAnArray() && !xlsColumnField.isComposite()) {
                writeSingleField(mainRow, isRowNoToDecrease, workbook, sheet, getObjValueList(xlsColumnField, clazz, t), xlsColumnField, tempRowNo);
            } else if (xlsColumnField.isAnArray()) {
                writeArrayFieldRow(mainRow, isRowNoToDecrease, workbook, sheet, getObjValueList(xlsColumnField, clazz, t), tempRowNo);
            }
        }
    }

    /**
     * @param mainRow           mainRow
     * @param isRowNoToDecrease isRowNoToDecrease
     * @param workbook          workbook
     * @param sheet             sheet
     * @param objValueList      objValueList
     * @param xlsColumnField    xlsColumnField
     * @param tempRowNo         tempRowNo
     */
    private void writeSingleField(Row mainRow, boolean isRowNoToDecrease, Workbook workbook, Sheet sheet,
                                  List<?> objValueList, XlsxField xlsColumnField, int tempRowNo) {
        boolean isFirstValue = true;
//                        looping through the items of the single array
        for (Object objectValue : objValueList) {
            Row childRow;
            if (isFirstValue) {
                childRow = mainRow;
                writeArrayFieldRow(childRow, xlsColumnField, objectValue, workbook);
                isFirstValue = false;

            } else if (isRowNoToDecrease) {
                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                writeArrayFieldRow(childRow, xlsColumnField, objectValue, workbook);
                isRowNoToDecrease = false;

            } else {
                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                writeArrayFieldRow(childRow, xlsColumnField, objectValue, workbook);
            }
        }
    }

    /**
     * @param mainRow           mainRow
     * @param isRowNoToDecrease isRowNoToDecrease
     * @param workbook          workbook
     * @param sheet             sheet
     * @param objValueList      objValueList
     * @param tempRowNo         tempRowNo
     */
    private void writeArrayFieldRow(Row mainRow, boolean isRowNoToDecrease, Workbook workbook,
                                    Sheet sheet, List<?> objValueList, int tempRowNo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        boolean isFirstRow = true;
        for (Object objectValue : objValueList) {
            Row childRow;
            List<XlsxField> xlsCompositeColumnFields = getFieldNamesForClass(objectValue.getClass());
            if (isFirstRow) {
                childRow = mainRow;
                for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                    writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, workbook);
                }
                isFirstRow = false;
            } else if (isRowNoToDecrease) {
                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                    writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, workbook);
                }
                isRowNoToDecrease = false;
            } else {
                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                    writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, workbook);
                }
            }
        }
    }

    /**
     * @param xlsColumnField xlsColumnField
     * @param clazz          clazz
     * @param t              t
     * @param <T>            <T>
     * @return ArrayList<Object>
     */
    private <T> List<?> getObjValueList(XlsxField xlsColumnField, Class<?> clazz, T t) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (List<?>) getMethod(clazz, xlsColumnField).invoke(t, (Object[]) null);
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        headerCellStyle.setFont(font);

        return headerCellStyle;
    }
}
