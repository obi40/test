package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpTestEntryType;

@Repository("LkpTestEntryTypeRepo")
public interface LkpTestEntryTypeRepo extends GenericRepository<LkpTestEntryType> {

	@Query("select et from LkpTestEntryType et where et.code = :code")
	public LkpTestEntryType getEntryTypeByCode(@Param("code") String code);

}
