package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpSignum;

@Repository("LkpSignumRepo")
public interface LkpSignumRepo extends GenericRepository<LkpSignum> {

	@Query("select t from LkpSignum t where t.code = :code")
	public LkpSignum findByCode(@Param("code") String code);

}
