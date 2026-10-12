/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.surveys.round;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.utils.DateTimeHandlerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.AUTOMATIC_ROUND_MANDATORY_FIELDS;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.END_TIME_FUTURE_ERROR;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.START_END_TIME_ERROR;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.FailedMessages.START_TIME_FUTURE_ERROR;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_DESCRIPTION_TITLE_AR;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_DESCRIPTION_TITLE_EN;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_TITLE_AR;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.ROUND_DEFAULT_TITLE_EN;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdssSurveyRoundRequest {

    @Builder.Default
    @NotCommandOrScript
    private String titleEn = ROUND_DEFAULT_TITLE_EN;
    @Builder.Default
    @NotCommandOrScript
    private String titleAr = ROUND_DEFAULT_TITLE_AR;

    @Builder.Default
    @NotCommandOrScript
    private String descriptionEn = ROUND_DEFAULT_DESCRIPTION_TITLE_EN;
    @Builder.Default
    @NotCommandOrScript
    private String descriptionAr = ROUND_DEFAULT_DESCRIPTION_TITLE_AR;

    @Builder.Default
    private Boolean manualHandle = Boolean.TRUE;

    @NotCommandOrScript
    private String startTime;
    @NotCommandOrScript
    private String endTime;

    private int roundNumber;

    @JsonIgnore
    @AssertTrue(message = START_END_TIME_ERROR)
    public boolean isStartTimeBeforeEndTime() {
        if (ObjectUtils.isNotEmpty(startTime) && ObjectUtils.isNotEmpty(endTime) && Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            return DateTimeHandlerUtil.compare(startTime, endTime, LocalDateTime::isBefore);
        } else {
            return true;
        }
    }

    @JsonIgnore
    @AssertTrue(message = START_TIME_FUTURE_ERROR)
    public boolean isStartTimeAfterCurrentDate() {
        if (ObjectUtils.isNotEmpty(startTime) && Objects.nonNull(startTime)) {
            return DateTimeHandlerUtil.compare(startTime, LocalDateTime::isAfter);
        } else {
            return true;
        }
    }

    @JsonIgnore
    @AssertTrue(message = END_TIME_FUTURE_ERROR)
    public boolean isEndTimeAfterCurrentDate() {
        if (ObjectUtils.isNotEmpty(endTime) && Objects.nonNull(endTime)) {
            return DateTimeHandlerUtil.compare(endTime, LocalDateTime::isAfter);
        } else {
            return true;
        }
    }

    @JsonIgnore
    @AssertTrue(message = AUTOMATIC_ROUND_MANDATORY_FIELDS)
    public boolean isStartAndEndTimeNotNull() {
        return Boolean.TRUE.equals(manualHandle) ||
                (
                        Objects.nonNull(startTime) &&
                                Objects.nonNull(endTime) &&
                                StringUtils.isNotBlank(startTime) &&
                                StringUtils.isNotBlank(endTime)
                );
    }

    public String getTitleEn() {
        return titleEn != null ? titleEn : ROUND_DEFAULT_TITLE_EN;
    }

    public String getTitleAr() {
        return titleAr != null ? titleAr : ROUND_DEFAULT_TITLE_AR;
    }

    public String getDescriptionEn() {
        return descriptionEn != null ? descriptionEn : ROUND_DEFAULT_DESCRIPTION_TITLE_EN;
    }

    public String getDescriptionAr() {
        return descriptionAr != null ? descriptionAr : ROUND_DEFAULT_DESCRIPTION_TITLE_AR;
    }
}
