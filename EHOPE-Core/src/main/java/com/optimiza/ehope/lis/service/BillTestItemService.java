package com.optimiza.ehope.lis.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.repo.BillTestItemRepo;

@Service("BillTestItemService")
public class BillTestItemService extends GenericService<BillTestItem, BillTestItemRepo> {

	@Autowired
	private BillTestItemRepo repo;

	@Override
	protected BillTestItemRepo getRepository() {
		return repo;
	}

	public BillTestItem addBillTestItem(BillTestItem billTestItem) {
		return getRepository().save(billTestItem);
	}

	public BillTestItem editBillTestItem(BillTestItem billTestItem) {
		return getRepository().save(billTestItem);
	}

	public void deleteBillTestItem(Long rid) {
		getRepository().delete(rid);
	}

	public List<BillTestItem> getByBillMaster(BillMasterItem billMasterItem) {
		return getRepository().getByBillMaster(billMasterItem);
	}

	public List<BillTestItem> getByTestDefinitions(Collection<TestDefinition> testDefinitionList) {
		return getRepository().fetchByTestDefinitons(testDefinitionList);
	}

}
