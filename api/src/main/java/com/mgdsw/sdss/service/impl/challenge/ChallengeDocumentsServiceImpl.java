/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.security.FileSecurityValidator;
import com.mgdsw.sdss.dto.documents.DocumentsCreationResponse;
import com.mgdsw.sdss.dto.documents.DocumentsResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeDocuments;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.FilesPathTypes;
import com.mgdsw.sdss.repository.challenge.ChallengeDocumentsRepository;
import com.mgdsw.sdss.service.ChallengeDocumentsService;
import com.mgdsw.sdss.service.ChallengesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Messages.SuccessMessages.DELETED_SUCCESSFULLY;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
@Service
public class ChallengeDocumentsServiceImpl extends AbstractBaseServiceImpl<Integer, ChallengeDocuments> implements ChallengeDocumentsService {

    private final ChallengeDocumentsRepository challengeDocumentsRepository;
    private final ChallengesService challengesService;
    private final FileSecurityValidator fileSecurityValidator;

    public ChallengeDocumentsServiceImpl(ChallengeDocumentsRepository challengeDocumentsRepository,
                                         @Lazy ChallengesService challengesService,
                                         FileSecurityValidator fileSecurityValidator) {
        this.challengeDocumentsRepository = challengeDocumentsRepository;
        this.challengesService = challengesService;
        this.fileSecurityValidator = fileSecurityValidator;
    }

    @Override
    protected ChallengeDocumentsRepository repository() {
        return challengeDocumentsRepository;
    }

    @Override
    @Transactional
    public DocumentsCreationResponse create(MultipartFile file, Integer challengeId) {
        return this.createNewDocument(file, challengeId);
    }

    @Override
    public List<DocumentsCreationResponse> createList(List<MultipartFile> files, Integer challengeId) {
        return files.stream().map(file -> this.createNewDocument(file, challengeId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDocumentWithCreatedChallenge(List<Integer> documentsIdList, Challenges challenges) {
        challengeDocumentsRepository.updateChallengesForDocuments(challenges, documentsIdList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeDocuments> retrieveAllDocsByChallenge(Challenges challenge) {
        return this.retrieveAllDocsByTheChallenge(challenge);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentsResponse> retrieveAllDocsByChallenge(Integer challengeId) {
        try {
            Map<Integer, DocumentsResponse> uniqueResponses = new HashMap<>();
            if (Objects.nonNull(challengeId)) {
                // Single challengeId case
                List<DocumentsCreationResponse> docsList = this.retrieveAllDocsByTheChallenge(challengesService.findById(challengeId))
                        .stream()
                        .map(doc -> modelMapper.map(doc, DocumentsCreationResponse.class))
                        .collect(Collectors.toList());

                uniqueResponses.put(challengeId, DocumentsResponse.builder()
                        .challengeId(challengeId)
                        .documentsResponseList(docsList)
                        .build());
            } else {
                // All-documents case
                List<ChallengeDocuments> allDocuments = challengeDocumentsRepository.findAll();
                allDocuments.forEach(doc -> {
                    Integer docChallengeId = (doc.getChallenges() != null) ? doc.getChallenges().getId() : null;

                    if (docChallengeId != null) {
                        uniqueResponses.computeIfAbsent(docChallengeId, id -> DocumentsResponse.builder()
                                .challengeId(id)
                                .documentsResponseList(new ArrayList<>())
                                .build()
                        ).getDocumentsResponseList().add(modelMapper.map(doc, DocumentsCreationResponse.class));
                    }
                });
            }

            // Return a list of unique DocumentsResponse values
            return new ArrayList<>(uniqueResponses.values());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ArrayList<>();
        }
    }


    @Override
    @Transactional
    public void deleteAllByChallengesExceptIds(Challenges challengeNeedToRemove, List<Integer> ids) {
        challengeDocumentsRepository.deleteAllByChallengesExceptIds(challengeNeedToRemove, ids);
    }

    private DocumentsCreationResponse createNewDocument(MultipartFile file, Integer challengeId) {
        return super.create(this.saveChallengeDocuments(file, challengeId), DocumentsCreationResponse.class);
    }

    @Override
    @Transactional
    public String removeDocument(Integer documentId) {
        // Find the document
        ChallengeDocuments document = super.findById(documentId);

        // Remove it from the parent entity (Challenges)
        Challenges challenge = document.getChallenges();
        if (Objects.nonNull(challenge)) {
            challenge.getChallengeDocuments().remove(document);
        }
        // Finally, delete the document
        challengeDocumentsRepository.delete(document);
        return super.translator.getMessage(DELETED_SUCCESSFULLY);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentsResponse getDocumentsResponse(Integer challengeId) {
        List<ChallengeDocuments> documents = challengeDocumentsRepository.findDocumentsByChallengeId(challengeId);
        List<DocumentsCreationResponse> documentsResponseList = documents.stream()
                .map(doc -> DocumentsCreationResponse.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .path(doc.getPath())
                        .type(doc.getType())
                        .documentFile(doc.getDocumentFile())
                        .challengeId(challengeId)
                        .build())
                .collect(Collectors.toList());

        return DocumentsResponse.builder()
                .challengeId(challengeId)
                .documentsResponseList(documentsResponseList)
                .build();
    }

    private ChallengeDocuments saveChallengeDocuments(MultipartFile file, Integer challengeId) {
        try {
            // Validate file security before processing
            fileSecurityValidator.validateFileSecurity(file);

            String filename = file.getOriginalFilename();
            return ChallengeDocuments.builder()
                    .challenges(Objects.nonNull(challengeId) ? challengesService.findById(challengeId) : null)
                    .type(FilenameUtils.getExtension(filename))
                    .path(FilesPathTypes.DB.name())
                    .documentFile(file.getBytes())
                    .name(filename)
                    .build();
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new CustomDataException(ErrorCodes.UPLOADED_ERROR.getMessage(), ErrorCodes.UPLOADED_ERROR.getErrorCode());
        }
    }

    private List<ChallengeDocuments> retrieveAllDocsByTheChallenge(Challenges challenge) {
        return challengeDocumentsRepository.findByChallenges(challenge);
    }
}