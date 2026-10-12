/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Schema(description = "Retrieve Users Or Challenges Based On Expertises Request")
public class RetrieveUsersBasedOnExpertisesRequest {

    @Schema(description = "Area Of Expertises ID", example = "9650")
    private Integer areaOfExpertisesId;
    @Schema(description = "Expert Level", example = "expert")
    private String expertLevel;
}
