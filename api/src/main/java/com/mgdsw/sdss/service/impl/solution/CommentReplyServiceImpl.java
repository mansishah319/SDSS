/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.solution;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.feedback.CommentReplyResponse;
import com.mgdsw.sdss.dto.feedback.CreateCommentReplyRequest;
import com.mgdsw.sdss.entity.solution.CommentReply;
import com.mgdsw.sdss.repository.solution.CommentReplyRepository;
import com.mgdsw.sdss.service.CommentReplyService;
import com.mgdsw.sdss.service.SolutionCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class CommentReplyServiceImpl extends AbstractBaseServiceImpl<Integer, CommentReply> implements CommentReplyService {

    private final CommentReplyRepository commentReplyRepository;
    private final SolutionCommentService solutionCommentService;

    @Override
    protected CommentReplyRepository repository() {
        return commentReplyRepository;
    }

    @Override
    public CommentReplyResponse create(CreateCommentReplyRequest request) {
        return super.create(
                CommentReply.builder()
                        .reply(request.getReply()).
                        solutionComment(solutionCommentService.findById(request.getCommentId())).build(),
                CommentReplyResponse.class);
    }
}