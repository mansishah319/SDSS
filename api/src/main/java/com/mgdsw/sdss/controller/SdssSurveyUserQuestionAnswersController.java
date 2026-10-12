/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.user.InvitedUserSingleAnswerRequest;
import com.mgdsw.sdss.dto.surveys.user.SdssSurveyUserQuestionAnswersResponse;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import com.mgdsw.sdss.service.SdssSurveyService;
import com.mgdsw.sdss.service.SdssSurveyUserQuestionAnswersService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 28 Nov 2024
 */

@Validated
@RestController
@RequestMapping("/api/v1/public/survey-user-answers")
@Tag(name = "Sdss Survey User Answers Controller")
@RequiredArgsConstructor
public class SdssSurveyUserQuestionAnswersController {

    private final SdssSurveyUserQuestionAnswersService service;
    private final SdssSurveyQuestionService surveyQuestionService;
    private final SdssSurveyRoundService surveyRoundService;
    private final SdssSurveyUsersService userSurveyService;
    private final SdssSurveyService surveyService;

    @PostMapping
    public ResponseEntity<SuccessResponse<List<SdssSurveyUserQuestionAnswersResponse>>> createNewSurveyUserQuestionAnswers(@Valid @RequestBody List<@Valid InvitedUserSingleAnswerRequest> userAnswers,
                                                                                                                           @RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey) {
        return ControllerResponseHelper.createResponse(service.createNewUserQuestionAnswers(userAnswers, userSurveyService.retrieveByUserInfoKey(userInfoKey)));
    }

    @GetMapping(value = "/retrieve-current-answers")
    public ResponseEntity<SuccessResponse<List<InvitedUserSingleAnswerRequest>>> retrieveUserQuestionAnswers(@RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey, @RequestParam Integer surveyId) {
        return ControllerResponseHelper.okResponse(
                service.retrieveCurrentActiveRoundUserAnswers(
                        userSurveyService.retrieveByUserInfoKey(userInfoKey),
                        surveyQuestionService.findByRound(surveyRoundService.findActiveRound(surveyService.findByIdForPublicApis(surveyId)))
                )
        );
    }

    @GetMapping(value = "/retrieve-my-questions-and-answers")
    public ResponseEntity<SuccessResponse<List<SdssSurveyUserQuestionAnswersResponse>>> retrieveCurrentUserQuestionAnswersBasedOnSurveyRound(@RequestHeader(name = "SurveyAuthorization", required = false) String userInfoKey,
                                                                                                                                             @RequestParam Integer roundId) {
        return ControllerResponseHelper.okResponse(
                service.retrieveCurrentUserQuestionAnswersBasedOnSurveyRound(
                        userSurveyService.retrieveByUserInfoKey(userInfoKey),
                        surveyQuestionService.findByRound(surveyRoundService.findByIdForPublicApis(roundId))
                )
        );
    }

    @GetMapping(value = "/retrieve-all-details-for-non-zero-probability-by-question")
    public ResponseEntity<SuccessResponse<List<SdssSurveyUserQuestionAnswersResponse>>> retrieveAllDetailsForNonZeroProbabilityByQuestion(@RequestParam Integer questionId) {
        return ControllerResponseHelper.okResponse(
                service.retrieveAllDetailsForNonZeroProbabilityByQuestion(surveyQuestionService.findById(questionId))
        );
    }

    @GetMapping(value = "/retrieve-all-details-for-non-zero-impact-by-question")
    public ResponseEntity<SuccessResponse<List<SdssSurveyUserQuestionAnswersResponse>>> retrieveAllDetailsForNonZeroImpactByQuestion(@RequestParam Integer questionId) {
        return ControllerResponseHelper.okResponse(
                service.retrieveAllDetailsForNonZeroImpactByQuestion(surveyQuestionService.findById(questionId))
        );
    }
}
