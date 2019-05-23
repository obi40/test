package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpSMSKey;

@Repository("LkpSMSKeyRepo")
public interface LkpSMSKeyRepo extends GenericRepository<LkpSMSKey> {

}
