/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.mgdsw.sdss.common.constants.DpConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "The base response object for all API responses.")
public class BaseResponse<I extends Serializable> implements BaseMarkerDTO {

    @Schema(description = "The ID of the entity.")
    private I id;

    @Schema(description = "The username of the user who created the entity.")
    private String createdBy;

    @Schema(description = "The creation date of the entity.")
    @JsonFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime createdAt;

    @Schema(description = "The username of the user who last modified the entity.")
    private String updatedBy;

    @Schema(description = "The last modified date of the entity.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DpConstants.DateFormat.DATETIME_PATTERN, lenient = OptBoolean.FALSE)
    private LocalDateTime updatedAt;
}
