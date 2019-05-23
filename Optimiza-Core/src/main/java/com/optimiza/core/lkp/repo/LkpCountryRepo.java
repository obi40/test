package com.optimiza.core.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.lkp.model.LkpCountry;

@Repository("LkpCountryRepo")
public interface LkpCountryRepo extends GenericRepository<LkpCountry> {

}
