package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.TestDefinition;

public class PaymentInformation extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private TestDefinition testDefinition;
	private List<BillChargeSlip> chargeSlips = new ArrayList<>();
	private BigDecimal chargeSlipsTotal = new BigDecimal("0");

	public PaymentInformation() {

	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public List<BillChargeSlip> getChargeSlips() {
		return chargeSlips;
	}

	public void setChargeSlips(List<BillChargeSlip> chargeSlips) {
		this.chargeSlips = chargeSlips;
	}

	public BigDecimal getChargeSlipsTotal() {
		return chargeSlipsTotal;
	}

	public void setChargeSlipsTotal(BigDecimal chargeSlipsTotal) {
		this.chargeSlipsTotal = chargeSlipsTotal;
	}

}
