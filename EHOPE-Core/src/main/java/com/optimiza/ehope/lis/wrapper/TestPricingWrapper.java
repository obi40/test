package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;
import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.core.lkp.model.LkpCurrency;
import com.optimiza.ehope.lis.lkp.model.LkpPaymentMethod;
import com.optimiza.ehope.lis.model.BillPatientTransaction;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;

/**
 * TestPricingWrapper.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/24/2018
 **/
public class TestPricingWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private List<TestDefinition> testDefinitionList;
	private EmrPatientInsuranceInfo patientInsuranceInfo;
	private InsProviderPlan insProviderPlan;
	private List<PaymentInformation> paymentInformations;
	private EmrVisit patientVisit;
	private List<TestPayment> testPaymentList;
	private BigDecimal generalDiscountPercentage;
	private BigDecimal generalDiscountAmount;
	private List<LabTestActual> testActualList;
	private List<TestGroup> testGroupList;

	public List<PaymentInformation> getPaymentInformations() {
		return paymentInformations;
	}

	public void setPaymentInformations(List<PaymentInformation> paymentInformations) {
		this.paymentInformations = paymentInformations;
	}

	public List<TestGroup> getTestGroupList() {
		return testGroupList;
	}

	public void setTestGroupList(List<TestGroup> testGroupList) {
		this.testGroupList = testGroupList;
	}

	public List<LabTestActual> getTestActualList() {
		return testActualList;
	}

	public void setTestActualList(List<LabTestActual> testActualList) {
		this.testActualList = testActualList;
	}

	public BigDecimal getGeneralDiscountAmount() {
		return generalDiscountAmount;
	}

	public void setGeneralDiscountAmount(BigDecimal generalDiscountAmount) {
		this.generalDiscountAmount = generalDiscountAmount;
	}

	public BigDecimal getGeneralDiscountPercentage() {
		return generalDiscountPercentage;
	}

	public void setGeneralDiscountPercentage(BigDecimal generalDiscountPercentage) {
		this.generalDiscountPercentage = generalDiscountPercentage;
	}

	public List<TestPayment> getTestPaymentList() {
		return testPaymentList;
	}

	public void setTestPaymentList(List<TestPayment> testPaymentList) {
		this.testPaymentList = testPaymentList;
	}

	public EmrVisit getPatientVisit() {
		return patientVisit;
	}

	public void setPatientVisit(EmrVisit patientVisit) {
		this.patientVisit = patientVisit;
	}

	public List<TestDefinition> getTestDefinitionList() {
		return testDefinitionList;
	}

	public void setTestDefinitionList(List<TestDefinition> testDefinitionList) {
		this.testDefinitionList = testDefinitionList;
	}

	public EmrPatientInsuranceInfo getPatientInsuranceInfo() {
		return patientInsuranceInfo;
	}

	public void setPatientInsuranceInfo(EmrPatientInsuranceInfo patientInsuranceInfo) {
		this.patientInsuranceInfo = patientInsuranceInfo;
	}

	public InsProviderPlan getInsProviderPlan() {
		return insProviderPlan;
	}

	public void setInsProviderPlan(InsProviderPlan insProviderPlan) {
		this.insProviderPlan = insProviderPlan;
	}

	public static class TestPayment extends BaseWrapper {

		private static final long serialVersionUID = 1L;

		private BigDecimal amount;
		private BigDecimal changeRate;
		private LkpPaymentMethod lkpPaymentMethod;
		private LkpCurrency lkpPaymentCurrency;

		private BillPatientTransaction previousPayment;

		public TestPayment() {
			super();
		}

		public BillPatientTransaction getPreviousPayment() {
			return previousPayment;
		}

		public void setPreviousPayment(BillPatientTransaction previousPayment) {
			this.previousPayment = previousPayment;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public BigDecimal getChangeRate() {
			return changeRate;
		}

		public void setChangeRate(BigDecimal changeRate) {
			this.changeRate = changeRate;
		}

		public LkpPaymentMethod getLkpPaymentMethod() {
			return lkpPaymentMethod;
		}

		public void setLkpPaymentMethod(LkpPaymentMethod lkpPaymentMethod) {
			this.lkpPaymentMethod = lkpPaymentMethod;
		}

		public LkpCurrency getLkpPaymentCurrency() {
			return lkpPaymentCurrency;
		}

		public void setLkpPaymentCurrency(LkpCurrency lkpPaymentCurrency) {
			this.lkpPaymentCurrency = lkpPaymentCurrency;
		}

		@Override
		public String toString() {
			return "TestPayment [amount=" + amount + ", changeRate=" + changeRate + ", lkpPaymentMethod=" + lkpPaymentMethod
					+ ", lkpPaymentCurrency=" + lkpPaymentCurrency + "]";
		}

	}
}
