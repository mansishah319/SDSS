/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.enduser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.enums.SdssStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static com.mgdsw.sdss.common.utils.DpDateUtil.DATE_TIME_FORMAT;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 25 Nov 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicRoundResponse {

    private Integer id;
    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;
    private Boolean manualHandle;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime updatedAt;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startTime;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endTime;
    private int roundNumber;
    private SdssStatus status;
    private Boolean active;
    private List<PublicQuestionResponse> questions;

    @JsonProperty("localTitle")
    public String getLocalTitle() {
        return DpCommonUtil.retrievePublicLocalValue(titleEn, titleAr);
    }

    @JsonProperty("localDescription")
    public String getLocalDescription() {
        return DpCommonUtil.retrievePublicLocalValue(descriptionEn, descriptionAr);
    }

    @JsonProperty("localStatus")
    public String getLocalStatus() {
        return DpCommonUtil.retrievePublicLocalValue(status.getEn(), status.getAr());
    }
}
