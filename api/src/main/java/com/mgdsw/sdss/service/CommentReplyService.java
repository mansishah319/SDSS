/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service;

import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.dto.feedback.CommentReplyResponse;
import com.mgdsw.sdss.dto.feedback.CreateCommentReplyRequest;
import com.mgdsw.sdss.entity.solution.CommentReply;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface CommentReplyService extends BaseService<Integer, CommentReply> {

    CommentReplyResponse create(CreateCommentReplyRequest request);
}
