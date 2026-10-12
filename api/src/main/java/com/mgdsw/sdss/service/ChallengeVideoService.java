/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.video.VideosDashboardResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeVideo;
import com.mgdsw.sdss.entity.challenge.Challenges;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChallengeVideoService extends BaseService<Integer, ChallengeVideo> {

    void create(List<String> linkList, Challenges challenges);

    void removeAllByChallenge(Challenges challengeNeedToRemove);

    List<VideosDashboardResponse> retrieveAllYoutubeVideosByChallengeId(Integer challengeId);
}
