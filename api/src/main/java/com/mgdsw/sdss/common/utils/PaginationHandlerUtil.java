/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
public final class PaginationHandlerUtil {

    public static <T> PaginationResponse<T> preparePaginationResponse(Page<T> page, Pageable pageable) {
        return PaginationResponse.<T>builder()
                .data(page.getContent())
                .numberOfPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .build();
    }

    private PaginationHandlerUtil() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
