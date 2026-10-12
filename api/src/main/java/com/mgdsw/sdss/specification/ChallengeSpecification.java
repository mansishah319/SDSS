/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.specification;

import com.mgdsw.sdss.common.base.BaseEntity_;
import com.mgdsw.sdss.common.base.BaseSearchSpecification;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.dto.challenge.ChallengeSearchRequest;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.challenge.Challenges_;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UserInvitation;
import com.mgdsw.sdss.entity.user.UserInvitation_;
import com.mgdsw.sdss.entity.user.User_;
import com.mgdsw.sdss.entity.user.Validators;
import com.mgdsw.sdss.enums.ChallengeStatus;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

public class ChallengeSpecification extends BaseSearchSpecification<ChallengeSearchRequest, Challenges> {

    public ChallengeSpecification(ChallengeSearchRequest search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Challenges> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder,
                                 ChallengeSearchRequest search) {

        List<Predicate> predicateList = new ArrayList<>();

        this.searchWithInvitedUser(search, predicateList, root, criteriaBuilder);
        this.searchWithChallengeInfo(search, predicateList, root, criteriaBuilder);
        this.searchWithChallengeDateInfo(search, predicateList, root, criteriaBuilder, query);
        this.searchWithDepartmentOrSector(search, predicateList, root, criteriaBuilder);

        query.distinct(true);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
    }

    private void searchWithChallengeInfo(ChallengeSearchRequest search, List<Predicate> predicateList,
                                         Root<Challenges> root, CriteriaBuilder criteriaBuilder) {
        ChallengeStatus challengeStatus = ChallengeStatus.fromString(search.getStatus());

        if (Objects.nonNull(search.getTitle()) && StringUtils.isNotEmpty(search.getTitle()))
            predicateList.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get(Challenges_.TITLE_EN).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getTitle() + DpConstants.Characters.PERCENTAGE),
                            criteriaBuilder.like(root.get(Challenges_.TITLE_AR).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getTitle() + DpConstants.Characters.PERCENTAGE)
                    )
            );

        if (Objects.nonNull(search.getDescription()) && StringUtils.isNotEmpty(search.getDescription()))
            predicateList.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get(Challenges_.DESCRIPTION_EN).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getDescription() + DpConstants.Characters.PERCENTAGE),
                            criteriaBuilder.like(root.get(Challenges_.DESCRIPTION_AR).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getDescription() + DpConstants.Characters.PERCENTAGE)
                    )
            );

        if (Objects.nonNull(search.getModerator()) && StringUtils.isNotEmpty(search.getModerator()))
            predicateList.add(criteriaBuilder.like(root.get(Challenges_.MODERATOR).get(User_.GRP_NUMBER).as(String.class),
                    DpConstants.Characters.PERCENTAGE + Long.parseLong(search.getModerator()) + DpConstants.Characters.PERCENTAGE));

        if (Objects.nonNull(search.getVisibility()) && StringUtils.isNotEmpty(search.getVisibility()))
            predicateList.add(criteriaBuilder.like(root.get(Challenges_.VISIBILITY).as(String.class),
                    DpConstants.Characters.PERCENTAGE + search.getVisibility() + DpConstants.Characters.PERCENTAGE));

        if (Objects.nonNull(challengeStatus))
            predicateList.add(criteriaBuilder.equal(root.get(Challenges_.status).as(String.class), challengeStatus.ordinal()));
    }

    private void searchWithChallengeDateInfo(ChallengeSearchRequest search, List<Predicate> predicateList,
                                             Root<Challenges> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query) {
        if (Objects.nonNull(search.getCreatedAt()) && StringUtils.isNotEmpty(search.getCreatedAt()) && StringUtils.isNotBlank(search.getCreatedAt()))
            predicateList.add(this.getLocalDateTimeBetweenOneDate(search.getCreatedAt(), BaseEntity_.CREATED_AT, root, query, criteriaBuilder));

        if (Objects.nonNull(search.getLastModifiedAt()) && StringUtils.isNotEmpty(search.getLastModifiedAt()) && StringUtils.isNotBlank(search.getLastModifiedAt()))
            predicateList.add(this.getLocalDateTimeBetweenOneDate(search.getLastModifiedAt(), BaseEntity_.UPDATED_AT, root, query, criteriaBuilder));

        if (Objects.nonNull(search.getStartVotingDate()) && StringUtils.isNotEmpty(search.getStartVotingDate()) && StringUtils.isNotBlank(search.getStartVotingDate()))
            predicateList.add(this.getLocalDateTimeBetweenOneDate(search.getStartVotingDate(), Challenges_.START_TIME, root, query, criteriaBuilder));

        if (Objects.nonNull(search.getEndVotingDate()) && StringUtils.isNotEmpty(search.getEndVotingDate()) && StringUtils.isNotBlank(search.getEndVotingDate()))
            predicateList.add(this.getLocalDateTimeBetweenOneDate(search.getEndVotingDate(), Challenges_.END_TIME, root, query, criteriaBuilder));
    }

    private void searchWithDepartmentOrSector(ChallengeSearchRequest search, List<Predicate> predicateList,
                                              Root<Challenges> root, CriteriaBuilder criteriaBuilder) {
        if (Objects.nonNull(search.getDepartment()) && StringUtils.isNotEmpty(search.getDepartment()))
            predicateList.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get(Challenges_.DEPARTMENT_NAME).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getDepartment() + DpConstants.Characters.PERCENTAGE),
                            criteriaBuilder.like(root.get(Challenges_.DEPARTMENT_ID).as(String.class),
                                    DpConstants.Characters.PERCENTAGE + search.getDepartment() + DpConstants.Characters.PERCENTAGE)
                    )
            );
    }

    private void searchWithInvitedUser(ChallengeSearchRequest search, List<Predicate> predicateList,
                                       Root<Challenges> root, CriteriaBuilder criteriaBuilder) {
        if (Objects.nonNull(search.getInvitedUserId()) && search.getInvitedUserId() > 0) {
            Join<Challenges, UserInvitation> userInvitationJoin = root.join(Challenges_.userInvitations, JoinType.LEFT);
            Join<UserInvitation, User> invitedUserJoin = userInvitationJoin.join(UserInvitation_.USER, JoinType.LEFT);
            Join<Challenges, Validators> validatorsJoin = root.join(Challenges_.validators, JoinType.LEFT);
            Join<Validators, User> validatorUserJoin = validatorsJoin.join("user", JoinType.LEFT);

            Predicate invitedUserPredicate = criteriaBuilder.or(
                    criteriaBuilder.equal(invitedUserJoin.get(User_.GRP_NUMBER), search.getInvitedUserId()),
                    criteriaBuilder.equal(validatorUserJoin.get(User_.GRP_NUMBER), search.getInvitedUserId()),
                    criteriaBuilder.equal(root.get(Challenges_.MODERATOR).get(User_.GRP_NUMBER), search.getInvitedUserId())
            );
            predicateList.add(invitedUserPredicate);
        }
    }

    private Predicate getLocalDateTimeBetweenOneDate(String date, String fieldName, Root<Challenges> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return super.getTimeBetween(
                DpDateUtil.parseFormattedLocalDateTime(
                        DpDateUtil.startTimeFromLocalDate(date)
                ),
                DpDateUtil.parseFormattedLocalDateTime(
                        DpDateUtil.endTimeFromLocalDate(date)
                ),
                fieldName
        ).toPredicate(root, query, criteriaBuilder);
    }
}
