package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpRangeUnit;

@Repository("LkpRangeUnitRepo")
public interface LkpRangeUnitRepo extends GenericRepository<LkpRangeUnit> {

	@Query("select t from LkpRangeUnit t where t.code = :code")
	public LkpRangeUnit findByCode(@Param("code") String code);

}
