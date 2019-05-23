package com.optimiza.ehope.lis.wrapper;

import java.math.BigDecimal;
import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.InsProvider;

public class InsParentProviderWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private BigDecimal masterBalance;//used this because we can't use a dummy bill balance inside a parent with no bill balance(spring exception)
	private InsProvider masterProvider;
	private List<InsProvider> providers;

	public InsParentProviderWrapper() {
		super();
	}

	public InsParentProviderWrapper(BigDecimal masterBalance, InsProvider parentProvider, List<InsProvider> providers) {
		super();
		this.masterBalance = masterBalance;
		this.masterProvider = parentProvider;
		this.providers = providers;
	}

	@Override
	public String toString() {
		return "InsParentProviderWrapper [masterProvider rid=" + masterProvider.getRid() + "]";
	}

	public BigDecimal getMasterBalance() {
		return masterBalance;
	}

	public void setMasterBalance(BigDecimal masterBalance) {
		this.masterBalance = masterBalance;
	}

	public InsProvider getMasterProvider() {
		return masterProvider;
	}

	public void setMasterProvider(InsProvider masterProvider) {
		this.masterProvider = masterProvider;
	}

	public List<InsProvider> getProviders() {
		return providers;
	}

	public void setProviders(List<InsProvider> providers) {
		this.providers = providers;
	}

}
