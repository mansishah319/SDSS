/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.committee.service;

import com.mgdsw.sdss.committee.dto.CommitteeResponse;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Mar, 2024
 */
public interface CommitteeService {

    List<CommitteeResponse> retrieveAllCommittees();
}
