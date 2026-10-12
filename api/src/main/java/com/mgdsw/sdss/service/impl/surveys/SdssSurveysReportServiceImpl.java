/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.helper.pdf.PdfGenerator;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.surveys.round.ReportSdssSurveyRoundResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.mapper.SdssSurveyReportMapper;
import com.mgdsw.sdss.mapper.SdssSurveyRoundMapper;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import com.mgdsw.sdss.service.SdssSurveyUserQuestionAnswersService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import com.mgdsw.sdss.service.SdssSurveysReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.helper.pdf.constants.HtmlTemplateConstants.SURVEY_DETAILS_REPORT_TEMPLATE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 20 Dec 2024
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class SdssSurveysReportServiceImpl implements SdssSurveysReportService {

    private final SdssSurveyUserQuestionAnswersService sdssSurveyUserQuestionAnswersService;
    private final SdssSurveyRoundService sdssSurveyRoundService;
    private final SdssSurveyUsersService sdssSurveyUsersService;
    private final SdssSurveyReportMapper reportMapper;
    private final SdssSurveyRoundMapper roundMapper;
    private final PdfGenerator pdfGenerator;

    @Override
    @Transactional(readOnly = true)
    public ReportSdssSurveyRoundResponse retrieveAllReportsForCurrentSurveyModerator(Integer roundId) {
        return retrieveAllReportsForTheCurrentSurveyModerator(roundId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateSurveyByRoundReportPDF(Integer roundId) {
        ReportSdssSurveyRoundResponse reportSdssSurveyRoundResponse = retrieveAllReportsForTheCurrentSurveyModerator(roundId);
        return pdfGenerator.createPdf(SURVEY_DETAILS_REPORT_TEMPLATE, reportSdssSurveyRoundResponse, Language.AR);
    }

    private ReportSdssSurveyRoundResponse retrieveAllReportsForTheCurrentSurveyModerator(Integer roundId) {
        SdssSurveyRound sdssSurveyRound = sdssSurveyRoundService.findById(roundId);
        this.checkIsTheCurrentUserAllowedToDoThat(sdssSurveyRound.getCreatedBy());
        ReportSdssSurveyRoundResponse reportSdssSurveyRoundResponse = reportMapper.toReportSdssSurveyRoundResponse(sdssSurveyRound);
        reportSdssSurveyRoundResponse.setSurveyUsers(sdssSurveyUsersService.getUsersWithAnswerStatus(sdssSurveyRound));
        reportSdssSurveyRoundResponse.setUsersSurveyAnswers(
                sdssSurveyUserQuestionAnswersService.retrieveAllAnswersForAllUsersByRoundId(roundId).
                        stream().map(reportMapper::mapToUserSurveyAnswersReportResponse).collect(Collectors.toList()));
        reportSdssSurveyRoundResponse.setRoundDetails(roundMapper.toRoundReportMainResponse(sdssSurveyRound));
        return reportSdssSurveyRoundResponse;
    }

    private void checkIsTheCurrentUserAllowedToDoThat(String createdBy) {
        if (!DpCommonUtil.isTheCurrentUserAllowedToDoThisProcess(createdBy)) {
            throw new CustomAuthorizationException(
                    String.format(
                            LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getErrorCode()
            );
        }
    }
}
