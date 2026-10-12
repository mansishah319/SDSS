/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.infrastructure.success;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(description = "The Abstract base response.")
public abstract class AbstractBaseResponse {

    @Schema(description = "Flag indicating whether the operation was successful or not", example = "true")
    private Boolean success;

    @Schema(description = "A message associated with the operation", example = "created successfully")
    private String message;

    @Schema(description = "A message code associated with the operation", example = "CREATED_SUCCESSFULLY")
    private String msgCode;

    @Schema(description = "Additional details about the operation", example = "User with email address 'john.doe@example.com' has been created")
    private String details;

    @Schema(description = "The URI path of the resource that was created/updated/deleted/etc.", example = "/api/v1/users/1234")
    private String uriPath;

    public abstract Boolean isSuccess();

    protected AbstractBaseResponse(Boolean success, String message, String msgCode) {
        this.success = success;
        this.message = message;
        this.msgCode = msgCode;
    }
}
