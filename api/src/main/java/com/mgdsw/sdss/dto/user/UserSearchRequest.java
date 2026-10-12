/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class UserSearchRequest {

    @NotCommandOrScript
    private String searchAny;
    @NotCommandOrScript
    private String grpNumber;
    @NotCommandOrScript
    private String language;
    private Integer status;
    @NotCommandOrScript
    private String mobile;
    @NotCommandOrScript
    private String email;
    @NotCommandOrScript
    private String type;
    @NotCommandOrScript
    private String role;
    @NotCommandOrScript
    private String department;
    @NotCommandOrScript
    private String sector;
    @NotCommandOrScript
    private String expertLevel;
    @NotCommandOrScript
    private String areaOfExpertises;
}
