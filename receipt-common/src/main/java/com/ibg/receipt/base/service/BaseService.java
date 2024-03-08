package com.ibg.receipt.base.service;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.ibg.receipt.base.model.BaseModel;

public interface BaseService<T extends BaseModel> {

    T save(T entity);

    T update(T entity);

    List<T> update(List<T> entities);

    T get(Long id);

    T load(Long id);

    List<T> findAll();

    List<T> findAll(Sort sort);

}
