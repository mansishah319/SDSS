/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.infrastructure.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.infrastructure.success.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response")
public final class ErrorResponse extends AbstractBaseResponse {

    @Schema(description = "Error code", example = "INVALID_REQUEST")
    private final String errorCode;

    @Override
    public Boolean isSuccess() {
        return Boolean.FALSE;
    }
}
