/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.user;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.user.GuestAccess;
import com.mgdsw.sdss.repository.user.GuestAccessRepository;
import com.mgdsw.sdss.service.GuestAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class GuestAccessServiceImpl extends AbstractBaseServiceImpl<Integer, GuestAccess> implements GuestAccessService {

    private final GuestAccessRepository guestAccessRepository;

    @Override
    protected GuestAccessRepository repository() {
        return guestAccessRepository;
    }
}