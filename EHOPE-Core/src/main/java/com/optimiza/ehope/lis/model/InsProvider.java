package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.core.lkp.model.LkpCountry;
import com.optimiza.ehope.lis.lkp.model.LkpInsuranceType;

/**
 * InsProvider.java
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Aug/7/2017
 */
@Entity
@Table(name = "ins_provider")
@Audited
public class InsProvider extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "address")
	private TransField address;

	@Size(min = 0, max = 50)
	@NotNull
	@Column(name = "code")
	private String code;

	@Size(min = 0, max = 4000)
	@Column(name = "remarks")
	private String remarks;

	@Column(name = "balance")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal balance;

	@Size(min = 0, max = 4000)
	@Column(name = "contact_information")
	private String contactInformation;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private LkpCountry lkpCountry;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@Column(name = "is_net_amount")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isNetAmount;

	@NotNull
	@Column(name = "is_simple")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSimple;

	@NotNull
	@Column(name = "is_auto_approve")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isAutoApprove;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "name")
	private TransField name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "network_id")
	private InsNetwork insNetwork;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insurance_type_id")
	private LkpInsuranceType insuranceType;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_list_id")
	private BillPriceList priceList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_provider_id")
	private InsProvider parentProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ins_tenant_id")
	private SecTenant insuranceTenant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ins_branch_id")
	private LabBranch insuranceBranch;

	@Column(name = "coverage_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	@NotNull
	private BigDecimal coveragePercentage;

	@Column(name = "discount")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	@NotNull
	private BigDecimal discount;

	@OneToMany(mappedBy = "insProvider", fetch = FetchType.LAZY)
	private List<EmrPatientInsuranceInfo> emrPatientInsuranceInfos;

	@OneToMany(mappedBy = "insProvider", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "insProvider", allowSetters = true)
	private List<InsProviderPlan> providerPlans;

	public InsProvider() {
	}

	public Boolean getIsNetAmount() {
		return isNetAmount;
	}

	public void setIsNetAmount(Boolean isNetAmount) {
		this.isNetAmount = isNetAmount;
	}

	public String getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	public SecTenant getInsuranceTenant() {
		return insuranceTenant;
	}

	public void setInsuranceTenant(SecTenant insuranceTenant) {
		this.insuranceTenant = insuranceTenant;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public LabBranch getInsuranceBranch() {
		return insuranceBranch;
	}

	public void setInsuranceBranch(LabBranch insuranceBranch) {
		this.insuranceBranch = insuranceBranch;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public List<InsProviderPlan> getProviderPlans() {
		return providerPlans;
	}

	public void setProviderPlans(List<InsProviderPlan> providerPlans) {
		this.providerPlans = providerPlans;
	}

	public InsProvider getParentProvider() {
		return parentProvider;
	}

	public void setParentProvider(InsProvider parentProvider) {
		this.parentProvider = parentProvider;
	}

	public BillPriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(BillPriceList priceList) {
		this.priceList = priceList;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public LkpInsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(LkpInsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public Boolean getIsSimple() {
		return isSimple;
	}

	public void setIsSimple(Boolean isSimple) {
		this.isSimple = isSimple;
	}

	public BigDecimal getCoveragePercentage() {
		return coveragePercentage;
	}

	public void setCoveragePercentage(BigDecimal coveragePercentage) {
		this.coveragePercentage = coveragePercentage;
	}

	public TransField getAddress() {
		return this.address;
	}

	public void setAddress(TransField address) {
		this.address = address;
	}

	public LkpCountry getLkpCountry() {
		return lkpCountry;
	}

	public void setLkpCountry(LkpCountry lkpCountry) {
		this.lkpCountry = lkpCountry;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<EmrPatientInsuranceInfo> getEmrPatientInsuranceInfos() {
		return this.emrPatientInsuranceInfos;
	}

	public void setEmrPatientInsuranceInfos(List<EmrPatientInsuranceInfo> emrPatientInsuranceInfos) {
		this.emrPatientInsuranceInfos = emrPatientInsuranceInfos;
	}

	public InsNetwork getInsNetwork() {
		return insNetwork;
	}

	public void setInsNetwork(InsNetwork insNetwork) {
		this.insNetwork = insNetwork;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
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
		InsProvider other = (InsProvider) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InsProvider [rid=" + rid + "]";
	}

	public Boolean getIsAutoApprove() {
		return isAutoApprove;
	}

	public void setIsAutoApprove(Boolean isAutoApprove) {
		this.isAutoApprove = isAutoApprove;
	}

}