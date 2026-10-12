/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.dto.surveys.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mgdsw.sdss.common.annotations.NotCommandOrScript;
import com.mgdsw.sdss.common.config.custom.SingleOrArrayDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 28 Nov 2024
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedUserSingleAnswerRequest {

    @DecimalMin(value = "0.0", message = "Probability of occurrence must be at least 0")
    @DecimalMax(value = "100.0", message = "Probability of occurrence must not exceed 100")
    private Double probabilityOfOccurrence;
    @JsonDeserialize(using = SingleOrArrayDeserializer.class)
    private List<Integer> optionIds;
    @DecimalMin(value = "0.0", message = "Impact severity must be at least 0")
    @DecimalMax(value = "100.0", message = "Impact severity must not exceed 100")
    private Double impactSeverity;
    @NotCommandOrScript
    @Size(max = 1000, message = "Response text must not exceed 1000 characters")
    private String responseText;
    private Integer questionId;
    private Boolean correct;
}
