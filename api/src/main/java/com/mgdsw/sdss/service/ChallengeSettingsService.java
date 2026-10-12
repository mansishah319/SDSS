/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsLangResponse;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsResponse;
import com.mgdsw.sdss.dto.challenge.settings.SettingsTypeResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeSettings;
import com.mgdsw.sdss.enums.ChallengeSettingType;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface ChallengeSettingsService extends BaseService<Integer, ChallengeSettings> {

    List<ChallengeSettingsLangResponse> findAllByType(ChallengeSettingType type);

    List<SettingsTypeResponse> findAllChallengeSettingsTypes();

    ChallengeSettingsResponse customFindByIdForChallenge(Integer id);
}
