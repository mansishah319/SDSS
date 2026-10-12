/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.helper.util.FilesUtil;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.custom.CustomSdssSurveyReportExcel;
import com.mgdsw.sdss.dto.surveys.round.ReportSdssSurveyRoundResponse;
import com.mgdsw.sdss.service.SdssSurveysReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UTF8;
import static com.mgdsw.sdss.common.helper.util.FilesUtil.ATTACHMENT_NAME;
import static com.mgdsw.sdss.common.helper.util.FilesUtil.DATE_TIME_FORMATTER;
import static com.mgdsw.sdss.util.UserUtils.EXCEL_SHEET_NAME;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 20 Dec 2024
 */

@Validated
@RestController
@RequestMapping("/api/v1/survey-reports")
@Tag(name = "Sdss Survey Reports Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SdssSurveysReportController {

    private final SdssSurveysReportService sdssSurveysReportService;

    @GetMapping(value = "/by-round")
    public ResponseEntity<SuccessResponse<ReportSdssSurveyRoundResponse>> retrieveAllReportsForCurrentSurveyModerator(@RequestParam Integer roundId) {
        return ControllerResponseHelper.okResponse(sdssSurveysReportService.retrieveAllReportsForCurrentSurveyModerator(roundId));
    }

    @GetMapping(value = "/download")
    public void downloadReport(HttpServletResponse response, @RequestParam Integer roundId) throws IOException {
        response.setContentType(FilesUtil.CONTENT_TYPE);
        response.setHeader(FilesUtil.CONTENT_DISPOSITION,
                ATTACHMENT_NAME + EXCEL_SHEET_NAME + LocalDateTime.now(ZoneId.of("Asia/Dubai")).format(DATE_TIME_FORMATTER) + FilesUtil.EXCEL_FILE_EXT);
        response.setCharacterEncoding(UTF8);
        response.setHeader(FilesUtil.ACCESS_CONTROL, FilesUtil.CONTENT_DISPOSITION_AND_SUGGESTED_FILENAME);
        IOUtils.copy(CustomSdssSurveyReportExcel.generateExcelSheetReport(sdssSurveysReportService.retrieveAllReportsForCurrentSurveyModerator(roundId)), response.getOutputStream());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF file retrieved successfully",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_PDF_VALUE,
                                    schema = @Schema(type = "string", format = "binary", description = "PDF file")
                            ),
                            @Content(mediaType = MediaType.ALL_VALUE,
                                    schema = @Schema(type = "string", format = "binary", description = "Binary data")
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Pdf File not generated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Operation(summary = "Convert HTML to PDF")
    @GetMapping(value = "/details-pdf", produces = MediaType.APPLICATION_PDF_VALUE + ";charset=UTF-8")
    public ResponseEntity<byte[]> convertHtmlToPdf(@RequestParam Integer roundId) {
        // Generate a meaningful filename (you can customize this)
        String fileName = "survey-report-round-" + roundId + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .body(sdssSurveysReportService.generateSurveyByRoundReportPDF(roundId));
    }
}
