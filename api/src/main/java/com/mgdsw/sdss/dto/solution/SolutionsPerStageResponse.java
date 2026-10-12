/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.solution;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.dto.challenge.VotingResultPerStageResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jul, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolutionsPerStageResponse implements BaseMarkerDTO {

    private Integer stageNumber;
    private List<SolutionFullResponse> solutionResponseList;
    private VotingResultPerStageResponse votingResultPerStageResponse;
}
