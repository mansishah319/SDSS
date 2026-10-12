/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.infrastructure.success.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgdsw.sdss.common.infrastructure.success.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination Response")
public class PaginationResponse<T> extends AbstractBaseResponse {

    @Schema(description = "The total amount elements in the collection")
    private Long totalElements;

    @Schema(description = "The total amount pages in the collection")
    private int numberOfPages;

    @Schema(description = "The maximum amount elements per page")
    private int pageSize;

    @Schema(description = "The current page number")
    private int pageNumber;

    @Schema(description = "The elements in the current page")
    private List<T> data;

    @Override
    public Boolean isSuccess() {
        return Boolean.TRUE;
    }

    public static <E, R> PaginationResponse<R> initPaginationResponse(Page<E> page, Pageable pageable, List<R> data) {
        return PaginationResponse.<R>builder()
                .numberOfPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .data(data)
                .build();
    }
}
