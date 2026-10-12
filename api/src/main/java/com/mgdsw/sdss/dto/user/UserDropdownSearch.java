/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.user;

import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import com.mgdsw.sdss.enums.ExpertLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDropdownSearch implements BaseMarkerDTO {

    private String searchTerm;
    private String department;
    private String expertLevel;
    private String areaOfExpertise;
    private Boolean isPrimaryExpertise;

    public void setExpertLevel(String expertLevel) {
        this.expertLevel = ExpertLevel.fromString(expertLevel).name();
    }
}
