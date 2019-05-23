package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.InsProvider;

@Repository("InsProviderRepo")
public interface InsProviderRepo extends GenericRepository<InsProvider> {

	//	@Query("SELECT ip FROM InsProvider ip LEFT JOIN FETCH ip.insNetwork LEFT JOIN FETCH ip.lkpCity LEFT JOIN FETCH ip.lkpCountry")
	//	List<InsProvider> searchInsProvider();
}
