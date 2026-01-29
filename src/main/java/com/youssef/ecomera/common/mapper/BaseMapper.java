package com.youssef.ecomera.common.mapper;

import com.youssef.ecomera.common.audit.BaseEntity;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Base mapper interface providing common mapping methods for all entity-dto pairs.
 */
public interface BaseMapper<E extends BaseEntity, D> {

    D toDto(E entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    E toEntity(D d);

    List<D> toDtoList(List<E> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    List<E> toEntityList(List<D> ds);
}