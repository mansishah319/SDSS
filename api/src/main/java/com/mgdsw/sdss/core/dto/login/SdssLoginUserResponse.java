/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.core.dto.login;

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
 * @email eng.mo.abdu@gmail.com
 * @creationDate 09 Jan 2025
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SdssLoginUserResponse {

    private String token;
    private String fullName;
    private String language;

    private String guid;
    private Long grpNumber;
    private String email;
    private String languageCode;
    private long iat;
    private long exp;

    private int departmentId;
    private String departmentName;
    private int sectorId;
    private String sectorName;
    private String gender;
    private int empType;
    private String sub;
    private String userName;
    private String employeeName;

    private Boolean internalDocuments;
    private Boolean socialMediaFeeds;
    private Boolean scrollingText;
    private Boolean chatContents;
    private Boolean questionView;
    private Boolean decision;
    private Boolean ratings;
    private List<String> roles;

    private String gradeName;
    private String iss;
    private String phoneNumber;
    private byte[] userPhoto;
}
