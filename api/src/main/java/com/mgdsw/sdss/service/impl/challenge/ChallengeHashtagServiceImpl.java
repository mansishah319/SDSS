/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.challenge.ChallengeHashtag;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.challenge.Hashtag;
import com.mgdsw.sdss.enums.Operations;
import com.mgdsw.sdss.repository.challenge.ChallengeHashtagRepository;
import com.mgdsw.sdss.service.ChallengeHashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ChallengeHashtagServiceImpl extends AbstractBaseServiceImpl<Integer, ChallengeHashtag> implements ChallengeHashtagService {

    private final ChallengeHashtagRepository challengeHashtagRepository;

    @Override
    protected ChallengeHashtagRepository repository() {
        return challengeHashtagRepository;
    }

    @Override
    public void create(List<Hashtag> hashtagList, Challenges challenges, Operations operations) {
        if (operations.equals(Operations.UPDATE))
            challengeHashtagRepository.deleteByChallenges(challenges);
        List<ChallengeHashtag> challengesHashtags = hashtagList.stream().map(hashtag ->
                ChallengeHashtag.builder().challenges(challenges).hashtag(hashtag).build()).collect(Collectors.toList());
        challengeHashtagRepository.saveAll(challengesHashtags);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> retrieveHashtagTextsByChallenges(Challenges challenges) {
        return challengeHashtagRepository.findByChallenges(challenges).stream().map(ChallengeHashtag::getHashtag)
                .map(Hashtag::getHashtagValue).collect(Collectors.toList());
    }
}