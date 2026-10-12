/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.lookup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.core.dto.DepartmentCoreResponse;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsLangResponse;
import com.mgdsw.sdss.dto.common.DropdownResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeLookupCreationScreenResponse {

    private List<UserDropDownListResponse> moderators;
    private List<DropdownResponse> expertisesResponseList;

    private List<DropdownResponse> strategicGoalsResponseList;
    private List<DropdownResponse> strategicDirectionResponseList;

    private List<DropdownResponse> futureStrategicGoalsResponseList;
    private List<DropdownResponse> futureStrategicDirectionResponseList;

    private List<ChallengeSettingsLangResponse> topicClassificationList;
    private List<ChallengeSettingsLangResponse> levelOfDecisionList;
    private List<ChallengeSettingsLangResponse> importanceOfDecisionList;
    private List<ChallengeSettingsLangResponse> impactList;
    private List<ChallengeSettingsLangResponse> fieldList;

    private List<DepartmentCoreResponse> departmentResponseList;
}
