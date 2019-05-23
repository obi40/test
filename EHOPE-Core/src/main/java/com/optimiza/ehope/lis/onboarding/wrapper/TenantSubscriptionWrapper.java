package com.optimiza.ehope.lis.onboarding.wrapper;

import java.util.List;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.ehope.lis.onboarding.model.BrdPlan;
import com.optimiza.ehope.lis.onboarding.model.BrdPlanField;

public class TenantSubscriptionWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private SecTenant tenant;
	private BrdPlan plan;
	private List<BrdPlanField> planFieldList;
	private String agreement;//json structure,exception when trying to serialize it using jackson, they use gson.
	private String approvalUrl;
	private String token;
	private Long price;
	private List<ComTenantLanguage> tenantLangauges;

	public List<ComTenantLanguage> getTenantLangauges() {
		return tenantLangauges;
	}

	public void setTenantLangauges(List<ComTenantLanguage> tenantLangauges) {
		this.tenantLangauges = tenantLangauges;
	}

	public String getAgreement() {
		return agreement;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}

	public String getApprovalUrl() {
		return approvalUrl;
	}

	public void setApprovalUrl(String approvalUrl) {
		this.approvalUrl = approvalUrl;
	}

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	public BrdPlan getPlan() {
		return plan;
	}

	public void setPlan(BrdPlan plan) {
		this.plan = plan;
	}

	public List<BrdPlanField> getPlanFieldList() {
		return planFieldList;
	}

	public void setPlanFieldList(List<BrdPlanField> planFieldList) {
		this.planFieldList = planFieldList;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

}
