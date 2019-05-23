package com.optimiza.ehope.lis.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillClassification;

@Repository("BillClassificationRepo")
public interface BillClassificationRepo extends GenericRepository<BillClassification> {

	@Query("select bc from BillClassification bc "
			+ "left outer join fetch bc.parentClassification "
			+ "where bc.rid = :rid")
	public BillClassification getByRidWithParent(@Param("rid") Long rid);

}
