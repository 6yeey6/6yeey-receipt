package com.ibg.receipt.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.base.service.BaseService;

public abstract class BaseServiceImpl<T extends BaseModel, R extends BaseRepository<T>> implements BaseService<T> {

    @Autowired
    protected R repository;

    protected void setRepository(R repository) {};

    @Override
    public T save(T entity) {
        Assert.notNull(entity, "对象");

        Date date = new Date();
        if (entity.getId() == null) {
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
            entity.setVersion(0);
        } else {
            entity.setUpdateTime(date);
        }

        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        Assert.notNull(entity, "对象");
        Assert.notNull(entity.getId(), "主键");

        Date date = new Date();
        entity.setUpdateTime(date);

        return repository.save(entity);
    }

    @Override
    public List<T> update(List<T> entities) {
        Assert.notNull(entities, "对象");

        Date date = new Date();

        for (T entity : entities) {
            entity.setUpdateTime(date);
        }
        return repository.save(entities);
    }


    @Override
    public T get(Long id) {
        Assert.notNull(id, "主键");
        return repository.findOne(id);
    }

    @Override
    public T load(Long id) {
        T entity = get(id);
        Assert.notExist(entity, "对象");
        return entity;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

}
