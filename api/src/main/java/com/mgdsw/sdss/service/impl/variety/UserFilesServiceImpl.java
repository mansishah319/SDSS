/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.service.impl.variety;

import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomServiceException;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.service.UserFilesService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 15 Jan 2025
 */

@Service
public class UserFilesServiceImpl implements UserFilesService {

    @Override
    public Resource downloadFile(String fileName) {
        try {
            // Define the file path (adjust as per your directory structure)
            Resource resource = new ClassPathResource("static/files/" + fileName);

            if (!resource.exists()) {
                throw new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), fileName),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode());
            }
            // Set response headers for downloading
            return resource;

        } catch (Exception e) {
            throw new CustomServiceException(
                    String.format(LocalizedErrorCodes.DOWNLOAD_FILE_ERROR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), fileName),
                    LocalizedErrorCodes.DOWNLOAD_FILE_ERROR.getErrorCode());
        }
    }
}
