/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.challenge;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.challenge.Hashtag;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Repository
public interface HashtagRepository extends BaseRepository<Hashtag, Integer> {

    Optional<Hashtag> findByHashtagValue(String hashtagValue);
}
