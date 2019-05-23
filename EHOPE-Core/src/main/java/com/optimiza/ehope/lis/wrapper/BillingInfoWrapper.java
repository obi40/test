package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.TestDefinition;

public class BillingInfoWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private BillMasterItem billMasterItem;
	private BillPricing billpricing;
	private List<TestDefinition> testDefinitionList = new ArrayList<>();
	private BigDecimal charge;
	private InsCoverageDetail insCoverageDetail;

	public BillMasterItem getBillMasterItem() {
		return billMasterItem;
	}

	public void setBillMasterItem(BillMasterItem billMasterItem) {
		this.billMasterItem = billMasterItem;
	}

	public BillPricing getBillpricing() {
		return billpricing;
	}

	public void setBillpricing(BillPricing billpricing) {
		this.billpricing = billpricing;
	}

	public List<TestDefinition> getTestDefinitionList() {
		return testDefinitionList;
	}

	public void setTestDefinitionList(List<TestDefinition> testDefinitionList) {
		this.testDefinitionList = testDefinitionList;
	}

	public void addItemToTestDefinitionList(TestDefinition testDefinition) {
		if (this.testDefinitionList == null) {
			this.testDefinitionList = new ArrayList<>();
		}
		this.testDefinitionList.add(testDefinition);
	}

	public BigDecimal getCharge() {
		return charge;
	}

	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}

	public InsCoverageDetail getInsCoverageDetail() {
		return insCoverageDetail;
	}

	public void setInsCoverageDetail(InsCoverageDetail insCoverageDetail) {
		this.insCoverageDetail = insCoverageDetail;
	}

}
