/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.user;

import com.google.common.collect.ImmutableMap;
import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.config.security.app.AppConfiguration;
import com.mgdsw.sdss.common.config.security.jwt.JwtTokenProvider;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.helper.ExcelWriterHelper;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAlreadyExistException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.core.dto.DepartmentCoreResponse;
import com.mgdsw.sdss.core.dto.EmployeeGrpResponse;
import com.mgdsw.sdss.core.dto.SdssEmployeeResponse;
import com.mgdsw.sdss.core.entity.Employee;
import com.mgdsw.sdss.core.service.EmployeePhotoService;
import com.mgdsw.sdss.core.service.EmployeeService;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.dto.user.CreateAdminUserRequest;
import com.mgdsw.sdss.dto.user.RetrieveUsersBasedOnExpertisesRequest;
import com.mgdsw.sdss.dto.user.UpdateMyProfileRequest;
import com.mgdsw.sdss.dto.user.UserDashboardResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.dto.user.UserDropdownSearch;
import com.mgdsw.sdss.dto.user.UserExcelSheetResponse;
import com.mgdsw.sdss.dto.user.UserResponse;
import com.mgdsw.sdss.dto.user.UserRolesResponse;
import com.mgdsw.sdss.dto.user.UserSearchRequest;
import com.mgdsw.sdss.dto.user.expertises.UsersExpertisesResponse;
import com.mgdsw.sdss.email.dto.EmailDetails;
import com.mgdsw.sdss.email.service.EmailSender;
import com.mgdsw.sdss.entity.user.Permissions;
import com.mgdsw.sdss.entity.user.Roles;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UsersExpertises;
import com.mgdsw.sdss.entity.variety.Expertises;
import com.mgdsw.sdss.enums.ExpertLevel;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Operations;
import com.mgdsw.sdss.enums.Status;
import com.mgdsw.sdss.enums.SystemRoles;
import com.mgdsw.sdss.enums.UserType;
import com.mgdsw.sdss.repository.user.UserRepository;
import com.mgdsw.sdss.service.RolesService;
import com.mgdsw.sdss.service.SdssSurveyUsersService;
import com.mgdsw.sdss.service.UserService;
import com.mgdsw.sdss.service.UsersExpertisesService;
import com.mgdsw.sdss.specification.UserDropdownSpecification;
import com.mgdsw.sdss.specification.UserSpecification;
import com.mgdsw.sdss.util.InvitationUtil;
import com.mgdsw.sdss.util.UserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.QUESTION_MARK;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SLASH;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SPACE;
import static com.mgdsw.sdss.common.constants.DpConstants.DateFormat.DUBAI_ZONE_ID;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.SuccessMessages.DELETED_SUCCESSFULLY;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.SuccessMessages.SDSS_INVITATION_CONFIRMED_SUCCESSFULLY;
import static com.mgdsw.sdss.common.constants.DpConstants.Urls.CONFIRMATION_TOKEN_PARAM;
import static com.mgdsw.sdss.common.constants.DpConstants.Urls.CONFIRM_ACCOUNT;
import static com.mgdsw.sdss.common.enums.EmailSubject.SDSS_USER_CREATION_SUBJECT;
import static com.mgdsw.sdss.common.enums.Templates.USER_TEMPLATE_CREATION_AR;
import static com.mgdsw.sdss.common.enums.Templates.USER_TEMPLATE_CREATION_EN;
import static com.mgdsw.sdss.common.utils.DateTimeHandlerUtil.DUBAI_ZONE;
import static com.mgdsw.sdss.common.utils.TranslatorUtil.ClassFiledNames.Users.USERS_TITLE;
import static com.mgdsw.sdss.common.utils.TranslatorUtil.Message.NOT_FOUND;
import static com.mgdsw.sdss.util.UserUtils.Fields.USER;
import static com.mgdsw.sdss.util.UserUtils.Fields.USER_CONFIRMATION_URL;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
@AllArgsConstructor
public class UserServiceImpl extends AbstractBaseServiceImpl<Integer, User> implements UserService {

    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final RolesService rolesService;
    private final EmployeeService employeeService;
    private final AppConfiguration appConfiguration;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeePhotoService employeePhotoService;
    private final ExcelWriterHelper excelWriterHelper;
    private final UsersExpertisesService expertisesService;
    private final SdssSurveyUsersService sdssSurveyUsersService;

    @Override
    protected UserRepository repository() {
        return userRepository;
    }

    @Override
    public boolean isUserExistInSdssSystem(Long grpNumber) {
        return userRepository.findByGrpNumber(grpNumber).isPresent();
    }

    @Override
    public boolean isUserExistInSdssSystem() {
        return userRepository.findByGrpNumber(this.retrieveGrpNumber()).isPresent();
    }

    @Override
    @Transactional
    public UserResponse addAdminUser(CreateAdminUserRequest request) {
        User currentUser = retrieveCurrentUser();
        checkUserExistBeforeCreationByGrpNumber(request.getGrpNumber(), currentUser.getLanguage());
        Long grpNumber = request.getGrpNumber();
        this.checkExistenceByGrpNumber(grpNumber);
        User mappedUser = this.handleUserInitInformationBeforeActivation(request);
        this.updateUserInfoFromHisGrp(mappedUser, grpNumber);
        User savedUser = userRepository.save(mappedUser);
        this.handleUserExpertises(request, savedUser, Operations.CREATE);
        this.sendInvitationForCreatedUser(savedUser, request);
        UserResponse response = this.prepareUserResponse(savedUser);
        response.setConfirmationUrl(this.generateConfirmationURL(savedUser, request));
        sdssSurveyUsersService.createInvitedUserInfo(savedUser.getGrpNumber());
        return response;
    }

    @Override
    public User findUserByGrpNumber(Long grpNumber) {
        return userRepository.findByGrpNumber(grpNumber)
                .orElseThrow(() -> new CustomNotFoundException(
                        super.translator.getMessage(NOT_FOUND, Arrays.asList(super.translator.getMessage(USERS_TITLE), grpNumber)),
                        ErrorCodes.NOT_FOUND_ERR.getErrorCode()));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new CustomNotFoundException(
                        super.translator.getMessage(NOT_FOUND, Arrays.asList(super.translator.getMessage(USERS_TITLE), email)),
                        ErrorCodes.NOT_FOUND_ERR.getErrorCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByGrpNumberAndStatus(Long grpNumber, Status status) {
        Language language = retrieveUserLanguage();
        return handleRetrieveCurrentUserBasedOnLanguageAndIsActive(grpNumber, status, language);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByGrpNumberAndStatus(Long grpNumber, Status status, Language language) {
        return handleRetrieveCurrentUserBasedOnLanguageAndIsActive(grpNumber, status, language);
    }

    @Override
    public String confirmUserAccount(Long grpNumber, String confirmationToken) {
        if (Objects.isNull(confirmationToken))
            throw new CustomNotFoundException(
                    String.format(ErrorCodes.USER_ALREADY_ACTIVATED_OR_NOT_INVITED_YET.getMessage(), grpNumber),
                    ErrorCodes.USER_ALREADY_ACTIVATED_OR_NOT_INVITED_YET.getErrorCode());

        User user = userRepository.findByGrpNumberAndConfirmationToken(grpNumber, confirmationToken)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(ErrorCodes.USER_ALREADY_ACTIVATED_OR_NOT_INVITED_YET.getMessage(), grpNumber),
                        ErrorCodes.USER_ALREADY_ACTIVATED_OR_NOT_INVITED_YET.getErrorCode()));
        user.setIsActiveBefore(Boolean.TRUE);
        user.setStatus(Status.ACTIVE);
        user.setConfirmationToken(null);
        userRepository.save(user);
        return SDSS_INVITATION_CONFIRMED_SUCCESSFULLY;
    }

    @Override
    @Transactional(readOnly = true)
    public UserRolesResponse getUserAuthorities() {
        User user = this.findUserByGrpNumber(this.retrieveGrpNumber());
        Employee employee =
                this.findEmployeeById(user.getGrpNumber());
        log.info("User ID is {}", user.getId());
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .filter(Objects::nonNull)
                .map(Permissions::getNameEn)
                .collect(Collectors.toList());
        List<String> roleNames = user.getRoles().stream()
                .map(Roles::getNameEn)
                .collect(Collectors.toList());
        log.info("User have these roles : {}, and have these permissions : {}", roleNames, permissions);

        String phoneNumber = null;

        if (Objects.nonNull(user.getMobile())) {
            phoneNumber = user.getMobile();
        } else if (Objects.nonNull(employee)) {
            phoneNumber = employee.getTelephoneNumber1();
        }

        return UserRolesResponse.builder()
                .fullName(Objects.nonNull(employee) ? DpCommonUtil.getFullNameWithoutIdSplit(employee.getFullName()) : null)
                .language(user.getLanguage().getCode())
                .gradeName(Objects.nonNull(employee) ? employee.getGradeName() : null)
                .iss(DpCommonUtil.getUserPayload().getIss())
                .phoneNumber(phoneNumber)
                .userPhoto(employeePhotoService.retrieveEmployeeImageByGrpNumber(user.getGrpNumber()))
                .permissions(permissions)
                .ratings(user.getRatings())
                .decision(user.getDecision())
                .chatContents(user.getChatContents())
                .questionView(user.getQuestionView())
                .scrollingText(user.getScrollingText())
                .socialMediaFeeds(user.getSocialMediaFeeds())
                .internalDocuments(user.getInternalDocuments())
                .roles(roleNames)
                .build();
    }

    @Override
    @Transactional
    public UserResponse changeUserLanguage(String language) {
        User user = this.findUserByGrpNumber(this.retrieveGrpNumber());
        user.setLanguage(language.equalsIgnoreCase(Language.EN.getCode()) ? Language.EN : Language.AR);
        user.setId(user.getId());
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public PaginationResponse<UserResponse> getAllUsers(Pageable pageable) {
        return this.userPaginationResponse(super.getAllWithPagination(pageable));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return super.getAll().stream().map(this::prepareUserResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteUserByGrp(Long grpNumber) {
        User user = this.findUserByGrpNumber(grpNumber);
        user.setStatus(Status.DELETED);
        user.setDeletedAt(LocalDateTime.now(DUBAI_ZONE).atZone(ZoneId.of(DUBAI_ZONE_ID)).toLocalDateTime());
        userRepository.save(user);
        return "User with GrpNumber: " + grpNumber + SPACE + DELETED_SUCCESSFULLY;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<UserResponse> userFiltration(UserSearchRequest search, Pageable pageable) {
        return this.retrieveUsersAfterFiltration(search, pageable);
    }

    @Override
    public Language retrieveUserLanguage() {
        User user = this.findUserByGrpNumber(this.retrieveGrpNumber());
        return user.getLanguage();
    }

    @Override
    public User retrieveCurrentUser() {
        return this.findUserByGrpNumber(this.retrieveGrpNumber());
    }

    @Override
    public ByteArrayInputStream exportUsersExcelSheet(UserSearchRequest search, Pageable pageable) {
        String[] strings = UserUtils.retrieveUserRecordsNames();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {
            this.excelWriterHelper.write(super.modelMapper.map(
                    this.retrieveUsersAfterFiltration(search, pageable).getData(), new TypeToken<List<UserExcelSheetResponse>>() {
                    }.getType()), bos, strings, workbook);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            log.error("Exception while writing data to excel file {}", e.getMessage());
            throw new CustomServiceException("3001", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse retrieveUserByGrpNumber(Long grpNumber) {
        return this.prepareUserResponse(this.findUserByGrpNumber(grpNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse retrieveUserByGrpNumber() {
        return this.prepareUserResponse(this.findUserByGrpNumber(this.retrieveGrpNumber()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByGrpNumberInAndStatusAndRolesNameEn(List<Long> grpNumberList, String roleName) {
        return userRepository.findByGrpNumberInAndStatusAndRolesNameEn(grpNumberList, Status.ACTIVE, roleName.toUpperCase());
    }

    @Override
    @Transactional
    public UserResponse updateAdminUser(CreateAdminUserRequest update) {
        Long grpNumber = update.getGrpNumber();
        this.checkExistenceByGrpNumber(grpNumber);
        User user = this.findUserByGrpNumber(grpNumber);
        this.updateUserInfoFromHisGrp(user, grpNumber);
        user.setId(user.getId());
        User mappedUser = this.handleUserUpdate(update, user);
        User updatedUser = userRepository.save(mappedUser);
        return this.prepareUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse updateAdminUserStatus(Long grpNumber, String userStatus) {
        User user = this.findUserByGrpNumber(grpNumber);
        user.setStatus(Status.valueOf(userStatus.toUpperCase()));
        User updatedUser = userRepository.save(user);
        return this.prepareUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDropDownListResponse> findValidatorUsers() {
        return this.findUsersBasedOnRoleName(SystemRoles.VALIDATOR.getEn().toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDropDownListResponse> findModeratorUsers() {
        return this.findUsersBasedOnRoleName(SystemRoles.MODERATOR.getEn().toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDropDownListResponse> findActiveUserMembers() {
        return super.mapListEntityToListDto(userRepository.findAllByStatus(Status.ACTIVE), UserDropDownListResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDropDownListResponse> retrieveUsersBasedOnAreaOfExpertiseAndLevel(RetrieveUsersBasedOnExpertisesRequest userInvitationExpertisesRequest) {
        List<User> users = expertisesService.retrieveUsersBasedOnAreaOfExpertiseAndLevel(Collections.singletonList(userInvitationExpertisesRequest));
        return super.mapListEntityToListDto(users, UserDropDownListResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userDropdownCache", key = "{#expertLevel, #areaOfExpertise, T(com.mgdsw.sdss.common.utils.SecurityUtils).getCurrentSessionId()}")
    public List<UserDropDownListResponse> getAllUsersWithPrimaryExpertisesSorted(String expertLevel, String areaOfExpertise) {
        return userRepository.findAllByStatus(Status.ACTIVE).stream()
                .map(this::convertToUserDropDownListResponse)
                .sorted(getComparator(expertLevel, areaOfExpertise))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<UserDropDownListResponse> filterAllUsersWithPrimaryExpertisesSorted(UserDropdownSearch search, Pageable pageable) {

        // Execute paginated query
        Page<User> userPage = userRepository.findAll(new UserDropdownSpecification(search), pageable);

        // Convert to DTOs
        List<UserDropDownListResponse> responses = userPage.getContent().stream()
                .map(this::convertToUserDropDownListResponse)
                .collect(Collectors.toList());

        // Create a pagination response with filtered content
        return PaginationResponse.<UserDropDownListResponse>builder()
                .numberOfPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .data(responses)
                .build();
    }

    @Override
    public UserResponse updateMyProfile(UpdateMyProfileRequest request) {
        User user = updateMyProfileProcess(request, retrieveCurrentUser());
        this.updateUserInfoFromHisGrp(user, user.getGrpNumber());
        return this.prepareUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDashboardResponse> retrieveUsersByStatus(Status status) {
        return super.mapList(userRepository.findAllByStatus(status), UserDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByStatus(Status status) {
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDashboardResponse> retrieveInternalUsers() {
        return super.mapList(userRepository.findAllByIsInternalTrue(), UserDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDashboardResponse> retrieveExternalUsers() {
        return super.mapList(userRepository.findByIsInternalFalse(), UserDashboardResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllInternalUsers() {
        return userRepository.countByIsInternalTrue();
    }


    @Override
    @Transactional(readOnly = true)
    public long countAllExternalUsers() {
        return userRepository.countByIsInternalFalse();
    }

    @Override
    public void updateUserOnlineStatus(Long grpNumber, Boolean onlineStatus) {
        User user = this.findUserByGrpNumber(grpNumber);
        user.setOnlineStatus(onlineStatus);
        super.modelMapper.map(userRepository.save(user), ChallengeUsersResponse.class);
    }

    @Override
    public Long retrieveGrpNumber() {
        return Long.parseLong(jwtTokenProvider.getUserGrpNumberFromJWT(jwtTokenProvider.retrieveResolvedToken()));
    }

    @Override
    public List<User> findAllByGrpNumberIn(List<Long> grpNumberList) {
        return userRepository.findAllByGrpNumberIn(grpNumberList);
    }

    @Override
    public List<Long> retrieveAllSdssUsersGrpNumbers() {
        return Optional.ofNullable(userRepository.findAllGrpNumbers()).orElse(Collections.emptyList());
    }

    private User updateMyProfileProcess(UpdateMyProfileRequest request, User user) {
        if (Objects.nonNull(request.getAlternativeEmail())) {
            user.setAlternativeEmail(request.getAlternativeEmail());
        }

        if (Objects.nonNull(request.getAlternativeMobile())) {
            user.setAlternativeMobile(request.getAlternativeMobile());
        }

        return user;
    }

    private User handleRetrieveCurrentUserBasedOnLanguageAndIsActive(Long grpNumber, Status status, Language language) {
        return userRepository.findByGrpNumberAndStatus(grpNumber, status)
                .orElseThrow(() -> new CustomAuthorizationException(
                        String.format(LocalizedErrorCodes.NO_ACTIVE_USER_ERR.getLocalizedMessage(Objects.nonNull(language) ? new Locale(language.getCode()) : Locale.getDefault()), grpNumber),
                        LocalizedErrorCodes.NO_ACTIVE_USER_ERR.getErrorCode()));
    }

    private UserDropDownListResponse convertToUserDropDownListResponse(User user) {
        Employee employee = this.findEmployeeById(user.getGrpNumber());

        List<UsersExpertises> primaryExpertises = user.getExpertisesList().stream()
                .filter(UsersExpertises::getIsPrimary)
                .collect(Collectors.toList());

        ExpertLevel expertLevelForIsPrimary = primaryExpertises.isEmpty() ? null :
                primaryExpertises.get(0).getExpertLevel();

        Expertises areaOfExpertiseForIsPrimary = primaryExpertises.isEmpty() ? null :
                primaryExpertises.get(0).getExpertises();

        return UserDropDownListResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .grpNumber(user.getGrpNumber())
                .fullName(Objects.nonNull(employee) ?
                        DpCommonUtil.getFullNameWithoutIdSplit(employee.getFullName()) : null)
                .expertLevel(Objects.nonNull(expertLevelForIsPrimary) ?
                        retrievePrimaryExpertiseLevelBasedOnLang(expertLevelForIsPrimary) : null)
                .areaOfExpertise(Objects.nonNull(areaOfExpertiseForIsPrimary) ?
                        retrievePrimaryExpertiseBasedOnLang(areaOfExpertiseForIsPrimary) : null)
                .departmentName(Objects.nonNull(employee) ? employee.getDepartmentName() : null)
                .departmentId(Objects.nonNull(employee) ? employee.getDepartmentId() : null)
                .build();
    }

    private String retrievePrimaryExpertiseLevelBasedOnLang(ExpertLevel expertLevelForIsPrimary) {
        return DpCommonUtil.retrieveLocalValue(expertLevelForIsPrimary.getEnglishName(), expertLevelForIsPrimary.getArabicName());
    }

    private String retrievePrimaryExpertiseBasedOnLang(Expertises areaOfExpertiseForIsPrimary) {
        return DpCommonUtil.retrieveLocalValue(areaOfExpertiseForIsPrimary.getNameEn(), areaOfExpertiseForIsPrimary.getNameAr());
    }

    private Comparator<UserDropDownListResponse> getComparator(String expertLevel, String areaOfExpertise) {
        Comparator<UserDropDownListResponse> comparator = Comparator
                .comparing(UserDropDownListResponse::getExpertLevel, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(UserDropDownListResponse::getAreaOfExpertise, Comparator.nullsLast(Comparator.naturalOrder()));

        if (Objects.nonNull(expertLevel)) {
            comparator = comparator.thenComparing(user -> expertLevel.equals(user.getExpertLevel()) ? 0 : 1);
        }

        if (Objects.nonNull(areaOfExpertise)) {
            comparator = comparator.thenComparing(user -> areaOfExpertise.equals(user.getAreaOfExpertise()) ? 0 : 1);
        }

        return comparator;
    }

    private List<UserDropDownListResponse> findUsersBasedOnRoleName(String roleNameEn) {
        return userRepository.findByRolesNameEn(roleNameEn.toUpperCase()).stream().map(this::convertToUserDropDownListResponse).collect(Collectors.toList());
    }

    private UserResponse prepareUserResponse(User user) {
        UserResponse response =
                this.prepareCoreEmployeeResponse(user, modelMapper.map(user, UserResponse.class));
        Language language = retrieveUserLanguage();
        if (Objects.nonNull(language)) {
            this.handleUsersExpertisesResponse(user, response);
            this.retrieveMainUsersExpertisesLevel(user, language, response);
            response.setLanguage(language.getName());
            response.setStatus(language.equals(Language.EN) ?
                    user.getStatus().getNameEn() :
                    user.getStatus().getNameAr());
        }
        return this.prepareChallengeResponse(response);
    }

    private void handleUsersExpertisesResponse(User user, UserResponse response) {
        List<UsersExpertisesResponse> usersExpertisesResponses =
                expertisesService.retrieveUsersExpertisesBasedOnUser(user, retrieveUserLanguage());
        if (Objects.nonNull(usersExpertisesResponses) && !usersExpertisesResponses.isEmpty()) {
            response.setUsersExpertisesResponses(usersExpertisesResponses);
        } else {
            response.setUsersExpertisesResponses(null);
        }
    }

    private void retrieveMainUsersExpertisesLevel(User user, Language language, UserResponse response) {
        UsersExpertises mainUsersExpertises = expertisesService.findMainUsersExpertises(user);
        if (Objects.nonNull(mainUsersExpertises))
            response.setMainExpertLevel(language.equals(Language.EN) ?
                    mainUsersExpertises.getExpertLevel().getEnglishName() :
                    mainUsersExpertises.getExpertLevel().getArabicName()
            );
        else
            response.setMainExpertLevel(null);
    }

    private UserResponse prepareCoreEmployeeResponse(User user, UserResponse response) {
        SdssEmployeeResponse sdssEmployeeResponse =
                this.retrieveEmployeeWithDepartmentInfoByGrpNumber(user.getGrpNumber());
        if (Objects.nonNull(sdssEmployeeResponse)) {
            response.setDepartmentName(Objects.nonNull(
                    sdssEmployeeResponse.getDepartmentName()) ? sdssEmployeeResponse.getDepartmentName() : null);
            response.setGradeName(
                    Objects.nonNull(sdssEmployeeResponse.getGradeName()) ? sdssEmployeeResponse.getGradeName() : null);
            response.setTitle(
                    DpCommonUtil.retrieveLocalValue(sdssEmployeeResponse.getPersonalTitle(), sdssEmployeeResponse.getPersonTitleAr()));
            response.setFullName(Objects.nonNull(sdssEmployeeResponse.getFullName()) ?
                    DpCommonUtil.getFullNameWithoutIdSplit(sdssEmployeeResponse.getFullName()) : null);
            response.setParentDepartmentName(sdssEmployeeResponse.getParentDepartmentName());
            response.setNationality(sdssEmployeeResponse.getNationality());
            this.getSectorInfoForCoreEmployee(sdssEmployeeResponse, response);
        }
        return response;
    }

    private void getSectorInfoForCoreEmployee(SdssEmployeeResponse sdssEmployeeResponse, UserResponse response) {
        DepartmentCoreResponse sector = sdssEmployeeResponse.getSector();
        log.info("Employee Sector information is : -> {}", sector);
        if (Objects.nonNull(sector)) {
            response.setDepartmentType(Objects.nonNull(sector.getDepartmentType()) ? sector.getDepartmentType() : null);
            // DepartmentName inside a Sector object is referred to sector Name
            response.setSectorName(Objects.nonNull(sector.getDepartmentName()) ? sector.getDepartmentName() : null);
            response.setSectorShortName(Objects.nonNull(sector.getShortName()) ? sector.getShortName() : null);
        }
    }

    private PaginationResponse<UserResponse> userPaginationResponse(PaginationResponse<User> result, UserSearchRequest searchRequest) {
        List<UserResponse> userResponseList = result.getData().stream().map(this::prepareUserResponse).collect(Collectors.toList());
        if (!userResponseList.isEmpty() && Objects.nonNull(searchRequest.getSector())) {
            return super.mapUserPaginationResponseToDTO(result, userResponseList.stream().filter(
                    userResponse ->
                            Objects.nonNull(userResponse.getSectorName()) &&
                                    userResponse.getSectorName().contains(searchRequest.getSector())
            ).collect(Collectors.toList()));
        }
        return super.mapUserPaginationResponseToDTO(result, userResponseList);
    }

    private UserResponse prepareChallengeResponse(UserResponse userResponse) {
        List<UsersExpertisesResponse> primaryExpertises = Optional.ofNullable(userResponse)
                .map(UserResponse::getUsersExpertisesResponses)
                .orElse(Collections.emptyList())
                .stream()
                .filter(UsersExpertisesResponse::getIsPrimary)
                .collect(Collectors.toList());

        String expertLevelForIsPrimary = primaryExpertises.isEmpty() ? null :
                primaryExpertises.get(0).getExpertLevel();

        String areaOfExpertiseForIsPrimary = primaryExpertises.isEmpty() ? null :
                primaryExpertises.get(0).getExpertisesName();

        assert userResponse != null;
        userResponse.setAreaOfExpertise(areaOfExpertiseForIsPrimary);
        userResponse.setExpertLevel(expertLevelForIsPrimary);

        return userResponse;
    }

    private PaginationResponse<UserResponse> userPaginationResponse(PaginationResponse<User> result) {
        return super.mapUserPaginationResponseToDTO(result, result.getData().stream().map(this::prepareUserResponse).collect(Collectors.toList()));
    }

    private User handleUserInitInformationBeforeActivation(CreateAdminUserRequest request) {
        User user = modelMapper.map(request, User.class);
        user.setType(UserType.INTERNAL.getEnglishName());
        user.setIsInternal(Boolean.TRUE);
        user.setOnlineStatus(Boolean.FALSE);
        user.setStatus(Status.INACTIVE);
        user.setIsActiveBefore(Boolean.FALSE);
        user.setIsMailSend(Boolean.TRUE);
        if (Objects.nonNull(request.getIsDelphiModerator()))
            user.setIsDelphiModerator(request.getIsDelphiModerator());
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setRoles(this.handleUserRoles(request.getRoles()));
        return user;
    }

    private User handleUserUpdate(CreateAdminUserRequest request, User user) {
        User updatedUser = new User();
        modelMapper.map(user, updatedUser);
        modelMapper.map(request, updatedUser);
        if (Objects.nonNull(request.getIsDelphiModerator()))
            updatedUser.setIsDelphiModerator(request.getIsDelphiModerator());
        updatedUser.setId(user.getId());
        this.handleUserExpertises(request, updatedUser, Operations.UPDATE);
        updatedUser.setRoles(this.handleUserRoles(request.getRoles()));
        return updatedUser;
    }

    private void handleUserExpertises(CreateAdminUserRequest request, User user, Operations operations) {
        if (Objects.nonNull(request.getUsersExpertises()))
            expertisesService.createListOfExpertisesByUser(request.getUsersExpertises(), user, operations);
    }

    private List<Roles> handleUserRoles(List<Integer> roleIds) {
        if (roleIds.isEmpty())
            throw new CustomDataException("Roles is a mandatory field", "3333");
        return rolesService.retrieveAllUserRoles(roleIds);
    }

    private void sendInvitationForCreatedUser(User user, CreateAdminUserRequest request) {
        try {
            emailSender.sendEmail(
                    EmailDetails.builder()
                            .subject(SDSS_USER_CREATION_SUBJECT.getEn())
                            .to(InvitationUtil.getReceiverEmailsForTheUser(user))
                            .model(
                                    ImmutableMap.of(
                                            USER, user,
                                            USER_CONFIRMATION_URL, this.generateConfirmationURL(user, request)
                                    )
                            ).templateName(
                                    request.getLanguage().equalsIgnoreCase(Language.EN.getCode()) ?
                                            USER_TEMPLATE_CREATION_EN.getPath() :
                                            USER_TEMPLATE_CREATION_AR.getPath()
                            ).build());
            userRepository.save(user);
        } catch (Exception e) {
            log.error("The email has this issue --> {}", e.getMessage());
            user.setIsMailSend(Boolean.FALSE);
            userRepository.save(user);
        }
    }

    private String generateConfirmationURL(User user, CreateAdminUserRequest request) {
        String serverMainContext = appConfiguration.getServerMainContext();
        log.info("Server BaseURL is {}, and the server context is {}", request.getBaseUrl(), serverMainContext);
        return ObjectUtils.isEmpty(serverMainContext) ?
                request.getBaseUrl() + CONFIRM_ACCOUNT + SLASH + user.getGrpNumber() + QUESTION_MARK + CONFIRMATION_TOKEN_PARAM + user.getConfirmationToken() :
                request.getBaseUrl() + SLASH + serverMainContext + CONFIRM_ACCOUNT + SLASH + user.getGrpNumber() + QUESTION_MARK + CONFIRMATION_TOKEN_PARAM + user.getConfirmationToken();
    }

    private SdssEmployeeResponse retrieveEmployeeWithDepartmentInfoByGrpNumber(Long grpNumber) {
        return employeeService.retrieveEmployeeWithDepartmentInfoByGrpNumber(grpNumber);
    }

    private void updateUserInfoFromHisGrp(User user, Long grpNumber) {
        Employee employeeById = this.findEmployeeById(grpNumber);
        user.setJobId(employeeById.getJobId());
        user.setJobName(employeeById.getJobName());
        user.setFullName(employeeById.getFullName());
        user.setDepartmentId(employeeById.getDepartmentId());
        user.setDepartmentName(employeeById.getDepartmentName());
    }

    private Employee findEmployeeById(Long grpNumber) {
        return employeeService.findEmployeeByIdOrNull(grpNumber);
    }

    public PaginationResponse<UserResponse> retrieveUsersAfterFiltration(UserSearchRequest search, Pageable pageable) {
        return this.userPaginationResponse(super.filter(new UserSpecification(search), pageable), search);
    }

    private void checkExistenceByGrpNumber(Long grpNumber) {
        EmployeeGrpResponse employeeByGrp = employeeService.findEmployeeByGrp(grpNumber);
        log.info("This User GrpNumber {} is existed in our Grp System For Employee his name is → {}", grpNumber, employeeByGrp.getFullName());
    }

    private void checkUserExistBeforeCreationByGrpNumber(Long grpNumber, Language language) {
        userRepository.findByGrpNumber(grpNumber).ifPresent(user -> {
            throw new CustomAlreadyExistException(
                    String.format(LocalizedErrorCodes.USER_ALREADY_EXIST.getLocalizedMessage(Objects.nonNull(language) ? new Locale(language.getCode()) : Locale.getDefault()), grpNumber),
                    LocalizedErrorCodes.USER_ALREADY_EXIST.getErrorCode());
        });
    }
}
