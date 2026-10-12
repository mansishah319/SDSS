/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.voting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VotingResponse extends BaseResponse<Integer> {

    private int queLevel;
    private String userSorting;
    private SolutionResponse solution;
}
