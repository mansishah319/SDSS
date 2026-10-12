package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsRequest;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.service.SdssSurveyQuestionOptionsService;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

@Validated
@RestController
@RequestMapping("/api/v1/questions-options")
@Tag(name = "Sdss Questions Options Controller", description = "Controller to manage survey question options")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class SdssSurveyQuestionOptionsController {

    private final SdssSurveyQuestionOptionsService questionOptionsService;
    private final SdssSurveyQuestionService surveyQuestionService;

    @Operation(
            summary = "Create a new option for a specific question",
            description = "This endpoint creates a new option for a given question specified by questionId",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Questions Options Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Question not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<SdssSurveyQuestionOptionsResponse>> createNewOptionForSpecificQuestion(
            @Valid @RequestBody SdssSurveyQuestionOptionsRequest optionsRequest,
            @RequestParam Integer questionId) {
        return ControllerResponseHelper.createResponse(
                questionOptionsService.createNewOptionForSpecificQuestion(surveyQuestionService.findById(questionId), optionsRequest));
    }

    @Operation(
            summary = "Update an option",
            description = "This endpoint updates an existing survey question option specified by optionId",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Questions Options Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Option not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @PutMapping
    public ResponseEntity<SuccessResponse<SdssSurveyQuestionOptionsResponse>> updateOption(
            @Valid @RequestBody SdssSurveyQuestionOptionsRequest optionsRequest,
            @RequestParam Integer optionId) {
        return ControllerResponseHelper.updateResponse(questionOptionsService.update(optionsRequest, optionId, SdssSurveyQuestionOptionsResponse.class));
    }

    @Operation(
            summary = "Delete an option by ID",
            description = "This endpoint deletes a survey question option specified by optionId",
            security = @SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE),
            tags = {"Sdss Questions Options Controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid option ID", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Option not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteOptionById(@RequestParam Integer optionId) {
        return ControllerResponseHelper.deleteResponse(surveyQuestionService.delete(optionId));
    }
}