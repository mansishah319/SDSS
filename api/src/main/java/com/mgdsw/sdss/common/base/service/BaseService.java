/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.base.service;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.infrastructure.success.dto.PaginationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * BaseService provides a set of generic CRUD operations and pagination
 * functionalities for entities extending the BaseEntity.
 *
 * @param <I> The type of the id of the entity.
 * @param <E> The type of the entity.
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */
public interface BaseService<I extends Serializable, E extends BaseEntity<I>> {

    /**
     * Creates a new entity.
     *
     * @param entity The entity to create.
     * @return The created entity.
     */
    E create(E entity);

    /**
     * Creates a new entity from a request object.
     *
     * @param <R>     The type of the request object.
     * @param request The request object containing entity data.
     * @return The created entity.
     */
    <R> E create(R request);

    /**
     * Creates a new entity from a request object and returns a response object.
     *
     * @param <R>      The type of the request object.
     * @param <O>      The type of the response object.
     * @param request  The request object containing entity data.
     * @param response The type of the response object.
     * @return The response object with the data of the created entity.
     */
    <R, O> O create(R request, Class<O> response);

    /**
     * Creates a new entity and returns a response object.
     *
     * @param <O>      The type of the response object.
     * @param entity   The entity to create.
     * @param response The type of the response object.
     * @return The response object with the data of the created entity.
     */
    <O> O create(E entity, Class<O> response);

    /**
     * Retrieves all entities with pagination.
     *
     * @param <O>      The type of the response object.
     * @param pageable The pagination information.
     * @param response The type of the response object.
     * @return A pagination response containing the paginated list of entities.
     */
    <O> PaginationResponse<O> getAllWithPagination(Pageable pageable, Class<O> response);

    /**
     * Updates an entity with data from a request object.
     *
     * @param <R>      The type of the request object.
     * @param <O>      The type of the response object.
     * @param request  The request object containing the updated data.
     * @param id       The id of the entity to update.
     * @param response The type of the response object.
     * @return The response object with the updated entity data.
     */
    <O, R> O update(R request, I id, Class<O> response);

    /**
     * Finds an entity by its id.
     *
     * @param id The id of the entity.
     * @return The entity with the specified id.
     */
    E findById(I id);

    /**
     * Finds an entity by its id.
     *
     * @param id The id of the entity.
     * @return The entity with the specified id.
     */
    E findByIdForPublicApis(I id);

    /**
     * Finds an entity by its id and returns an optional.
     *
     * @param id The id of the entity.
     * @return An optional containing the entity if found, else empty.
     */
    Optional<E> findOptionalById(I id);

    /**
     * Finds an entity by its id and returns a response object.
     *
     * @param <O>      The type of the response object.
     * @param id       The id of the entity.
     * @param response The type of the response object.
     * @return The response object with the entity data.
     */
    <O> O findById(I id, Class<O> response);

    /**
     * Filters entities based on a specification and returns a pagination response.
     *
     * @param <O>           The type of the response object.
     * @param searchRequest The specification for filtering.
     * @param pageable      The pagination information.
     * @param response      The type of the response object.
     * @return A pagination response containing the filtered list of entities.
     */
    <O> PaginationResponse<O> filter(Specification<E> searchRequest, Pageable pageable, Class<O> response);

    /**
     * Retrieves all entities with pagination.
     *
     * @param pageable The pagination information.
     * @return A pagination response containing the paginated list of entities.
     */
    PaginationResponse<E> getAllWithPagination(Pageable pageable);

    /**
     * Filters entities based on a specification and returns a pagination response.
     *
     * @param searchRequest The specification for filtering.
     * @param pageable      The pagination information.
     * @return A pagination response containing the filtered list of entities.
     */
    PaginationResponse<E> filter(Specification<E> searchRequest, Pageable pageable);

    /**
     * Retrieves all entities and maps them to response objects.
     *
     * @param <O>      The type of the response object.
     * @param response The type of the response object.
     * @return A list of response objects with the entity data.
     */
    <O> List<O> getAll(Class<O> response);

    /**
     * Retrieves all entities with pagination and maps them to response objects.
     *
     * @param <O>      The type of the response object.
     * @param response The type of the response object.
     * @param pageable The pagination information.
     * @return A list of response objects with the entity data.
     */
    <O> List<O> getAll(Class<O> response, Pageable pageable);

    /**
     * Retrieves all entities.
     *
     * @return A list of all entities.
     */
    List<E> getAll();

    /**
     * Filters entities based on a specification and returns a page of entities.
     *
     * @param searchRequest The specification for filtering.
     * @param pageable      The pagination information.
     * @return A page of filtered entities.
     */
    Page<E> filterReturnPage(Specification<E> searchRequest, Pageable pageable);

    /**
     * Maps a list of entities to a list of response objects.
     *
     * @param <O>      The type of the response object.
     * @param t        The list of entities.
     * @param response The type of the response object.
     * @return A list of response objects with the entity data.
     */
    <O> List<O> mapListEntityToListDto(List<E> t, Class<O> response);

    /**
     * Finds all entities with ids in the specified list.
     *
     * @param idList The list of ids.
     * @return A list of entities with the specified ids.
     */
    List<E> findAllInIdList(List<I> idList);

    /**
     * Deletes an entity by its id.
     *
     * @param id The id of the entity to delete.
     * @return A message indicating the result of the deletion.
     */
    String delete(I id);

    /**
     * Updates an entity with data from a request object.
     *
     * @param <R>     The type of the request object.
     * @param request The request object containing the updated data.
     * @param id      The id of the entity to update.
     * @return The updated entity.
     */
    <R> E update(R request, I id);

    /**
     * Updates an entity.
     *
     * @param entity The entity to update.
     */
    void update(E entity);

    /**
     * Maps a list of entities to a list of response objects.
     *
     * @param <O>        The type of the response object.
     * @param entityList The list of entities.
     * @param response   The type of the response object.
     * @return A list of response objects with the entity data.
     */
    <O> List<O> mapList(List<E> entityList, Class<O> response);

    /**
     * Checks if an entity with the specified id exists.
     *
     * @param id The id of the entity.
     * @return True, if the entity exists, else false.
     */
    boolean existsById(Integer id);

    /**
     * Checks who created the entity with the specified id.
     *
     * @param id The id of the entity.
     * @return True if the current user created the entity, else false.
     */
    Boolean checkWhoCreated(I id);

    /**
     * Checks who created the specified entity.
     *
     * @param entity The entity.
     * @return True if the current user created the entity, else false.
     */
    Boolean checkWhoCreatedThisRecordByEntity(E entity);
}
