/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.challenge;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.ChallengeSettings;
import com.mgdsw.sdss.enums.ChallengeSettingType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface ChallengeSettingsRepository extends BaseRepository<ChallengeSettings, Integer> {

    List<ChallengeSettings> findAllByType(ChallengeSettingType type);
}
