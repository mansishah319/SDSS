/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.user.UserInvitationExpertisesRequest;
import com.mgdsw.sdss.entity.challenge.ChallengeExpertises;
import com.mgdsw.sdss.entity.challenge.Challenges;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */
public interface ChallengeExpertisesService extends BaseService<Integer, ChallengeExpertises> {

    void create(Challenges challenges, UserInvitationExpertisesRequest userInvitationExpertisesRequest);
}
