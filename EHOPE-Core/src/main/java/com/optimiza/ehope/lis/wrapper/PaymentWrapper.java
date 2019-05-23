package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.TestDefinition;

public class PaymentWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private TestDefinition testDefinition;

	private Set<PaymentInformation> paymentInformationList = new HashSet<>();
	private BigDecimal paymentInfoTotal = new BigDecimal("0");

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((testDefinition == null) ? 0 : testDefinition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentWrapper other = (PaymentWrapper) obj;
		if (testDefinition == null) {
			if (other.getTestDefinition() != null)
				return false;
		} else if (!testDefinition.equals(other.getTestDefinition()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PaymentWrapper [testDefinition=" + testDefinition + ", paymentInformationList=" + paymentInformationList
				+ ", paymentInfoTotal=" + paymentInfoTotal + "]";
	}

	public BigDecimal getPaymentInfoTotal() {
		return paymentInfoTotal;
	}

	public void setPaymentInfoTotal(BigDecimal paymentInfoTotal) {
		this.paymentInfoTotal = paymentInfoTotal;
	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public Set<PaymentInformation> getPaymentInformationList() {
		return paymentInformationList;
	}

	public void setPaymentInformationList(Set<PaymentInformation> paymentInformationList) {
		this.paymentInformationList = paymentInformationList;
	}

	public void addItemToPaymentInformationList(PaymentInformation paymentInformation) {
		if (this.paymentInformationList == null) {
			this.paymentInformationList = new HashSet<>();
		}
		this.paymentInformationList.add(paymentInformation);
	}

}
