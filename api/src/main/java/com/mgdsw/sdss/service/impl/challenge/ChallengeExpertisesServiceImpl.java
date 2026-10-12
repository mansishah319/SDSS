/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.user.UserInvitationExpertisesRequest;
import com.mgdsw.sdss.entity.challenge.ChallengeExpertises;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.ExpertLevel;
import com.mgdsw.sdss.repository.challenge.ChallengeExpertisesRepository;
import com.mgdsw.sdss.service.ChallengeExpertisesService;
import com.mgdsw.sdss.service.ExpertisesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeExpertisesServiceImpl extends AbstractBaseServiceImpl<Integer, ChallengeExpertises> implements ChallengeExpertisesService {

    private final ChallengeExpertisesRepository challengeExpertisesRepository;
    private final ExpertisesService expertisesService;

    @Override
    protected ChallengeExpertisesRepository repository() {
        return challengeExpertisesRepository;
    }

    @Override
    public void create(Challenges challenges, UserInvitationExpertisesRequest userInvitationExpertisesRequest) {
        List<ChallengeExpertises> expertisesList = userInvitationExpertisesRequest.getUsersExpertisesRequestList().stream().map(expertise ->
                ChallengeExpertises.builder()
                        .expertises(expertisesService.findById(expertise.getAreaOfExpertisesId()))
                        .challenges(challenges)
                        .expertLevel(ExpertLevel.valueOf(expertise.getExpertLevel().toUpperCase()))
                        .build()).collect(Collectors.toList());
        challengeExpertisesRepository.saveAll(expertisesList);
    }
}
