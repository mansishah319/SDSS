/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.documents.DocumentsCreationResponse;
import com.mgdsw.sdss.dto.documents.DocumentsResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeDocuments;
import com.mgdsw.sdss.service.ChallengeDocumentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UNDER_SCORE;
import static com.mgdsw.sdss.common.helper.util.FilesUtil.ATTACHMENT_NAME;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Documents Controller")
@RequestMapping("/api/v1/documents")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class DocumentsController {

    private final ChallengeDocumentsService challengeDocumentsService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<DocumentsCreationResponse>> uploadDocument(@RequestParam("file") MultipartFile file,
                                                                                     @RequestParam Integer challengeId) {
        return ControllerResponseHelper.createResponse(challengeDocumentsService.create(file, challengeId));
    }

    @PostMapping(value = "/upload-list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<List<DocumentsCreationResponse>>> uploadDocumentList(@RequestPart("files") List<MultipartFile> files,
                                                                                               @RequestParam Integer challengeId) {
        return ControllerResponseHelper.createResponse(challengeDocumentsService.createList(files, challengeId));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<String>> deleteDocumentById(@RequestParam Integer documentId) {
        return ControllerResponseHelper.okResponse(challengeDocumentsService.removeDocument(documentId));
    }

    @PostMapping(value = "/upload-list-without-challenge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<List<DocumentsCreationResponse>>> uploadDocumentList(@RequestPart("files") List<MultipartFile> files) {
        return ControllerResponseHelper.createResponse(challengeDocumentsService.createList(files, null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<DocumentsResponse>>> retrieveAllDocsByChallenge(
            @Parameter(
                    explode = Explode.TRUE,
                    name = "challengeId",
                    in = ParameterIn.QUERY,
                    required = false,
                    description = "Optional challengeId to filter documents by a specific challenge.",
                    style = ParameterStyle.FORM,
                    schema = @Schema(type = "Integer", defaultValue = "0")
            )
            @RequestParam(value = "challengeId", required = false) Integer challengeId
    ) {
        return ControllerResponseHelper.okResponse(challengeDocumentsService.retrieveAllDocsByChallenge(challengeId));
    }

    @GetMapping("/{documentId}/download")
    @Operation(summary = "Download Document", description = "Download a document by document ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Resource> retrieveUserProfile(
            @Parameter(description = "Document ID", required = true) @PathVariable Integer documentId
    ) {
        ChallengeDocuments documents = challengeDocumentsService.findById(documentId);
        ByteArrayResource resource = new ByteArrayResource(documents.getDocumentFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_NAME + documents.getName().replace(SPACE, UNDER_SCORE))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(documents.getDocumentFile().length)
                .body(resource);
    }
}
