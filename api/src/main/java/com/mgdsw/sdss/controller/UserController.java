/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.controller;

import com.mgdsw.sdss.common.helper.ControllerResponseHelper;
import com.mgdsw.sdss.common.helper.util.FilesUtil;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import com.mgdsw.sdss.dto.user.CreateAdminUserRequest;
import com.mgdsw.sdss.dto.user.RetrieveUsersBasedOnExpertisesRequest;
import com.mgdsw.sdss.dto.user.UpdateMyProfileRequest;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.dto.user.UserDropdownSearch;
import com.mgdsw.sdss.dto.user.UserResponse;
import com.mgdsw.sdss.dto.user.UserRolesResponse;
import com.mgdsw.sdss.dto.user.UserSearchRequest;
import com.mgdsw.sdss.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.mgdsw.sdss.common.constants.DpConstants.Authentication.MAIN_AUTH_SWAGGER_MODULE;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.UTF8;
import static com.mgdsw.sdss.common.helper.util.FilesUtil.DATE_TIME_FORMATTER;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_MODERATOR_PREAUTHORIZE;
import static com.mgdsw.sdss.util.DpRolesUtil.ADMIN_ONLY_PREAUTHORIZE;
import static com.mgdsw.sdss.util.UserUtils.EXCEL_SHEET_NAME;
import static com.mgdsw.sdss.util.UserUtils.USER_CREATION_AND_UPDATE_EXAMPLE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "User Controller")
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = MAIN_AUTH_SWAGGER_MODULE)
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create new Admin User")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @PostMapping
    public ResponseEntity<SuccessResponse<UserResponse>> addAdminUser(@Parameter(schema = @Schema(description = "Create Admin User Request"))
                                                                      @Valid @RequestBody CreateAdminUserRequest createAdminUserRequest,
                                                                      HttpServletRequest httpServletRequest) {
        createAdminUserRequest.setBaseUrl(
                ServletUriComponentsBuilder.fromRequestUri(httpServletRequest)
                        .replacePath(null)
                        .build()
                        .toUriString()
        );
        return ControllerResponseHelper.createResponse(userService.addAdminUser(createAdminUserRequest));
    }

    @Operation(summary = "Update Admin User")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @PutMapping
    public ResponseEntity<SuccessResponse<UserResponse>> updateAdminUser(@Parameter(schema = @Schema(description = "Update Admin User Request", example = USER_CREATION_AND_UPDATE_EXAMPLE))
                                                                         @RequestBody @Valid CreateAdminUserRequest createAdminUserRequest,
                                                                         HttpServletRequest request) {
        createAdminUserRequest.setBaseUrl(request.getRequestURL().toString());
        return ControllerResponseHelper.okResponse(userService.updateAdminUser(createAdminUserRequest));
    }

    @Operation(summary = "Update Admin User Status")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @PutMapping(value = "/update-user-status/{grpNumber}")
    public ResponseEntity<SuccessResponse<UserResponse>> updateAdminUser(@PathVariable Long grpNumber,
                                                                         @RequestParam String userStatus) {
        return ControllerResponseHelper.okResponse(userService.updateAdminUserStatus(grpNumber, userStatus));
    }

    @Operation(summary = "Update My Profile")
    @PutMapping(value = "/update-my-profile")
    public ResponseEntity<SuccessResponse<UserResponse>> updateMyProfile(@RequestBody UpdateMyProfileRequest request) {
        return ControllerResponseHelper.okResponse(userService.updateMyProfile(request));
    }

    @Operation(summary = "Get all Users Authorities")
    @GetMapping("/authorities")
    public ResponseEntity<SuccessResponse<UserRolesResponse>> getUserAuthorities() {
        return ControllerResponseHelper.okResponse(userService.getUserAuthorities());
    }

    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @Operation(summary = "Get all Users By Filtration")
    @PageableAsQueryParam
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<UserResponse>> userFiltration(UserSearchRequest search,
                                                                           @PageableDefault(size = Integer.MAX_VALUE)
                                                                           @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(userService.userFiltration(search, pageable));
    }

    @Operation(summary = "Change Current user language")
    @GetMapping("/change-lang")
    public ResponseEntity<SuccessResponse<UserResponse>> changeUserLanguage(@RequestParam String language) {
        return ControllerResponseHelper.okResponse(userService.changeUserLanguage(language));
    }

    @Operation(summary = "Retrieve User Language")
    @GetMapping("/user-lang")
    public ResponseEntity<SuccessResponse<String>> retrieveUserLanguageCode() {
        return ControllerResponseHelper.okResponse(userService.retrieveUserLanguage().getCode());
    }

    @Operation(summary = "Export all Users in Excel file")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @PageableAsQueryParam
    @PostMapping("/export/excel")
    public void exportUsersExcelSheet(HttpServletResponse response,
                                      @PageableDefault(size = Integer.MAX_VALUE, page = 1)
                                      @Parameter(hidden = true) Pageable pageable,
                                      @RequestBody UserSearchRequest search) throws IOException {
        response.setContentType(FilesUtil.CONTENT_TYPE);
        response.setHeader(FilesUtil.CONTENT_DISPOSITION,
                FilesUtil.ATTACHMENT_NAME + EXCEL_SHEET_NAME + LocalDateTime.now(ZoneId.of("Asia/Dubai")).format(DATE_TIME_FORMATTER) + FilesUtil.EXCEL_FILE_EXT);
        response.setCharacterEncoding(UTF8);
        response.setHeader(FilesUtil.ACCESS_CONTROL, FilesUtil.CONTENT_DISPOSITION_AND_SUGGESTED_FILENAME);
        IOUtils.copy(this.userService.exportUsersExcelSheet(search, pageable), response.getOutputStream());
    }

    @Operation(summary = "Get all Users Pageable")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @PageableAsQueryParam
    @GetMapping(value = "/pageable")
    public ResponseEntity<PaginationResponse<UserResponse>> getAllUsersPageable(@PageableDefault(size = Integer.MAX_VALUE, page = 1)
                                                                                @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get all Users without pagination")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Delete User By Grp Number")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @DeleteMapping(value = "/grp/{grpNumber}")
    public ResponseEntity<SuccessResponse<String>> deleteUserByGrp(@PathVariable Long grpNumber) {
        return ControllerResponseHelper.deleteResponse(userService.deleteUserByGrp(grpNumber));
    }

    @Operation(summary = "Account Confirmation")
    @GetMapping(value = "/confirm-account/{grpNumber}")
    public ResponseEntity<SuccessResponse<String>> confirmUserAccount(@PathVariable Long grpNumber, @RequestParam String confirmationToken) {
        return ControllerResponseHelper.okResponse(userService.confirmUserAccount(grpNumber, confirmationToken));
    }

    @Operation(summary = "Retrieve User By GRP Number")
    @PreAuthorize(ADMIN_ONLY_PREAUTHORIZE)
    @GetMapping(value = "/{grpNumber}")
    public ResponseEntity<SuccessResponse<UserResponse>> retrieveUserByGrpNumber(@PathVariable Long grpNumber) {
        return ControllerResponseHelper.okResponse(userService.retrieveUserByGrpNumber(grpNumber));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<SuccessResponse<UserResponse>> retrieveUserByGrpNumber() {
        return ControllerResponseHelper.okResponse(userService.retrieveUserByGrpNumber());
    }

    @GetMapping(value = "/validators")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Find All Validator Users")
    public ResponseEntity<SuccessResponse<List<UserDropDownListResponse>>> findValidatorUsers() {
        return ControllerResponseHelper.okResponse(userService.findValidatorUsers());
    }

    @GetMapping(value = "/active")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Find All Active Users")
    public ResponseEntity<SuccessResponse<List<UserDropDownListResponse>>> findAllActiveUsers() {
        return ControllerResponseHelper.okResponse(userService.findActiveUserMembers());
    }

    @GetMapping(value = "/active-based-on-expertise")
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @Operation(summary = "Active Users based on Expertise")
    public ResponseEntity<SuccessResponse<List<UserDropDownListResponse>>> retrieveUsersBasedOnAreaOfExpertiseAndLevel(
            @Parameter(example = "35400") @RequestParam(required = false) Integer areaOfExpertisesId,
            @Parameter(example = "expert") @RequestParam(required = false) String expertLevel
    ) {
        return ControllerResponseHelper.okResponse(userService.retrieveUsersBasedOnAreaOfExpertiseAndLevel(
                RetrieveUsersBasedOnExpertisesRequest.builder()
                        .areaOfExpertisesId(areaOfExpertisesId)
                        .expertLevel(expertLevel)
                        .build()
        ));
    }

    @Operation(summary = "Get all Users Pageable Based on them Expertises")
    @PageableAsQueryParam
    @PreAuthorize(ADMIN_MODERATOR_PREAUTHORIZE)
    @GetMapping(value = "/filter-by-expertises")
    public ResponseEntity<PaginationResponse<UserDropDownListResponse>> filterAllUsersWithPrimaryExpertisesSorted(UserDropdownSearch search,
                                                                                                                  @PageableDefault(size = Integer.MAX_VALUE, page = 1)
                                                                                                                  @Parameter(hidden = true) Pageable pageable) {
        return ControllerResponseHelper.okPaginationResponse(userService.filterAllUsersWithPrimaryExpertisesSorted(search, pageable));
    }
}
