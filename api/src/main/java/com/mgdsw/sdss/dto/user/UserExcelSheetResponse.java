/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.helper.annotations.ExcelSheet;
import com.mgdsw.sdss.common.helper.annotations.ExcelSingleField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

import static com.mgdsw.sdss.util.UserUtils.EXCEL_SHEET_NAME;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet(value = EXCEL_SHEET_NAME)
public class UserExcelSheetResponse {

    @ExcelSingleField(columnIndex = 0)
    @Schema(description = "The ID of the User.")
    private Integer id;
    @ExcelSingleField(columnIndex = 1)
    private String title;
    @ExcelSingleField(columnIndex = 2)
    private String fullName;
    @ExcelSingleField(columnIndex = 3)
    @Schema(description = "The grpNumber of the user who created the user.")
    private String createdBy;
    @ExcelSingleField(columnIndex = 4)
    @Schema(description = "The creation date of the user.")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private String createdAt;
    @ExcelSingleField(columnIndex = 5)
    @Schema(description = "The username of the user who last modified this user.")
    private String updatedBy;
    @ExcelSingleField(columnIndex = 6)
    @Schema(description = "The last modified date of the user.")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private String updatedAt;
    @ExcelSingleField(columnIndex = 7)
    private String grpNumber;
    @ExcelSingleField(columnIndex = 8)
    private String language;
    @ExcelSingleField(columnIndex = 9)
    private String status;
    @ExcelSingleField(columnIndex = 10)
    private String mobile;
    @ExcelSingleField(columnIndex = 11)
    private String email;
    @ExcelSingleField(columnIndex = 12)
    private String type;
    @ExcelSingleField(columnIndex = 13)
    private List<String> roles;
    @ExcelSingleField(columnIndex = 14)
    private String expertLevel;

    @ExcelSingleField(columnIndex = 15)
    private String gradeName;
    @ExcelSingleField(columnIndex = 16)
    private String sectorName;
    @ExcelSingleField(columnIndex = 17)
    private String departmentName;
    @ExcelSingleField(columnIndex = 18)
    private String departmentType;
    @ExcelSingleField(columnIndex = 19)
    private String sectorShortName;
}
