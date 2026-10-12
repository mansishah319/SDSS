/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.common.utils.ListActionsUtil;
import com.mgdsw.sdss.dto.template.EmailTemplatesRequest;
import com.mgdsw.sdss.dto.template.EmailTemplatesResponse;
import com.mgdsw.sdss.entity.variety.EmailTemplates;
import com.mgdsw.sdss.entity.variety.TemplateType;
import com.mgdsw.sdss.repository.variety.EmailTemplatesRepository;
import com.mgdsw.sdss.service.EmailTemplatesService;
import com.mgdsw.sdss.service.TemplateTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.AND_WITH_SPACES;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.COMMA_SPACE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailTemplatesServiceImpl extends AbstractBaseServiceImpl<Integer, EmailTemplates> implements EmailTemplatesService {

    private final EmailTemplatesRepository emailTemplatesRepository;
    private final TemplateTypeService templateTypeService;

    @Override
    protected EmailTemplatesRepository repository() {
        return emailTemplatesRepository;
    }

    @Override
    @Transactional
    public EmailTemplatesResponse createNewEmailTemplate(EmailTemplatesRequest request) {
        TemplateType templateType = validateAllRequirementsForCreationOrUpdateTemplateAndReturnType(request);
        EmailTemplates emailTemplates = super.modelMapper.map(request, EmailTemplates.class);
        emailTemplates.setTemplateType(templateType);
        return super.create(emailTemplates, EmailTemplatesResponse.class);
    }

    @Override
    @Transactional
    public EmailTemplatesResponse updateEmailTemplate(EmailTemplatesRequest request, Integer templateId) {
        TemplateType templateType = validateAllRequirementsForCreationOrUpdateTemplateAndReturnType(request);
        EmailTemplates emailTemplates = super.findById(templateId);
        super.modelMapper.map(request, emailTemplates);
        emailTemplates.setTemplateType(templateType);
        return modelMapper.map(emailTemplatesRepository.save(emailTemplates), EmailTemplatesResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailTemplates retrieveActiveEmailTemplatesByTemplateType(String templateKey) {
        TemplateType byTemplateTypeKey = templateTypeService.findByTemplateTypeAcceptNull(templateKey);
        return Objects.nonNull(byTemplateTypeKey) ? this.retrieveActiveEmailTemplateByType(byTemplateTypeKey) : null;
    }

    private TemplateType validateAllRequirementsForCreationOrUpdateTemplateAndReturnType(EmailTemplatesRequest request) {
        TemplateType templateType = retrieveTemplateTypeById(request.getTemplateTypeId());
        this.validateMandatoryFieldsBasedOnTemplateType(templateType, request);
        this.handleTheActiveTemplateProcessForTheSameTypeAndUpdateNew(templateType, request);
        return templateType;
    }

    private void handleTheActiveTemplateProcessForTheSameTypeAndUpdateNew(TemplateType templateType, EmailTemplatesRequest request) {
        if (Boolean.TRUE.equals(request.getIsDefault()))
            this.handleIfThereIsAnyActiveTemplateForTheSameType(templateType);
    }

    private void handleIfThereIsAnyActiveTemplateForTheSameType(TemplateType templateType) {
        EmailTemplates currentDefault = this.retrieveActiveEmailTemplateByType(templateType);
        if (Objects.nonNull(currentDefault)) {
            // Set the current default images to false
            log.info("Deactivating current default with ID: {}", currentDefault.getId());
            currentDefault.setIsDefault(Boolean.FALSE);
            emailTemplatesRepository.save(currentDefault);
        }
    }

    private EmailTemplates retrieveActiveEmailTemplateByType(TemplateType templateType) {
        return emailTemplatesRepository.findByTemplateTypeAndIsDefaultTrue(templateType);
    }

    private void validateMandatoryFieldsBasedOnTemplateType(TemplateType templateType, EmailTemplatesRequest request) {
        String mandatoryFieldsEnMain = templateType.getMandatoryFieldsEn();
        String mandatoryFieldsArMain = templateType.getMandatoryFieldsAr();

        // Check if both mandatory fields are non-null and non-blank
        if (StringUtils.isBlank(mandatoryFieldsEnMain) || StringUtils.isBlank(mandatoryFieldsArMain)) {
            return; // Early exit if mandatory fields are blank
        }

        List<String> missingFieldsEn = getMissingFields(mandatoryFieldsEnMain, request.getContentEn());
        List<String> missingFieldsAr = getMissingFields(mandatoryFieldsArMain, request.getContentAr());

        // Check if there are any missing fields
        if (!missingFieldsEn.isEmpty() || !missingFieldsAr.isEmpty()) {
            handleMissingFields(missingFieldsEn, missingFieldsAr);
        }
    }

    private List<String> getMissingFields(String mandatoryFields, String content) {
        List<String> mandatoryFieldsList = ListActionsUtil.convert(mandatoryFields);
        return mandatoryFieldsList.stream()
                .filter(field -> !content.contains(field))
                .collect(Collectors.toList());
    }

    private void handleMissingFields(List<String> missingFieldsEn, List<String> missingFieldsAr) {
        Locale currentLocale = DpCommonUtil.getUserCurrentLocale();
        String missingFieldsMessage;
        String messageCode;

        if (!missingFieldsEn.isEmpty() && !missingFieldsAr.isEmpty()) {
            missingFieldsMessage = buildMissingFieldsMessage(
                    LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_EN.getLocalizedMessage(currentLocale), missingFieldsEn,
                    LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_AR.getLocalizedMessage(currentLocale), missingFieldsAr);
            messageCode = buildMessageCode(LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_EN.getErrorCode(),
                    LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_AR.getErrorCode());
        } else if (!missingFieldsEn.isEmpty()) {
            missingFieldsMessage = buildSingleMissingFieldsMessage(
                    LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_EN.getLocalizedMessage(currentLocale), missingFieldsEn);
            messageCode = LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_EN.getErrorCode();
        } else {
            missingFieldsMessage = buildSingleMissingFieldsMessage(
                    LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_AR.getLocalizedMessage(currentLocale), missingFieldsAr);
            messageCode = LocalizedErrorCodes.MISSING_MANDATORY_FIELDS_AR.getErrorCode();
        }

        log.error("Validation error: {}", missingFieldsMessage);
        throw new CustomDataException(missingFieldsMessage, messageCode);
    }

    private String buildMissingFieldsMessage(String enMessage, List<String> missingFieldsEn, String arMessage, List<String> missingFieldsAr) {
        return String.format("%s [%s], %s [%s]",
                enMessage, String.join(COMMA_SPACE, missingFieldsEn),
                arMessage, String.join(COMMA_SPACE, missingFieldsAr));
    }

    private String buildSingleMissingFieldsMessage(String message, List<String> missingFields) {
        return String.format("%s [%s]", message, String.join(COMMA_SPACE, missingFields));
    }

    private String buildMessageCode(String enErrorCode, String arErrorCode) {
        return String.format("[%s %s %s]", enErrorCode, AND_WITH_SPACES, arErrorCode);
    }


    private TemplateType retrieveTemplateTypeById(Integer templateTypeId) {
        return templateTypeService.findById(templateTypeId);
    }
}