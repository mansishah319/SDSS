/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.infrastructure.success.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.infrastructure.success.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Success Response")
public class SuccessResponse<T> extends AbstractBaseResponse {

    @Schema(description = "The success data")
    private T data;

    @Override
    public Boolean isSuccess() {
        return Boolean.TRUE;
    }
}
