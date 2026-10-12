package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionRequest;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.UpdateSurveyQuestionRequest;
import com.mgdsw.sdss.enums.QuestionType;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import com.mgdsw.sdss.service.SdssSurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

@Validated
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
@Tag(name = "Sdss Survey Questions Controller", description = "Endpoints to manage survey questions")
@RequestMapping("/api/v1/survey-questions")
public class SdssQuestionController {

    private final SdssSurveyQuestionService surveyQuestionService;
    private final SdssSurveyRoundService surveyRoundService;
    private final SdssSurveyService surveyService;

    @Operation(
            summary = "Create a new survey question",
            description = "This endpoint allows the creation of a new survey question with the specified details.",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Survey Questions Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the survey question", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Survey or round not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<SdssSurveyQuestionResponse>> createNewSurveyQuestion(@Valid @RequestBody SdssSurveyQuestionRequest questionRequest) {
        return ControllerResponseHelper.createResponse(
                surveyQuestionService.createNewSurveyQuestion(
                        questionRequest,
                        surveyService.findById(questionRequest.getSurveyId()),
                        surveyRoundService.findById(questionRequest.getRoundId()))
        );
    }

    @Operation(
            summary = "Update a survey question",
            description = "This endpoint allows updating an existing survey question with the given details.",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Survey Questions Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the survey question", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Survey question not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @PutMapping
    public ResponseEntity<SuccessResponse<SdssSurveyQuestionResponse>> updateSurveyQuestion(@Valid @RequestBody UpdateSurveyQuestionRequest updateRequest, @RequestParam Integer questionId) {
        return ControllerResponseHelper.createResponse(surveyQuestionService.updateSurveyQuestion(updateRequest, questionId));
    }

    @Operation(
            summary = "Get all question types based on user language",
            description = "This endpoint retrieves all question types and their keys based on the user's current language.",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Survey Questions Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of question types retrieved successfully", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @GetMapping(value = "/question-types")
    public ResponseEntity<SuccessResponse<List<Map<String, String>>>> retrieveAllQuestionTypesBasedOnUserLang() {
        return ControllerResponseHelper.okResponse(QuestionType.getAllWithKeysByLanguage());
    }

    @Operation(
            summary = "Delete a survey question",
            description = "This endpoint deletes an existing survey question by its ID.",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Survey Questions Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the survey question", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "Survey question not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteQuestionById(@RequestParam Integer questionId) {
        return ControllerResponseHelper.deleteResponse(surveyQuestionService.delete(questionId));
    }
}