package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpBalanceTransactionType;

/**
 * LkpBalanceTransactionTypeRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jul/09/2018
 **/

@Repository("LkpBalanceTransactionTypeRepo")
public interface LkpBalanceTransactionTypeRepo extends GenericRepository<LkpBalanceTransactionType> {

}
