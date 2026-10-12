/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.variety.Tweets;
import com.mgdsw.sdss.repository.variety.TweetsRepository;
import com.mgdsw.sdss.service.TweetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class TweetsServiceImpl extends AbstractBaseServiceImpl<Integer, Tweets> implements TweetsService {

    private final TweetsRepository tweetsRepository;

    @Override
    protected TweetsRepository repository() {
        return tweetsRepository;
    }
}