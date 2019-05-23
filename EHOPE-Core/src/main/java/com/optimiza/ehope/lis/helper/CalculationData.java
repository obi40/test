package com.optimiza.ehope.lis.helper;

import java.util.List;
import java.util.Set;

import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.wrapper.PaymentInformation;

public class CalculationData {

	private List<BillMasterItem> fetchedBmiList;
	private List<BillMasterItem> bmiNoPriceList;
	private BillPriceList defaultBillPriceList;
	private InsProviderPlan userInsProviderPlan;
	private List<PaymentInformation> paymentInformations;
	private Set<TestGroup> testGroupsSet;

	public CalculationData(List<BillMasterItem> fetchedBmiList, List<BillMasterItem> bmiNoPriceList, BillPriceList defaultBillPriceList,
			InsProviderPlan userInsProviderPlan, List<PaymentInformation> paymentInformations, Set<TestGroup> testGroupsSet) {
		super();
		this.fetchedBmiList = fetchedBmiList;
		this.bmiNoPriceList = bmiNoPriceList;
		this.defaultBillPriceList = defaultBillPriceList;
		this.userInsProviderPlan = userInsProviderPlan;
		this.paymentInformations = paymentInformations;
		this.testGroupsSet = testGroupsSet;
	}

	public Set<TestGroup> getTestGroupsSet() {
		return testGroupsSet;
	}

	public void setTestGroupsSet(Set<TestGroup> testGroupsSet) {
		this.testGroupsSet = testGroupsSet;
	}

	public List<BillMasterItem> getFetchedBmiList() {
		return fetchedBmiList;
	}

	public void setFetchedBmiList(List<BillMasterItem> fetchedBmiList) {
		this.fetchedBmiList = fetchedBmiList;
	}

	public List<BillMasterItem> getBmiNoPriceList() {
		return bmiNoPriceList;
	}

	public void setBmiNoPriceList(List<BillMasterItem> bmiNoPriceList) {
		this.bmiNoPriceList = bmiNoPriceList;
	}

	public BillPriceList getDefaultBillPriceList() {
		return defaultBillPriceList;
	}

	public void setDefaultBillPriceList(BillPriceList defaultBillPriceList) {
		this.defaultBillPriceList = defaultBillPriceList;
	}

	public InsProviderPlan getUserInsProviderPlan() {
		return userInsProviderPlan;
	}

	public void setUserInsProviderPlan(InsProviderPlan userInsProviderPlan) {
		this.userInsProviderPlan = userInsProviderPlan;
	}

	public List<PaymentInformation> getPaymentInformations() {
		return paymentInformations;
	}

	public void setPaymentInformations(List<PaymentInformation> paymentInformations) {
		this.paymentInformations = paymentInformations;
	}

}
