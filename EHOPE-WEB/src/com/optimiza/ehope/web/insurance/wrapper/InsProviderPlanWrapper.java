package com.optimiza.ehope.web.insurance.wrapper;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.InsProviderPlan;

public class InsProviderPlanWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private EmrPatientInsuranceInfo patientInsuranceInfo;
	private InsProvider insProvider;
	private InsProviderPlan insProviderPlan;

	public InsProviderPlanWrapper() {
		super();
	}

	public InsProviderPlanWrapper(EmrPatientInsuranceInfo patientInsuranceInfo, InsProvider insProvider, InsProviderPlan insProviderPlan) {
		super();
		this.patientInsuranceInfo = patientInsuranceInfo;
		this.insProvider = insProvider;
		this.insProviderPlan = insProviderPlan;
	}

	public EmrPatientInsuranceInfo getPatientInsuranceInfo() {
		return patientInsuranceInfo;
	}

	public void setPatientInsuranceInfo(EmrPatientInsuranceInfo patientInsuranceInfo) {
		this.patientInsuranceInfo = patientInsuranceInfo;
	}

	public InsProvider getInsProvider() {
		return insProvider;
	}

	public void setInsProvider(InsProvider insProvider) {
		this.insProvider = insProvider;
	}

	public InsProviderPlan getInsProviderPlan() {
		return insProviderPlan;
	}

	public void setInsProviderPlan(InsProviderPlan insProviderPlan) {
		this.insProviderPlan = insProviderPlan;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((insProvider == null) ? 0 : insProvider.hashCode());
		result = prime * result + ((insProviderPlan == null) ? 0 : insProviderPlan.hashCode());
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
		InsProviderPlanWrapper other = (InsProviderPlanWrapper) obj;
		if (insProvider == null) {
			if (other.insProvider != null)
				return false;
		} else if (!insProvider.equals(other.insProvider))
			return false;
		if (insProviderPlan == null) {
			if (other.insProviderPlan != null)
				return false;
		} else if (!insProviderPlan.equals(other.insProviderPlan))
			return false;
		return true;
	}

}
