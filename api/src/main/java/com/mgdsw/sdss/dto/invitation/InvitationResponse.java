/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.invitation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.challenge.ChallengesResponse;
import com.mgdsw.sdss.dto.department.DepartmentResponse;
import com.mgdsw.sdss.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
@Schema(description = "The User Invitation response object for Invitation responses.")
public class InvitationResponse extends BaseResponse<Integer> {

    private UserResponse user;
    private ChallengesResponse question;
    private DepartmentResponse department;
    private String inviteLink;
    private String hashkey;
    private String status;
    private boolean isGuest;
    private String locale;
    private String reason;
}
