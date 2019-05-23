package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.AntiMicrobial;

/**
 * AntiMicrobialRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Repository("AntiMicrobialRepo")
public interface AntiMicrobialRepo extends GenericRepository<AntiMicrobial> {

}
