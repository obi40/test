package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.InsProviderPlan;

@Repository("InsProviderPlanRepo")
public interface InsProviderPlanRepo extends GenericRepository<InsProviderPlan> {

	void deleteAllByInsProvider(InsProvider insProvider);
}
