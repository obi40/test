package com.optimiza.ehope.lis.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.TestDefinition;

@Repository("BillTestItemRepo")
public interface BillTestItemRepo extends GenericRepository<BillTestItem> {

	@Query("SELECT t FROM BillTestItem t WHERE t.billMasterItem = :billMasterItem")
	List<BillTestItem> getByBillMaster(@Param("billMasterItem") BillMasterItem billMasterItem);

	@Query("SELECT DISTINCT bti FROM BillTestItem bti "
			+ "LEFT JOIN FETCH bti.billMasterItem "
			+ "LEFT JOIN FETCH bti.testDefinition td "
			+ "WHERE td IN :testDefinitionList")
	List<BillTestItem> fetchByTestDefinitons(@Param("testDefinitionList") Collection<TestDefinition> testDefinitionList);

}
