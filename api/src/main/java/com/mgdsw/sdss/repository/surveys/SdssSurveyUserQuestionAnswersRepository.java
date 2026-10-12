/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.repository.surveys;

import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUserQuestionAnswers;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Repository
public interface SdssSurveyUserQuestionAnswersRepository extends BaseRepository<SdssSurveyUserQuestionAnswers, Integer> {

    Optional<SdssSurveyUserQuestionAnswers> findByQuestionAndUser(SdssSurveyQuestion question, SdssSurveyUsers user);

    @Query("SELECT a FROM SdssSurveyUserQuestionAnswers a WHERE a.user = :user AND a.question IN :questions")
    List<SdssSurveyUserQuestionAnswers> findByUserAndQuestions(@Param("user") SdssSurveyUsers user, @Param("questions") List<SdssSurveyQuestion> questions);

    @Query("SELECT a.correct, COUNT(a) FROM SdssSurveyUserQuestionAnswers a " +
            "WHERE a.question.id = :questionId AND a.correct IS NOT NULL " +
            "GROUP BY a.correct")
    List<Object[]> countTrueFalseAnswers(@Param("questionId") Integer questionId);

    @Query("SELECT o.optionEn, COUNT(o) FROM SdssSurveyUserQuestionAnswers a " +
            "JOIN a.selectedOptions o " +
            "WHERE a.question.id = :questionId " +
            "GROUP BY o.optionEn")
    List<Object[]> countOptionsAnswers(@Param("questionId") Integer questionId);

    @Query("SELECT o.id, o.optionEn, o.optionAr, o.createdBy, o.createdAt, o.updatedAt, o.updatedBy, COUNT(a) " +
            "FROM SdssSurveyQuestionOptions o " +
            "LEFT JOIN SdssSurveyUserQuestionAnswers a ON o MEMBER OF a.selectedOptions " +
            "WHERE o.question.id = :questionId " +
            "GROUP BY o.id, o.optionEn, o.optionAr, o.createdBy, o.createdAt, o.updatedAt, o.updatedBy")
    List<Object[]> countOptionsAnswersWithDetails(@Param("questionId") Integer questionId);

    @Query("SELECT COUNT(a) FROM SdssSurveyUserQuestionAnswers a " +
            "WHERE a.question.id = :questionId AND a.responseText IS NOT NULL")
    long countTextResponses(@Param("questionId") Integer questionId);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT 1 FROM SdssSurveyUserQuestionAnswers a WHERE a.user.id = :userId" +
            ") THEN TRUE ELSE FALSE END " +
            "FROM SdssSurveyUserQuestionAnswers a")
    Boolean hasUserAnswered(@Param("userId") Integer userId);

    List<SdssSurveyUserQuestionAnswers> findByQuestion(SdssSurveyQuestion question);

    @Query("SELECT sqa FROM SdssSurveyUserQuestionAnswers sqa " +
            "JOIN sqa.question q " +
            "JOIN q.round r " +
            "WHERE r.id = :roundId")
    List<SdssSurveyUserQuestionAnswers> findAllByRoundId(Integer roundId);

    @Query("SELECT a FROM SdssSurveyUserQuestionAnswers a WHERE a.question = :question AND a.probabilityOfOccurrence IS NOT NULL AND a.probabilityOfOccurrence <> 0")
    List<SdssSurveyUserQuestionAnswers> findNonZeroProbabilityByQuestion(@Param("question") SdssSurveyQuestion question);

    @Query("SELECT a FROM SdssSurveyUserQuestionAnswers a WHERE a.question = :question AND a.impactSeverity  IS NOT NULL AND a.impactSeverity  <> 0")
    List<SdssSurveyUserQuestionAnswers> findNonZeroImpactByQuestion(@Param("question") SdssSurveyQuestion question);

    List<SdssSurveyUserQuestionAnswers> findAllByQuestion(SdssSurveyQuestion question);

    long countByUser(SdssSurveyUsers user);

    @Query("SELECT DISTINCT r.survey.id FROM SdssSurveyUserQuestionAnswers a " +
            "JOIN a.question q " +
            "JOIN q.round r " +
            "WHERE a.user = :user")
    List<Integer> findDistinctAnsweredSurveyIdsByUser(@Param("user") SdssSurveyUsers user);

    @Query("SELECT MAX(a.createdAt) FROM SdssSurveyUserQuestionAnswers a WHERE a.user = :user")
    LocalDateTime findLastActivityDateByUser(@Param("user") SdssSurveyUsers user);
}
