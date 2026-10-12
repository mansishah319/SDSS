/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.hashtag.HashtagCreationRequest;
import com.mgdsw.sdss.dto.hashtag.HashtagCreationResponse;
import com.mgdsw.sdss.entity.challenge.Hashtag;
import com.mgdsw.sdss.repository.challenge.HashtagRepository;
import com.mgdsw.sdss.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl extends AbstractBaseServiceImpl<Integer, Hashtag> implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    protected HashtagRepository repository() {
        return hashtagRepository;
    }

    @Override
    public HashtagCreationResponse create(HashtagCreationRequest request) {
        Hashtag hashtag = createEntityOrReturnExisting(request);
        return super.modelMapper.map(hashtag, HashtagCreationResponse.class);
    }

    @Override
    @Transactional
    public List<Hashtag> saveList(List<String> hashtagTexts) {
        return hashtagTexts.stream().map(
                text -> HashtagCreationRequest.builder()
                        .hashtagValue(text)
                        .build()
        ).collect(Collectors.toList()).stream().map(this::createEntityOrReturnExisting).collect(Collectors.toList());
    }

    private Hashtag createEntityOrReturnExisting(HashtagCreationRequest request) {
        return hashtagRepository.findByHashtagValue(request.getHashtagValue())
                .orElseGet(() -> super.create(request));
    }
}
