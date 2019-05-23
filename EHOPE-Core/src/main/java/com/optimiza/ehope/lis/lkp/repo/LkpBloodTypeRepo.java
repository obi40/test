package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpBloodType;

@Repository("LkpBloodTypeRepo")
public interface LkpBloodTypeRepo extends GenericRepository<LkpBloodType> {

}
