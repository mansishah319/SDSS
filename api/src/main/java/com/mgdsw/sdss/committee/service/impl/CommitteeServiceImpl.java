/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.committee.service.impl;

import com.mgdsw.sdss.committee.dto.CommitteeResponse;
import com.mgdsw.sdss.committee.repository.CommitteeRepository;
import com.mgdsw.sdss.committee.service.CommitteeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Mar, 2024
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CommitteeServiceImpl implements CommitteeService {

    private final CommitteeRepository committeeRepository;

    @Override
    public List<CommitteeResponse> retrieveAllCommittees() {
        return committeeRepository.findAll().stream().map(
                committee ->
                        CommitteeResponse.builder()
                                .teamID(committee.getId())
                                .teamTitle(committee.getName())
                                .build()).collect(Collectors.toList());
    }
}
