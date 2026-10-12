/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.guest;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Data
@Builder
@NoArgsConstructor
@Schema(description = "Request object for Creating or Updating GuestAccess")
public class CreateGuestAccessRequest implements BaseMarkerDTO {


}
