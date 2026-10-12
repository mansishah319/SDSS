/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.solution;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.dto.feedback.SolutionCommentResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
@Schema(description = "The Solution response object for Solution responses.")
public class SolutionFullResponse extends BaseResponse<Integer> {

    private String solutionText;

    private Boolean isFinal;
    private Boolean isAdminAgree;

    private int queLevel;
    private int solutionColor;
    private int voteColor;
    private int voteCount;
    private int point;
    private int rank;

    private Double averageRating;
    private Long ratingCount;
    private Double participantAverageRating;
    private Long participantRatingCount;
    private Double validatorAverageRating;
    private Long validatorRatingCount;
    private Double commanderAverageRating;
    private Long commanderRatingCount;

    private List<SolutionCommentResponse> solutionCommentList;
}
