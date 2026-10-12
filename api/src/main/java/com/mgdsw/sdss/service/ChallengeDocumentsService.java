/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.documents.DocumentsCreationResponse;
import com.mgdsw.sdss.dto.documents.DocumentsResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeDocuments;
import com.mgdsw.sdss.entity.challenge.Challenges;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChallengeDocumentsService extends BaseService<Integer, ChallengeDocuments> {

    DocumentsCreationResponse create(MultipartFile file, Integer challengeId);

    List<DocumentsCreationResponse> createList(List<MultipartFile> files, Integer challengeId);

    void updateDocumentWithCreatedChallenge(List<Integer> documentsIdList, Challenges challenges);

    List<ChallengeDocuments> retrieveAllDocsByChallenge(Challenges challenge);

    void deleteAllByChallengesExceptIds(Challenges challengeNeedToRemove, List<Integer> ids);

    List<DocumentsResponse> retrieveAllDocsByChallenge(Integer challengeId);

    String removeDocument(Integer documentId);

    DocumentsResponse getDocumentsResponse(Integer challengeId);
}
