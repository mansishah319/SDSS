/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.solution;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.feedback.CreateSolutionCommentRequest;
import com.mgdsw.sdss.dto.feedback.SolutionCommentResponse;
import com.mgdsw.sdss.entity.solution.SolutionComment;
import com.mgdsw.sdss.repository.solution.SolutionCommentRepository;
import com.mgdsw.sdss.service.SolutionCommentService;
import com.mgdsw.sdss.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class SolutionCommentServiceImpl extends AbstractBaseServiceImpl<Integer, SolutionComment> implements SolutionCommentService {

    private final SolutionCommentRepository solutionCommentRepository;
    private final SolutionService solutionService;

    @Override
    protected SolutionCommentRepository repository() {
        return solutionCommentRepository;
    }

    @Override
    public SolutionCommentResponse create(CreateSolutionCommentRequest request) {
        return super.create(
                SolutionComment.builder()
                        .solution(solutionService.findById(request.getSolutionId()))
                        .solComment(request.getSolComment())
                        .build(),
                SolutionCommentResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionCommentResponse> getAllBySolutionId(Integer solutionId) {
        return super.mapListEntityToListDto(solutionCommentRepository.findAllBySolution(solutionService.findById(solutionId)), SolutionCommentResponse.class);
    }
}