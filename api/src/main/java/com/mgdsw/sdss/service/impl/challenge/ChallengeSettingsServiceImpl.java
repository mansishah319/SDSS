/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsLangResponse;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsResponse;
import com.mgdsw.sdss.dto.challenge.settings.SettingsTypeResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeSettings;
import com.mgdsw.sdss.enums.ChallengeSettingType;
import com.mgdsw.sdss.repository.challenge.ChallengeSettingsRepository;
import com.mgdsw.sdss.service.ChallengeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ChallengeSettingsServiceImpl extends AbstractBaseServiceImpl<Integer, ChallengeSettings> implements ChallengeSettingsService {

    private final ChallengeSettingsRepository challengeSettingsRepository;

    @Override
    protected ChallengeSettingsRepository repository() {
        return challengeSettingsRepository;
    }

    @Override
    public List<ChallengeSettingsLangResponse> findAllByType(ChallengeSettingType type) {
        return DpCommonUtil.getUserCurrentLocale().equals(Locale.US) ?
                challengeSettingsRepository.findAllByType(type)
                .stream().map(challengeSetting ->
                        this.handleResponseBasedOnLanguageValues(
                                challengeSetting, challengeSetting.getNameEn(),
                                challengeSetting.getType().getEn()
                        )
                ).collect(Collectors.toList()) :
                challengeSettingsRepository.findAllByType(type)
                .stream().map(challengeSetting ->
                        this.handleResponseBasedOnLanguageValues(
                                challengeSetting, challengeSetting.getNameAr(),
                                challengeSetting.getType().getAr()
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public List<SettingsTypeResponse> findAllChallengeSettingsTypes() {
        return Arrays.stream(ChallengeSettingType.values())
                .map(type -> SettingsTypeResponse.builder()
                        .name(type.name().toUpperCase())
                        .arName(type.getAr())
                        .enName(type.getEn())
                        .localName(DpCommonUtil.isCurrentUserLanguageIsEnglish() ? type.getEn() : type.getAr())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public ChallengeSettingsResponse customFindByIdForChallenge(Integer id) {
        ChallengeSettings challengeSettings = challengeSettingsRepository.findById(id).orElse(null);
        return Objects.nonNull(challengeSettings) ? super.modelMapper.map(challengeSettings, ChallengeSettingsResponse.class) : null;
    }

    private ChallengeSettingsLangResponse handleResponseBasedOnLanguageValues(ChallengeSettings challengeSetting, String localName, String localType) {
        ChallengeSettingsLangResponse challengeSettingsLangResponse = modelMapper.map(challengeSetting, ChallengeSettingsLangResponse.class);
        challengeSettingsLangResponse.setName(localName);
        challengeSettingsLangResponse.setType(localType);
        return challengeSettingsLangResponse;
    }
}