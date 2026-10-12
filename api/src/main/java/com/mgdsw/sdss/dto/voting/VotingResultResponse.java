/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.voting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseMarkerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VotingResultResponse implements BaseMarkerDTO {

    private List<String> solutionTextList;
    private List<Long> countOfVotes;
    private Map<String, Long> solutionVotingResult;

    @JsonProperty("totalVotes")
    public long getSumOfVotes() {
        if (countOfVotes == null) {
            return 0;
        }
        return countOfVotes.stream().mapToLong(Long::longValue).sum();
    }

    @JsonProperty("percentageOfVotes")
    public List<Double> getPercentageOfVotes() {
        long sumOfVotes = getSumOfVotes();
        if (sumOfVotes == 0) {
            return countOfVotes.stream().map(v -> 0.0).collect(Collectors.toList());
        }
        return countOfVotes.stream()
                .map(v -> (v * 100.0) / sumOfVotes)
                .collect(Collectors.toList());
    }
}
