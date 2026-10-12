/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys;

import com.mgdsw.sdss.dto.surveys.lookups.LookupInfoRequest;
import com.mgdsw.sdss.dto.surveys.round.UpdateSdssSurveyForRoundRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SdssSurveyEditRequest extends LookupInfoRequest {

    // retrieve employees by this info
    private List<String> jobList;
    private Boolean manualHandle;
    private List<String> departmentList;

    private Integer numberOfRounds;

    private List<UpdateSdssSurveyForRoundRequest> roundRequestList;
}
