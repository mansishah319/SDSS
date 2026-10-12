/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.base.service.impl;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.base.BaseRepository;
import com.mgdsw.sdss.common.base.service.BaseService;
import com.mgdsw.sdss.common.enums.LocalizedErrorCodes;
import com.mgdsw.sdss.common.helper.Translator;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomAuthorizationException;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomNotFoundException;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.constants.DpConstants.Messages.SuccessMessages.DELETED_SUCCESSFULLY;
import static com.mgdsw.sdss.common.constants.DpConstants.Messages.SuccessMessages.PAGINATION_LOGGED_MESSAGE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Slf4j
public abstract class AbstractBaseServiceImpl<I extends Serializable, E extends BaseEntity<I>> implements BaseService<I, E> {

    protected abstract BaseRepository<E, I> repository();

    protected ModelMapper modelMapper;

    protected Translator translator;

    @Override
    public E create(E entity) {
        return repository().save(entity);
    }

    @Override
    public <R> E create(R request) {
        return repository().save(modelMapper.map(request, getEntityClass()));
    }

    @Override
    public <R, O> O create(R request, Class<O> response) {
        E entity = repository().save(modelMapper.map(request, getEntityClass()));
        return modelMapper.map(entity, response);
    }

    @Override
    public <O> O create(E entity, Class<O> response) {
        E savedEntity = repository().save(entity);
        return modelMapper.map(savedEntity, response);
    }

    @Override
    public E findById(I id) {
        log.info("This ID {} which you need it data relate to this Entity {}", id, getEntityClass().getSimpleName());
        return repository().findById(id)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale()), id),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    @Override
    public E findByIdForPublicApis(I id) {
        return repository().findById(id)
                .orElseThrow(() -> new CustomNotFoundException(
                        String.format(LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getLocalizedMessage(DpCommonUtil.getCurrentUserLocaleForPublicApis()), id),
                        LocalizedErrorCodes.NOT_FOUND_FIELD_ERR.getErrorCode()));
    }

    @Override
    @Transactional
    public String delete(I id) {
        E byId = findById(id);
        repository().delete(byId);
        return translator.getMessage(DELETED_SUCCESSFULLY);
    }

    @Override
    public List<E> findAllInIdList(List<I> idList) {
        return repository().findAllByIdIn(idList);
    }

    @Override
    @Transactional(readOnly = true)
    public <O> O findById(I id, Class<O> response) {
        return modelMapper.map(findById(id), response);
    }

    @Override
    public boolean existsById(Integer id) {
        return repository().existsById(id);
    }

    @Override
    public Optional<E> findOptionalById(I id) {
        return repository().findById(id);
    }

    @Override
    public <O, R> O update(R request, I id, Class<O> response) {
        E existEntity = findById(id);
        modelMapper.map(request, existEntity);
        existEntity.setId(id);
        E updatedEntity = repository().save(existEntity);
        return modelMapper.map(updatedEntity, response);
    }

    @Override
    public <R> E update(R request, I id) {
        E existEntity = findById(id);
        modelMapper.map(request, existEntity);
        existEntity.setId(id);
        return repository().save(existEntity);
    }

    @Override
    public void update(E entity) {
        repository().save(entity);
    }

    @Override
    public <O> List<O> getAll(Class<O> response) {
        return repository().findAll().stream().map(x -> modelMapper.map(x, response)).collect(Collectors.toList());
    }

    @Override
    public <O> List<O> getAll(Class<O> response, Pageable pageable) {
        return repository().findAll(pageable).stream().map(x -> modelMapper.map(x, response)).collect(Collectors.toList());
    }

    @Override
    public <O> List<O> mapListEntityToListDto(List<E> dtoList, Class<O> response) {
        return dtoList.stream().map(x -> modelMapper.map(x, response)).collect(Collectors.toList());
    }

    @Override
    public List<E> getAll() {
        return repository().findAll();
    }

    @Override
    public <O> PaginationResponse<O> getAllWithPagination(Pageable pageable, Class<O> response) {
        return retrievePagination(repository().findAll(pageable), pageable, response);
    }

    @Override
    public PaginationResponse<E> getAllWithPagination(Pageable pageable) {
        return retrievePagination(repository().findAll(pageable), pageable);
    }

    @Override
    public <O> PaginationResponse<O> filter(Specification<E> searchRequest, Pageable pageable, Class<O> response) {
        return retrievePagination(repository().findAll(searchRequest, pageable), pageable, response);
    }

    @Override
    public PaginationResponse<E> filter(Specification<E> searchRequest, Pageable pageable) {
        return retrievePagination(repository().findAll(searchRequest, pageable), pageable);
    }

    @Override
    public Page<E> filterReturnPage(Specification<E> searchRequest, Pageable pageable) {
        return repository().findAll(searchRequest, pageable);
    }

    @Override
    public <O> List<O> mapList(List<E> entityList, Class<O> response) {
        return entityList.stream().map(solution -> modelMapper.map(solution, response)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkWhoCreated(I id) {
        E entity = findById(id);
        return baseCheckWhoCreatedThisRecordByEntity(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkWhoCreatedThisRecordByEntity(E entity) {
        return baseCheckWhoCreatedThisRecordByEntity(entity);
    }

    public <O> PaginationResponse<O> retrievePagination(List<O> response, Pageable pageable, Page<E> page) {
        return PaginationResponse.<O>builder()
                .data(response)
                .numberOfPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }

    public <O> PaginationResponse<O> mapUserPaginationResponseToDTO(PaginationResponse<E> paginationResponse, List<O> data) {
        return PaginationResponse.<O>builder()
                .data(data)
                .numberOfPages(paginationResponse.getNumberOfPages())
                .totalElements(paginationResponse.getTotalElements())
                .pageNumber(paginationResponse.getPageNumber())
                .pageSize(paginationResponse.getPageSize())
                .build();
    }

    public <O> PaginationResponse<O> sortBasedOnField(int numberOfPages, Long totalElements, Pageable pageable, List<O> response, Comparator<O> comparator) {
        return PaginationResponse.<O>builder()
                .data(this.sortBasedOnField(response, pageable, comparator))
                .numberOfPages(numberOfPages)
                .totalElements(totalElements)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }

    public void checkIsTheCurrentUserAllowedToDoThat(String createdBy) {
        if (!DpCommonUtil.isTheCurrentUserAllowedToDoThisProcess(createdBy)) {
            throw new CustomAuthorizationException(
                    String.format(
                            LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getLocalizedMessage(DpCommonUtil.getUserCurrentLocale())),
                    LocalizedErrorCodes.NOT_AUTHORIZED_TO_APPLY_THIS_ACTION.getErrorCode()
            );
        }
    }

    @SuppressWarnings("unchecked")
    private Class<E> getEntityClass() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[1];
    }

    private PaginationResponse<E> retrievePagination(Page<E> page, Pageable pageable) {
        log.info(PAGINATION_LOGGED_MESSAGE, page.getTotalPages());
        return PaginationResponse.<E>builder()
                .data(page.getContent().stream().distinct().collect(Collectors.toList()))
                .numberOfPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }

    private <O> PaginationResponse<O> retrievePagination(Page<E> page, Pageable pageable, Class<O> response) {
        return PaginationResponse.<O>builder()
                .data(page.getContent().stream().map(p -> modelMapper.map(p, response)).collect(Collectors.toList()))
                .numberOfPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }

    private <O> List<O> sortBasedOnField(List<O> response, Pageable pageable, Comparator<O> comparator) {
        if (pageable.getSort().iterator().next().getDirection().toString().equalsIgnoreCase("asc")) {
            return response.stream().sorted(comparator).collect(Collectors.toList());
        } else {
            return response.stream().sorted(comparator.reversed()).collect(Collectors.toList());
        }
    }

    private Boolean baseCheckWhoCreatedThisRecordByEntity(E entity) {
        return DpCommonUtil.isTheCurrentUserAllowedToDoThisProcess(entity.getCreatedBy());
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setTranslator(Translator translator) {
        this.translator = translator;
    }
}
