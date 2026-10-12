/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.roles.RolesResponse;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseResponse<Integer> {

    private String fullName;
    private String grpNumber;
    private String language;
    private String nationality;
    private String middleName;
    private String status;
    private String mobile;
    private String alternativeMobile;
    private String email;
    private String alternativeEmail;
    private String type;
    private String title;
    private List<RolesResponse> roles;
    private String mainExpertLevel;
    private String areaOfExpertise;
    private String expertLevel;
    private Integer expertisesId;

    private Boolean isMailSend;

    private Boolean internalDocuments;
    private Boolean chatContents;
    private Boolean socialMediaFeeds;
    private Boolean decision;
    private Boolean questionView;
    private Boolean scrollingText;

    private String gradeName;

    private String sectorName;
    private String sectorShortName;

    private String departmentName;
    private String parentDepartmentName;
    private String departmentType;

    private String confirmationUrl;
    private String committeeName;
    private String committeeId;
    private String shortName;

    private Boolean isDelphiModerator;

    private List<UsersExpertisesResponse> usersExpertisesResponses;

    @JsonProperty("shortDepartmentName")
    public String getShortDepartmentName() {
        return shortName;
    }
}
