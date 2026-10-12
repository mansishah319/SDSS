/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.solution;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.solution.SolutionComment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Repository
public interface SolutionCommentRepository extends BaseRepository<SolutionComment, Integer> {

    List<SolutionComment> findAllBySolution(Solution solution);
}
