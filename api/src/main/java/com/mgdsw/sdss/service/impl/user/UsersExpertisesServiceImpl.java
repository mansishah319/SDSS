/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.user;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.user.RetrieveUsersBasedOnExpertisesRequest;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesRequest;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesResponse;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UsersExpertises;
import com.mgdsw.sdss.enums.ExpertLevel;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Operations;
import com.mgdsw.sdss.repository.user.UsersExpertisesRepository;
import com.mgdsw.sdss.service.ExpertisesService;
import com.mgdsw.sdss.service.UsersExpertisesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersExpertisesServiceImpl extends AbstractBaseServiceImpl<Integer, UsersExpertises> implements UsersExpertisesService {

    private final ExpertisesService expertisesService;
    private final UsersExpertisesRepository usersExpertisesRepository;

    @Override
    protected UsersExpertisesRepository repository() {
        return usersExpertisesRepository;
    }

    @Override
    @Transactional
    public void createListOfExpertisesByUser(List<UsersExpertisesRequest> usersExpertisesRequestList, User user, Operations operations) {
        List<UsersExpertises> existingUserExpertises = this.findAllByUser(user);
        if (operations.equals(Operations.UPDATE) && Objects.nonNull(existingUserExpertises) && !existingUserExpertises.isEmpty()) {
            usersExpertisesRepository.deleteByUser(user);
        }
        usersExpertisesRepository.saveAll(prepareListToSave(usersExpertisesRequestList, user));
    }

    @Override
    public List<UsersExpertisesResponse> retrieveUsersExpertisesBasedOnUser(User user, Language userLanguage) {
        return usersExpertisesRepository.findAllByUser(user).stream().map(usersExpertise ->
                UsersExpertisesResponse.builder()
                        .expertisesId(usersExpertise.getExpertises().getId())
                        .userId(usersExpertise.getUser().getId())
                        .expertLevel(userLanguage.equals(Language.EN) ?
                                usersExpertise.getExpertLevel().getEnglishName() :
                                usersExpertise.getExpertLevel().getArabicName()
                        )
                        .expertisesName(
                                userLanguage.equals(Language.EN) ?
                                        usersExpertise.getExpertises().getNameEn() :
                                        usersExpertise.getExpertises().getNameAr()
                        )
                        .isPrimary(usersExpertise.getIsPrimary())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<User> retrieveUsersBasedOnAreaOfExpertiseAndLevel(List<RetrieveUsersBasedOnExpertisesRequest> usersExpertisesRequestList) {
        List<String> expertLevelList = usersExpertisesRequestList.stream().map(RetrieveUsersBasedOnExpertisesRequest::getExpertLevel).collect(Collectors.toList());
        return usersExpertisesRepository.findUserByExpertisesInAndExpertLevelIn(
                usersExpertisesRequestList.stream().map(RetrieveUsersBasedOnExpertisesRequest::getAreaOfExpertisesId).collect(Collectors.toList()),
                expertLevelList.stream().map(expertLevel -> ExpertLevel.valueOf(expertLevel.toUpperCase())).collect(Collectors.toList())
        );
    }

    @Override
    public UsersExpertises findMainUsersExpertises(User user) {
        return usersExpertisesRepository.findByUserAndIsPrimaryTrue(user);
    }

    private List<UsersExpertises> findAllByUser(User user) {
        return usersExpertisesRepository.findAllByUser(user);
    }

    private List<UsersExpertises> prepareListToSave(List<UsersExpertisesRequest> usersExpertisesRequestList, User user) {
        return usersExpertisesRequestList.stream().map(request ->
                UsersExpertises.builder()
                        .expertises(expertisesService.findById(request.getAreaOfExpertisesId()))
                        .expertLevel(ExpertLevel.fromString(request.getExpertLevel()))
                        .isPrimary(request.getIsPrimary())
                        .user(user)
                        .build()
        ).collect(Collectors.toList());
    }
}
