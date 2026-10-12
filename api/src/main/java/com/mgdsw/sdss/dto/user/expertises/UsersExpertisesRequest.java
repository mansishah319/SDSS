/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user.expertises;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.utils.FieldNamesUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersExpertisesRequest implements BaseMarkerDTO {

    @Min(1)
    private Integer areaOfExpertisesId;
    @NotNull(message = FieldNamesUtil.EXPERT_LEVEL + DpConstants.Messages.FailedMessages.NOT_NULL_ERROR_MESSAGE)
    @NotBlank(message = FieldNamesUtil.EXPERT_LEVEL + DpConstants.Messages.FailedMessages.NOT_BLANK_ERROR_MESSAGE)
    private String expertLevel;
    private Boolean isPrimary;
}
