/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRolesResponse {

    private List<String> roles;
    private List<String> permissions;
    private String fullName;
    private String language;
    private String gradeName;
    private String iss;
    private String phoneNumber;
    private byte[] userPhoto;

    private Boolean internalDocuments;
    private Boolean chatContents;
    private Boolean socialMediaFeeds;
    private Boolean decision;
    private Boolean questionView;
    private Boolean ratings;
    private Boolean scrollingText;
}
