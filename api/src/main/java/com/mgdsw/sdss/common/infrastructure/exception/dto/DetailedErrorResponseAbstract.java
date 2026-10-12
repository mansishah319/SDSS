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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

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
public class DetailedErrorResponseAbstract<H extends Serializable, D> extends AbstractBaseResponse {

    private H errorHeader;
    private D errorDetails;

    @Override
    public Boolean isSuccess() {
        return Boolean.FALSE;
    }

    public DetailedErrorResponseAbstract(String message, String responseCode, H errorHeader, D errorDetails) {
        super(Boolean.FALSE, message, responseCode);
        this.errorHeader = errorHeader;
        this.errorDetails = errorDetails;
    }
}
