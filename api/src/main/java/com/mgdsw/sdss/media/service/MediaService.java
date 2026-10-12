/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.media.service;

import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.common.security.FileSecurityValidator;
import com.mgdsw.sdss.media.Base64Helper;
import com.mgdsw.sdss.media.helper.FileHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class MediaService {

    private final FileSecurityValidator fileSecurityValidator;

    public String retrieveBase64Images(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(filename);
        log.info("File name is {}, and extension is {}", filename, extension);

        // Validate file security before processing
        fileSecurityValidator.validateFileSecurity(file);

        if (Objects.nonNull(extension)) {
            FileHelper.checkImages(extension);
        } else {
            throw new CustomDataException(
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getErrorCode(),
                    ErrorCodes.UNSUPPORTED_MEDIA_TYPE.getMessage());
        }
        return Base64Helper.encodeToBase64(file);
    }
}
