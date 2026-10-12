/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.entity.challenge.ChallengeHashtag;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.challenge.Hashtag;
import com.mgdsw.sdss.enums.Operations;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChallengeHashtagService extends BaseService<Integer, ChallengeHashtag> {

    void create(List<Hashtag> hashtagList, Challenges challenges, Operations operations);

    List<String> retrieveHashtagTextsByChallenges(Challenges challenges);
}
