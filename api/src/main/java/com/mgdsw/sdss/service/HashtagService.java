/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.hashtag.HashtagCreationRequest;
import com.mgdsw.sdss.dto.hashtag.HashtagCreationResponse;
import com.mgdsw.sdss.entity.challenge.Hashtag;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */
public interface HashtagService extends BaseService<Integer, Hashtag> {

    HashtagCreationResponse create(HashtagCreationRequest request);

    List<Hashtag> saveList(List<String> hashtagTexts);
}
