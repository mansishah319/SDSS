/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.solution.SolutionResponse;
import com.mgdsw.sdss.dto.voting.CreateVotingRequest;
import com.mgdsw.sdss.dto.voting.VotingResponse;
import com.mgdsw.sdss.dto.voting.VotingResultResponse;
import com.mgdsw.sdss.service.VotingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Feb, 2024
 */

@Validated
@RestController
@RequestMapping("/api/v1/voting")
@Tag(name = "Voting Controller")
@RequiredArgsConstructor
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class VotingController {

    private final VotingService votingService;

    @PostMapping
    public ResponseEntity<SuccessResponse<VotingResponse>> create(@Valid @RequestBody CreateVotingRequest request) {
        return ControllerResponseHelper.okResponse(votingService.create(request));
    }

    @DeleteMapping("remove-my-vote")
    public ResponseEntity<SuccessResponse<SolutionResponse>> removeCurrentUserVote(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(votingService.removeCurrentUserVote(solutionId));
    }

    @GetMapping("check-vote-for-current-user")
    public ResponseEntity<SuccessResponse<Boolean>> checkTheCurrentUserIsVoted(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(votingService.checkTheCurrentUserIsVoted(solutionId));
    }

    @GetMapping(value = "/solution")
    public ResponseEntity<SuccessResponse<List<VotingResponse>>> findBySolutionId(@RequestParam Integer solutionId) {
        return ControllerResponseHelper.okResponse(votingService.findBySolutionId(solutionId));
    }

    @GetMapping(value = "/challenge")
    public ResponseEntity<SuccessResponse<List<VotingResponse>>> findByChallengeId(@RequestParam Integer challengeId) {
        return ControllerResponseHelper.okResponse(votingService.findByChallengeId(challengeId));
    }

    @GetMapping(value = "/voting-result")
    public ResponseEntity<SuccessResponse<VotingResultResponse>> retrieveSolutionVotingResult(@RequestParam Integer challengeId) {
        return ControllerResponseHelper.okResponse(votingService.retrieveSolutionVotingResult(challengeId));
    }
}
