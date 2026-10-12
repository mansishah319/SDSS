/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.security.dto;

import com.mgdsw.sdss.entity.user.Roles;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.ROLE_PREFIX;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private String fullName;
    private String phoneNumber;
    private Long employeeNumber;
    private String sectorName;
    private Long grpNumber;
    private Integer sectorId;
    private String username;
    private String email;
    private String guid;
    private Language language;
    private String languageCode;

    private Boolean internalDocuments;
    private Boolean chatContents;
    private Boolean socialMediaFeeds;
    private Boolean decision;
    private Boolean questionView;
    private Boolean ratings;
    private Boolean scrollingText;

    private String token;
    private long iat;
    private long exp;

    private int departmentId;
    private String departmentName;
    private String gender;
    private int empType;
    private String sub;
    private String employeeName;

    private String gradeName;
    private String iss;

    private Collection<? extends GrantedAuthority> authorities;
    private Collection<? extends GrantedAuthority> permissions;

    public static UserPrincipal createUserPrincipal(User user) {
        List<Roles> roleList = user.getRoles();

        List<GrantedAuthority> roles = roleList.stream().filter(Objects::nonNull)
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.getNameEn()))
                .collect(Collectors.toList());

        List<GrantedAuthority> permissions = roleList.stream().filter(Objects::nonNull)
                .map(Roles::getPermissions).filter(permission -> Objects.nonNull(permission) && !permission.isEmpty()).flatMap(Collection::stream)
                .map(permission -> new SimpleGrantedAuthority(permission.getNameEn()))
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .authorities(roles)
                .email(user.getEmail())
                .permissions(permissions)
                .ratings(user.getRatings())
                .language(user.getLanguage())
                .decision(user.getDecision())
                .grpNumber(user.getGrpNumber())
                .chatContents(user.getChatContents())
                .questionView(user.getQuestionView())
                .scrollingText(user.getScrollingText())
                .socialMediaFeeds(user.getSocialMediaFeeds())
                .internalDocuments(user.getInternalDocuments())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}