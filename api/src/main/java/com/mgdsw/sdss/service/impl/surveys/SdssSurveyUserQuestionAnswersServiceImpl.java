/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.dto.surveys.questions.ReportSurveyQuestionAnalysis;
import com.mgdsw.sdss.dto.surveys.questions.options.OptionStatisticResponse;
import com.mgdsw.sdss.dto.surveys.user.InvitedUserSingleAnswerRequest;
import com.mgdsw.sdss.dto.surveys.user.SdssSurveyUserQuestionAnswersResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUserQuestionAnswers;
import com.mgdsw.sdss.entity.surveys.SdssSurveyUsers;
import com.mgdsw.sdss.enums.QuestionType;
import com.mgdsw.sdss.mapper.SurveyUserQuestionAnswersMapper;
import com.mgdsw.sdss.repository.surveys.SdssSurveyUserQuestionAnswersRepository;
import com.mgdsw.sdss.service.SdssSurveyQuestionService;
import com.mgdsw.sdss.service.SdssSurveyUserQuestionAnswersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.utils.FieldNamesUtil.FALSE_COUNT;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.TOTAL_COUNT;
import static com.mgdsw.sdss.common.utils.FieldNamesUtil.TRUE_COUNT;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Service
@RequiredArgsConstructor
public class SdssSurveyUserQuestionAnswersServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurveyUserQuestionAnswers> implements SdssSurveyUserQuestionAnswersService {

    private final SdssSurveyUserQuestionAnswersRepository repository;

    private final SdssSurveyQuestionService sdssSurveyQuestionService;
    private final SurveyUserQuestionAnswersMapper mapper;
    private final EmployeeService employeeService;

    @Override
    protected SdssSurveyUserQuestionAnswersRepository repository() {
        return repository;
    }

    @Override
    @Transactional
    public List<SdssSurveyUserQuestionAnswersResponse> createNewUserQuestionAnswers(List<InvitedUserSingleAnswerRequest> requestList, SdssSurveyUsers invitedUser) {
        return requestList.stream().map(req ->
                mapper.toSdssSurveyUserQuestionAnswersResponse(this.handleUpdateAndCreateProcess(req, invitedUser))
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitedUserSingleAnswerRequest> retrieveCurrentActiveRoundUserAnswers(SdssSurveyUsers invitedUser, List<SdssSurveyQuestion> questions) {
        return questions.stream()
                .map(question -> repository.findByQuestionAndUser(question, invitedUser))
                .filter(Optional::isPresent) // Only keep non-empty Optionals
                .map(Optional::get) // Unwrap the Optional
                .map(mapper::mapToRequest) // Map to the request
                .collect(Collectors.toList()); // Collect into a list
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyUserQuestionAnswersResponse> retrieveCurrentUserQuestionAnswersBasedOnSurveyRound(SdssSurveyUsers invitedUser, List<SdssSurveyQuestion> questions) {
        return repository.findByUserAndQuestions(invitedUser, questions).stream().map(mapper::toSdssSurveyUserQuestionAnswersResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkIfUserHasAnswered(Integer userId) {
        return repository.hasUserAnswered(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Serializable> getStatisticsByQuestionIdAndTypeForFinalReport(SdssSurveyQuestion question) {
        QuestionType questionType = question.getQuestionType();
        switch (questionType) {
            case TRUE_FALSE:
                return getTrueFalseStatistics(question);
            case SINGLE_SELECT:
            case MULTI_SELECT:
                return getOptionStatistics(question);
            case TEXT:
                return getTextResponseStatistics(question);
            default:
                throw new IllegalArgumentException("Unsupported question type: " + questionType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyUserQuestionAnswers> retrieveAllAnswersForAllUsersByRoundId(Integer roundId) {
        return repository.findAllByRoundId(roundId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyUserQuestionAnswers> retrieveAllAnswersForAllUsersByQuestion(SdssSurveyQuestion question) {
        return repository.findAllByQuestion(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyUserQuestionAnswersResponse> retrieveAllDetailsForNonZeroProbabilityByQuestion(SdssSurveyQuestion question) {
        return repository.findNonZeroProbabilityByQuestion(question).stream().map(mapper::toSdssSurveyUserQuestionAnswersResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SdssSurveyUserQuestionAnswersResponse> retrieveAllDetailsForNonZeroImpactByQuestion(SdssSurveyQuestion question) {
        return repository.findNonZeroImpactByQuestion(question).stream().map(mapper::toSdssSurveyUserQuestionAnswersResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSurveyQuestionAnalysis generateReportSurveyQuestionAnalysis(SdssSurveyQuestion question) {
        List<SdssSurveyUserQuestionAnswers> answersList = this.findAllByQuestion(question);
        return ReportSurveyQuestionAnalysis.builder()
                .userQualificationSpecificationAnalysis(applyAnalysisByQualificationSpecification(answersList))
                .userNationalityAnalysis(applyAnalysisByUserNationalityCode(answersList))
                .userMaritalStatusAnalysis(applyAnalysisByMaritalStatus(answersList))
                .userDepartmentAnalysis(applyAnalysisByDepartment(answersList))
                .userJobAnalysis(applyAnalysisByJobName(answersList))
                .userGenderAnalysis(applyAnalysisByGender(answersList))
                .build();
    }

    private List<SdssSurveyUserQuestionAnswers> findAllByQuestion(SdssSurveyQuestion question) {
        return repository.findByQuestion(question);
    }

    private Map<Long, Employee> retrieveEmployeeInMapWithHisID(List<SdssSurveyUserQuestionAnswers> answers) {
        // Get all employee numbers from answers
        List<Long> employeeNumbers = answers.stream()
                .map(a -> a.getUser().getEmployeeGrpNumber())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Fetch all employees in one query
        return employeeService.findAllByIdsIn(employeeNumbers)
                .stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity()));
    }

    private Map<String, Long> applyAnalysisByUserNationalityCode(List<SdssSurveyUserQuestionAnswers> answers) {
        // Fetch all employees in one query
        Map<Long, Employee> employeesMap = retrieveEmployeeInMapWithHisID(answers);

        // Perform counting by nationality code only
        return answers.stream()
                .filter(a -> a.getUser() != null && employeesMap.containsKey(a.getUser().getEmployeeGrpNumber()))
                .collect(Collectors.groupingBy(
                        a -> employeesMap.get(a.getUser().getEmployeeGrpNumber()).getNationality(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> applyAnalysisByDepartment(List<SdssSurveyUserQuestionAnswers> answers) {
        // Fetch all employees in one query
        Map<Long, Employee> employeesMap = retrieveEmployeeInMapWithHisID(answers);

        // Now perform the counting by department
        return answers.stream()
                .filter(a -> a.getUser() != null && employeesMap.containsKey(a.getUser().getEmployeeGrpNumber()))
                .collect(Collectors.groupingBy(
                        a -> {
                            Employee emp = employeesMap.get(a.getUser().getEmployeeGrpNumber());
                            return emp.getDepartmentName() != null ? emp.getDepartmentName() : "UNKNOWN_DEPARTMENT";
                        },
                        Collectors.counting()
                ));
    }

    private Map<String, Long> applyAnalysisByJobName(List<SdssSurveyUserQuestionAnswers> answers) {
        // Fetch all employees in one query using the existing helper method
        Map<Long, Employee> employeesMap = retrieveEmployeeInMapWithHisID(answers);

        // Perform counting by jobName
        return answers.stream()
                .filter(a -> a.getUser() != null && employeesMap.containsKey(a.getUser().getEmployeeGrpNumber()))
                .collect(Collectors.groupingBy(
                        a -> {
                            Employee emp = employeesMap.get(a.getUser().getEmployeeGrpNumber());
                            return emp.getJobName() != null ? emp.getJobName() : "UNKNOWN_JOB";
                        },
                        Collectors.counting()
                ));
    }

    private Map<String, Long> applyAnalysisByQualificationSpecification(List<SdssSurveyUserQuestionAnswers> answers) {
        // Fetch all employees in one query using the existing helper method
        Map<Long, Employee> employeesMap = retrieveEmployeeInMapWithHisID(answers);

        // Perform counting by qualificationSpecification
        return answers.stream()
                .filter(a -> a.getUser() != null && employeesMap.containsKey(a.getUser().getEmployeeGrpNumber()))
                .collect(Collectors.groupingBy(
                        a -> {
                            Employee emp = employeesMap.get(a.getUser().getEmployeeGrpNumber());
                            return emp.getQualificationSpecification() != null
                                    ? emp.getQualificationSpecification()
                                    : "UNKNOWN_QUALIFICATION";
                        },
                        Collectors.counting()
                ));
    }

    private Map<String, Long> applyAnalysisByMaritalStatus(List<SdssSurveyUserQuestionAnswers> answers) {
        Map<Long, Employee> employeesMap = retrieveEmployeeInMapWithHisID(answers);

        return answers.stream()
                .filter(a -> a.getUser() != null && employeesMap.containsKey(a.getUser().getEmployeeGrpNumber()))
                .collect(Collectors.groupingBy(
                        a -> {
                            Employee employee = employeesMap.get(a.getUser().getEmployeeGrpNumber());
                            if (employee.getMaritalStatusDesc() != null && !employee.getMaritalStatusDesc().trim().isEmpty()) {
                                return employee.getMaritalStatusDesc();
                            }
                            return employee.getMaritalStatus() != null && !employee.getMaritalStatus().trim().isEmpty()
                                    ? employee.getMaritalStatus()
                                    : "UNKNOWN_MARITAL_STATUS";
                        },
                        Collectors.counting()
                ));
    }

    private Map<String, Long> applyAnalysisByGender(List<SdssSurveyUserQuestionAnswers> answers) {
        Map<Long, Employee> employeesMap = retrieveEmployeeInMapWithHisID(answers);

        return answers.stream()
                .filter(a -> a.getUser() != null && employeesMap.containsKey(a.getUser().getEmployeeGrpNumber()))
                .collect(Collectors.groupingBy(
                        a -> {
                            String gender = employeesMap.get(a.getUser().getEmployeeGrpNumber()).getGender();
                            return gender != null && !gender.trim().isEmpty() ? gender : "UNKNOWN_GENDER";
                        },
                        Collectors.counting()
                ));
    }

    private Map<String, Serializable> getTrueFalseStatistics(SdssSurveyQuestion question) {
        List<Object[]> result = repository.countTrueFalseAnswers(question.getId());

        long trueCount = 0;
        long falseCount = 0;
        long totalCount = 0;

        for (Object[] row : result) {
            Boolean answer = (Boolean) row[0];
            Long count = (Long) row[1];
            if (Boolean.TRUE.equals(answer)) {
                trueCount = count;
            } else {
                falseCount = count;
            }
            totalCount += count;
        }

        Map<String, Serializable> statistics = new HashMap<>();
        statistics.put(TRUE_COUNT, trueCount);
        statistics.put(FALSE_COUNT, falseCount);
        statistics.put(TOTAL_COUNT, totalCount);
        calculatePublicQuestionAverages(question, statistics);
        return statistics;
    }

    private Map<String, Serializable> getOptionStatistics(SdssSurveyQuestion question) {
        List<Object[]> result = repository.countOptionsAnswersWithDetails(question.getId());

        // Ensure OptionStatisticResponse implements Serializable
        List<OptionStatisticResponse> optionStatistics = result.stream()
                .map(row -> OptionStatisticResponse.builder()
                        .id((Integer) row[0])
                        .optionEn((String) row[1])
                        .optionAr((String) row[2])
                        .createdBy((String) row[3])
                        .createdAt((LocalDateTime) row[4])
                        .updatedAt((LocalDateTime) row[5])
                        .updatedBy((String) row[6])
                        .count((Long) row[7])
                        .build())
                .collect(Collectors.toList());

        Set<String> uniqueCreatedBy = new HashSet<>();
        long totalCount = optionStatistics.stream()
                .filter(stat -> stat.getCreatedBy() != null)
                .filter(stat -> uniqueCreatedBy.add(stat.getCreatedBy()))
                .mapToLong(OptionStatisticResponse::getCount)
                .sum();

        if (totalCount == 0) {
            totalCount = optionStatistics.stream()
                    .mapToLong(OptionStatisticResponse::getCount)
                    .sum();
        }

        Map<String, Serializable> statistics = new HashMap<>();
        statistics.put("optionStatistics", new ArrayList<>(optionStatistics)); // ArrayList is Serializable
        statistics.put(TOTAL_COUNT, totalCount);
        calculatePublicQuestionAverages(question, statistics);
        return statistics;
    }

    private Map<String, Serializable> getTextResponseStatistics(SdssSurveyQuestion question) {
        long responseCount = repository.countTextResponses(question.getId());
        Map<String, Serializable> statistics = new HashMap<>();
        statistics.put(TOTAL_COUNT, responseCount);
        calculatePublicQuestionAverages(question, statistics);
        return statistics;
    }

    private SdssSurveyUserQuestionAnswers handleUpdateAndCreateProcess(InvitedUserSingleAnswerRequest request, SdssSurveyUsers invitedUser) {
        SdssSurveyQuestion surveyQuestion = sdssSurveyQuestionService.findById(request.getQuestionId());
        Optional<SdssSurveyUserQuestionAnswers> surveyUserQuestionAnswersOptional = repository.findByQuestionAndUser(surveyQuestion, invitedUser);
        if (surveyUserQuestionAnswersOptional.isPresent()) {
            SdssSurveyUserQuestionAnswers sdssSurveyUserQuestionAnswers = surveyUserQuestionAnswersOptional.get();
            return repository.save(mapper.updateEntity(sdssSurveyUserQuestionAnswers, request, surveyQuestion));
        } else {
            return repository.save(mapper.toEntity(request, invitedUser, surveyQuestion));
        }
    }

    private void calculatePublicQuestionAverages(SdssSurveyQuestion question, Map<String, Serializable> neededData) {
        if (Boolean.TRUE.equals(question.getShowPublicQuestions())) {
            List<SdssSurveyUserQuestionAnswers> answers = this.findAllByQuestion(question);

            double totalProbability = 0.0;
            double totalSeverity = 0.0;
            int count = 0;

            for (SdssSurveyUserQuestionAnswers answer : answers) {
                if (answer.getProbabilityOfOccurrence() != null && answer.getImpactSeverity() != null) {
                    totalProbability += answer.getProbabilityOfOccurrence();
                    totalSeverity += answer.getImpactSeverity();
                    count++;
                }
            }

            double avgProbability = count > 0 ? totalProbability / count : 0.0;
            double avgSeverity = count > 0 ? totalSeverity / count : 0.0;

            neededData.put("averageProbabilityOfOccurrence", avgProbability);
            neededData.put("averageImpactSeverity", avgSeverity);
        }
    }
}
