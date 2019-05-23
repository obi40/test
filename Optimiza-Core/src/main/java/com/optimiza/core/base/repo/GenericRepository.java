package com.optimiza.core.base.repo;

import org.springframework.data.repository.NoRepositoryBean;

import com.optimiza.core.base.entity.BaseEntity;


@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends BaseRepository<T, Long> {

}
