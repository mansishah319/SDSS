/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.entity.challenge.ChallengeReminder;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.Operations;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChallengeReminderService extends BaseService<Integer, ChallengeReminder> {

    void create(List<String> challengeRemindersDateTimeList, Challenges challenges, Operations operations);

    void removeAllByChallenge(Challenges challengeNeedToRemove);

    List<ChallengeReminder> findByChallenges(Challenges challenges);
}
