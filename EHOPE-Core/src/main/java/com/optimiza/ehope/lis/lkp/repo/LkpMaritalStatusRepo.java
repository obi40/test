package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpMaritalStatus;

@Repository("LkpMaritalStatusRepo")
public interface LkpMaritalStatusRepo extends GenericRepository<LkpMaritalStatus> {

}
