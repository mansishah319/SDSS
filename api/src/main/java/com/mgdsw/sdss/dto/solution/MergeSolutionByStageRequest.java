/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.solution;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Merge Solutions")
public class MergeSolutionByStageRequest {

    @NotNull(message = FieldNamesUtil.SOLUTION_TEXT + SPACE + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.SOLUTION_TEXT + SPACE + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    @Schema(description = "New Solution Text", example = "عقد جلسة حوارية تستهدف الموظفين من مختلف المناصب والمسميات لمناقشة وربط المهام والمؤشرات")
    private String newSolutionText;
    private List<Integer> mergedSolutionIds;
    private Integer queLevel;
    private Integer challengeId;
}
