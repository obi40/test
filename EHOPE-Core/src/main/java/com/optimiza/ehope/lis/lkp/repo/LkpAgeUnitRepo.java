package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;

@Repository("LkpAgeUnitRepo")
public interface LkpAgeUnitRepo extends GenericRepository<LkpAgeUnit> {

	@Query("select t from LkpAgeUnit t where t.code = :code")
	public LkpAgeUnit findByCode(@Param("code") String code);

}
