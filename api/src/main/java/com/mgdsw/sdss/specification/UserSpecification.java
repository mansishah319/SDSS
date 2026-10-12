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
import com.mgdsw.sdss.dto.user.UserSearchRequest;
import com.mgdsw.sdss.entity.user.Roles;
import com.mgdsw.sdss.entity.user.Roles_;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.User_;
import com.mgdsw.sdss.entity.user.UsersExpertises;
import com.mgdsw.sdss.entity.user.UsersExpertises_;
import com.mgdsw.sdss.entity.variety.Expertises;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Status;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

public class UserSpecification extends BaseSearchSpecification<UserSearchRequest, User> {

    public UserSpecification(UserSearchRequest search) {
        super(search);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<User> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder,
                                 UserSearchRequest search) {

        query.distinct(true);
        List<Predicate> predicateList = new ArrayList<>();

        this.searchWithAnyValue(search, predicateList, root, criteriaBuilder);
        this.searchWithUserPersonalInfo(search, predicateList, root, criteriaBuilder);
        this.searchWithUserCustomInfo(search, predicateList, root, criteriaBuilder);
        this.searchWithDepartment(search, predicateList, root, criteriaBuilder);
        this.searchAndRetrieveUserByRoles(search, predicateList, root, criteriaBuilder);
        this.searchAndRetrieveUserByExpertises(search, predicateList, root, criteriaBuilder);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
    }

    private void searchWithAnyValue(UserSearchRequest search, List<Predicate> predicateList,
                                    Root<User> root, CriteriaBuilder criteriaBuilder) {
        if (StringUtils.isBlank(search.getSearchAny())) {
            return;
        }

        String searchAny = search.getSearchAny();
        List<Predicate> anyPredicates = new ArrayList<>();

        anyPredicates.add(this.likeIgnoreCase(criteriaBuilder, root.get(User_.EMAIL).as(String.class), searchAny));
        anyPredicates.add(criteriaBuilder.like(root.get(User_.MOBILE).as(String.class), this.wrapLike(searchAny)));
        anyPredicates.add(criteriaBuilder.like(root.get(User_.GRP_NUMBER).as(String.class), this.wrapLike(searchAny)));
        anyPredicates.add(this.likeIgnoreCase(criteriaBuilder, root.get(User_.TYPE).as(String.class), searchAny));

        this.addPredicate(anyPredicates, this.buildLanguagePredicate(criteriaBuilder, root, searchAny));
        this.addPredicate(anyPredicates, this.buildStatusPredicate(criteriaBuilder, root, searchAny));
        this.addPredicate(anyPredicates, this.buildDepartmentPredicate(criteriaBuilder, root, searchAny));
        this.addPredicate(anyPredicates, this.buildRolePredicate(criteriaBuilder, root, searchAny));
        this.addPredicate(anyPredicates, this.buildExpertLevelPredicate(criteriaBuilder, root, searchAny));
        this.addPredicate(anyPredicates, this.buildAreaOfExpertisesPredicate(criteriaBuilder, root, searchAny));

        if (!anyPredicates.isEmpty()) {
            predicateList.add(criteriaBuilder.or(anyPredicates.toArray(new Predicate[]{})));
        }
    }

    private void searchWithUserPersonalInfo(UserSearchRequest search, List<Predicate> predicateList,
                                            Root<User> root, CriteriaBuilder criteriaBuilder) {
        if (StringUtils.isNotBlank(search.getEmail())) {
            predicateList.add(this.likeIgnoreCase(criteriaBuilder, root.get(User_.EMAIL).as(String.class), search.getEmail()));
        }

        if (StringUtils.isNotBlank(search.getMobile())) {
            predicateList.add(criteriaBuilder.like(root.get(User_.MOBILE).as(String.class), this.wrapLike(search.getMobile())));
        }

        if (StringUtils.isNotBlank(search.getGrpNumber())) {
            predicateList.add(criteriaBuilder.like(root.get(User_.GRP_NUMBER).as(String.class), this.wrapLike(search.getGrpNumber())));
        }
    }

    private void searchWithUserCustomInfo(UserSearchRequest search, List<Predicate> predicateList,
                                          Root<User> root, CriteriaBuilder criteriaBuilder) {
        if (StringUtils.isNotBlank(search.getLanguage())) {
            Language language = Language.fromString(search.getLanguage());
            if (language != null) {
                predicateList.add(criteriaBuilder.equal(root.get(User_.LANGUAGE), language));
            }
        }

        if (search.getStatus() != null && search.getStatus() >= 0 && search.getStatus() < Status.values().length) {
            predicateList.add(criteriaBuilder.equal(root.get("status"), Status.values()[search.getStatus()]));
        }

        if (StringUtils.isNotBlank(search.getType())) {
            predicateList.add(this.likeIgnoreCase(criteriaBuilder, root.get(User_.TYPE).as(String.class), search.getType()));
        }
    }

    private void searchWithDepartment(UserSearchRequest search, List<Predicate> predicateList,
                                      Root<User> root, CriteriaBuilder criteriaBuilder) {
        this.addPredicate(predicateList, this.buildDepartmentPredicate(criteriaBuilder, root, search.getDepartment()));
    }

    private void searchAndRetrieveUserByRoles(UserSearchRequest search, List<Predicate> predicateList,
                                              Root<User> root, CriteriaBuilder criteriaBuilder) {
        this.addPredicate(predicateList, this.buildRolePredicate(criteriaBuilder, root, search.getRole()));
    }

    private void searchAndRetrieveUserByExpertises(UserSearchRequest search, List<Predicate> predicateList,
                                                   Root<User> root, CriteriaBuilder criteriaBuilder) {
        if (StringUtils.isBlank(search.getExpertLevel()) && StringUtils.isBlank(search.getAreaOfExpertises())) {
            return;
        }

        Join<User, UsersExpertises> usersExpertisesJoin = root.join(User_.EXPERTISES_LIST, JoinType.LEFT);

        if (StringUtils.isNotBlank(search.getExpertLevel())) {
            predicateList.add(this.likeIgnoreCase(criteriaBuilder, usersExpertisesJoin.get(UsersExpertises_.EXPERT_LEVEL).as(String.class), search.getExpertLevel()));
        }

        if (StringUtils.isNotBlank(search.getAreaOfExpertises())) {
            Join<UsersExpertises, Expertises> expertisesJoin = usersExpertisesJoin.join("expertises", JoinType.LEFT);
            List<Predicate> expertisePredicates = new ArrayList<>();
            expertisePredicates.add(this.likeIgnoreCase(criteriaBuilder, expertisesJoin.get("nameAr").as(String.class), search.getAreaOfExpertises()));
            expertisePredicates.add(this.likeIgnoreCase(criteriaBuilder, expertisesJoin.get("nameEn").as(String.class), search.getAreaOfExpertises()));

            Integer expertiseId = this.parseInteger(search.getAreaOfExpertises());
            if (expertiseId != null) {
                expertisePredicates.add(criteriaBuilder.equal(expertisesJoin.get(BaseEntity_.ID), expertiseId));
            }

            predicateList.add(criteriaBuilder.or(expertisePredicates.toArray(new Predicate[]{})));
        }
    }

    private Predicate buildLanguagePredicate(CriteriaBuilder criteriaBuilder, Root<User> root, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        List<Predicate> languagePredicates = new ArrayList<>();
        for (Language language : Language.values()) {
            if (this.matchesSearchValue(language.name(), value)
                    || this.matchesSearchValue(language.getName(), value)
                    || this.matchesSearchValue(language.getCode(), value)) {
                languagePredicates.add(criteriaBuilder.equal(root.get(User_.LANGUAGE), language));
            }
        }

        return languagePredicates.isEmpty() ? null : criteriaBuilder.or(languagePredicates.toArray(new Predicate[]{}));
    }

    private Predicate buildStatusPredicate(CriteriaBuilder criteriaBuilder, Root<User> root, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        List<Predicate> statusPredicates = new ArrayList<>();
        String trimmedValue = StringUtils.trim(value);

        for (int index = 0; index < Status.values().length; index++) {
            Status status = Status.values()[index];
            if (StringUtils.equals(trimmedValue, String.valueOf(index))
                    || this.matchesSearchValue(status.name(), trimmedValue)
                    || this.matchesSearchValue(status.getNameEn(), trimmedValue)
                    || this.matchesSearchValue(status.getNameAr(), trimmedValue)) {
                statusPredicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
        }

        return statusPredicates.isEmpty() ? null : criteriaBuilder.or(statusPredicates.toArray(new Predicate[]{}));
    }

    private Predicate buildDepartmentPredicate(CriteriaBuilder criteriaBuilder, Root<User> root, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        List<Predicate> departmentPredicates = new ArrayList<>();
        departmentPredicates.add(this.likeIgnoreCase(criteriaBuilder, root.get(User_.DEPARTMENT_NAME).as(String.class), value));

        Long departmentId = this.parseLong(value);
        if (departmentId != null) {
            departmentPredicates.add(criteriaBuilder.equal(root.get(User_.DEPARTMENT_ID), departmentId));
        }

        return criteriaBuilder.or(departmentPredicates.toArray(new Predicate[]{}));
    }

    private Predicate buildRolePredicate(CriteriaBuilder criteriaBuilder, Root<User> root, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Join<User, Roles> roleJoin = root.join(User_.ROLES, JoinType.LEFT);
        List<Predicate> rolePredicates = new ArrayList<>();
        rolePredicates.add(this.likeIgnoreCase(criteriaBuilder, roleJoin.get(Roles_.NAME_EN).as(String.class), value));
        rolePredicates.add(this.likeIgnoreCase(criteriaBuilder, roleJoin.get(Roles_.NAME_AR).as(String.class), value));

        Integer roleId = this.parseInteger(value);
        if (roleId != null) {
            rolePredicates.add(criteriaBuilder.equal(roleJoin.get(BaseEntity_.ID), roleId));
        }

        return criteriaBuilder.or(rolePredicates.toArray(new Predicate[]{}));
    }

    private Predicate buildExpertLevelPredicate(CriteriaBuilder criteriaBuilder, Root<User> root, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Join<User, UsersExpertises> usersExpertisesJoin = root.join(User_.EXPERTISES_LIST, JoinType.LEFT);
        return this.likeIgnoreCase(criteriaBuilder, usersExpertisesJoin.get(UsersExpertises_.EXPERT_LEVEL).as(String.class), value);
    }

    private Predicate buildAreaOfExpertisesPredicate(CriteriaBuilder criteriaBuilder, Root<User> root, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Join<User, UsersExpertises> usersExpertisesJoin = root.join(User_.EXPERTISES_LIST, JoinType.LEFT);
        Join<UsersExpertises, Expertises> expertisesJoin = usersExpertisesJoin.join("expertises", JoinType.LEFT);
        List<Predicate> expertisePredicates = new ArrayList<>();
        expertisePredicates.add(this.likeIgnoreCase(criteriaBuilder, expertisesJoin.get("nameAr").as(String.class), value));
        expertisePredicates.add(this.likeIgnoreCase(criteriaBuilder, expertisesJoin.get("nameEn").as(String.class), value));

        Integer expertiseId = this.parseInteger(value);
        if (expertiseId != null) {
            expertisePredicates.add(criteriaBuilder.equal(expertisesJoin.get(BaseEntity_.ID), expertiseId));
        }

        return criteriaBuilder.or(expertisePredicates.toArray(new Predicate[]{}));
    }

    private void addPredicate(List<Predicate> predicateList, Predicate predicate) {
        if (predicate != null) {
            predicateList.add(predicate);
        }
    }

    private boolean matchesSearchValue(String source, String searchValue) {
        return StringUtils.isNotBlank(source) && StringUtils.containsIgnoreCase(source, StringUtils.trim(searchValue));
    }

    private Predicate likeIgnoreCase(CriteriaBuilder criteriaBuilder, Expression<String> expression, String value) {
        return criteriaBuilder.like(criteriaBuilder.lower(expression), this.wrapLike(StringUtils.lowerCase(value)));
    }

    private String wrapLike(String value) {
        return "%" + StringUtils.trim(value) + "%";
    }

    private Integer parseInteger(String value) {
        return StringUtils.isNumeric(StringUtils.trim(value)) ? Integer.parseInt(StringUtils.trim(value)) : null;
    }

    private Long parseLong(String value) {
        return StringUtils.isNumeric(StringUtils.trim(value)) ? Long.parseLong(StringUtils.trim(value)) : null;
    }
}
