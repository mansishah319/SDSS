/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.template.type.TemplateKeysResponse;
import com.mgdsw.sdss.dto.template.type.TemplateTypeRequest;
import com.mgdsw.sdss.dto.template.type.TemplateTypeResponse;
import com.mgdsw.sdss.entity.variety.TemplateType;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import com.mgdsw.sdss.repository.variety.TemplateTypeRepository;
import com.mgdsw.sdss.service.TemplateTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class TemplateTypeServiceImpl extends AbstractBaseServiceImpl<Integer, TemplateType> implements TemplateTypeService {

    private final TemplateTypeRepository templateTypeRepository;

    @Override
    protected TemplateTypeRepository repository() {
        return templateTypeRepository;
    }

    @Override
    @Transactional
    public TemplateTypeResponse createTemplateType(TemplateTypeRequest request) {
        checkIfTemplateKeyAlreadyExist(TemplatesTypeKeys.valueOf(request.getTemplateTypeKey()));
        TemplateType templateType = modelMapper.map(request, TemplateType.class);
        return modelMapper.map(templateTypeRepository.save(templateType), TemplateTypeResponse.class);
    }

    @Override
    @Transactional
    public TemplateTypeResponse updateTemplateType(Integer templateTypeId, TemplateTypeRequest request) {
        TemplateType templateType = super.findById(templateTypeId);
        modelMapper.map(request, templateType);
        applyMandatoryFieldsForEmailTemplateType(request, templateType);
        return modelMapper.map(templateTypeRepository.save(templateType), TemplateTypeResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateType findByTemplateTypeKey(String templateTypeKey) {
        log.info("We Try To found an images type with this KEY : {}.", templateTypeKey);
        return templateTypeRepository.findByTemplateTypeKey(TemplatesTypeKeys.valueOf(templateTypeKey))
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), templateTypeKey),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateType findByTemplateTypeAcceptNull(String templateTypeKey) {
        log.info("Found an images type with this KEY : {}.", templateTypeKey);
        return templateTypeRepository.findByTemplateTypeKey(TemplatesTypeKeys.valueOf(templateTypeKey)).orElse(null);
    }

    @Override
    public List<TemplateKeysResponse> getTemplateKeys() {
        return Stream.of(TemplatesTypeKeys.values())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TemplateKeysResponse mapToResponse(TemplatesTypeKeys typeKey) {
        return TemplateKeysResponse.builder()
                .templateTypeKey(typeKey)
                .build();
    }

    private void checkIfTemplateKeyAlreadyExist(TemplatesTypeKeys templateTypeKey) {
        Optional<TemplateType> optionalTemplateTypeKey = templateTypeRepository.findByTemplateTypeKey(templateTypeKey);
        if (optionalTemplateTypeKey.isPresent()) {
            Locale userCurrentLocale = DpCommonUtil.getUserCurrentLocale();
            throw new CustomDataException(
                    String.format(
                            translator.getMessage(LocalizedErrorCodes.ALREADY_EXIST.getErrorCode(), userCurrentLocale),
                            userCurrentLocale.equals(Locale.US) ? templateTypeKey.getEn() : templateTypeKey.getAr()
                    ),
                    LocalizedErrorCodes.ALREADY_EXIST.getErrorCode()
            );
        }
    }

    private void applyMandatoryFieldsForEmailTemplateType(TemplateTypeRequest request, TemplateType templateType) {
        if (Objects.nonNull(request.getMandatoryFieldsAr())) {
            templateType.setMandatoryFieldsAr(request.getMandatoryFieldsAr());
        }
        if (Objects.nonNull(request.getMandatoryFieldsEn())) {
            templateType.setMandatoryFieldsEn(request.getMandatoryFieldsEn());
        }
    }
}
