/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.helper;

import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.infrastructure.success.dto.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

public final class ControllerResponseHelper {

    public static <O> ResponseEntity<SuccessResponse<O>> createResponse(O data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<O>builder()
                        .data(data)
                        .message(DpConstants.Messages.SuccessMessages.CREATED_SUCCESSFULLY)
                        .build());
    }

    public static <O> ResponseEntity<SuccessResponse<O>> okResponse(O data) {
        return ResponseEntity.ok(SuccessResponse.<O>builder()
                .data(data)
                .message(DpConstants.Messages.SuccessMessages.DATA_RETRIEVED_SUCCESSFULLY)
                .build());
    }

    public static <O> ResponseEntity<SuccessResponse<O>> deleteResponse(O data) {
        return ResponseEntity.ok(SuccessResponse.<O>builder()
                .data(data)
                .message(DpConstants.Messages.SuccessMessages.DELETED_SUCCESSFULLY)
                .build());
    }

    public static <O> ResponseEntity<SuccessResponse<O>> updateResponse(O data) {
        return ResponseEntity.ok(SuccessResponse.<O>builder()
                .data(data)
                .message(DpConstants.Messages.SuccessMessages.UPDATED_SUCCESSFULLY)
                .build());
    }

    public static <O> ResponseEntity<PaginationResponse<O>> okPaginationResponse(PaginationResponse<O> paginationResponse) {
        return ResponseEntity.ok(paginationResponse);
    }

    public static <O> ResponseEntity<O> okDataOnlyResponse(O data) {
        return ResponseEntity.ok(data);
    }

    private ControllerResponseHelper() {
        throw new IllegalStateException(DpConstants.UTILITY_CLASS);
    }
}
