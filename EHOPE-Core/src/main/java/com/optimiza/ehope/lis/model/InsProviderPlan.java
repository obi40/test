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
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * InsProviderPlan.java
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Aug/7/2017
 */
@Entity
@Table(name = "ins_provider_plan")
@Audited
public class InsProviderPlan extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "code")
	private String code;

	@Size(min = 0, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "description")
	private TransField description;

	@NotNull
	@Convert(converter = TransFieldAttConverter.class)
	@Size(min = 1, max = 4000)
	@Column(name = "name")
	private TransField name;

	@Column(name = "coverage_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	@NotNull
	private BigDecimal coveragePercentage;

	@NotNull
	@Column(name = "is_fixed")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isFixed;

	@NotNull
	@Column(name = "is_simple")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSimple;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id")
	private InsProvider insProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_list_id")
	private BillPriceList billPriceList;

	//bi-directional many-to-one association to InsCoverageDetail
	@OneToMany(mappedBy = "insProviderPlan", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("insProviderPlan")
	private List<InsCoverageDetail> insCoverageDetailList;

	//bi-directional many-to-one association to EmrPatientInsuranceInfo
	@OneToMany(mappedBy = "insProviderPlan", fetch = FetchType.LAZY)
	private List<EmrPatientInsuranceInfo> emrPatientInsuranceInfoList;

	public InsProviderPlan() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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

	public Boolean getIsFixed() {
		return this.isFixed;
	}

	public void setIsFixed(Boolean isFixed) {
		this.isFixed = isFixed;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TransField getDescription() {
		return description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public InsProvider getInsProvider() {
		return insProvider;
	}

	public void setInsProvider(InsProvider insProvider) {
		this.insProvider = insProvider;
	}

	public BillPriceList getBillPriceList() {
		return billPriceList;
	}

	public void setBillPriceList(BillPriceList billPriceList) {
		this.billPriceList = billPriceList;
	}

	public List<InsCoverageDetail> getInsCoverageDetailList() {
		return insCoverageDetailList;
	}

	public void setInsCoverageDetailList(List<InsCoverageDetail> insCoverageDetailList) {
		this.insCoverageDetailList = insCoverageDetailList;
	}

	public List<EmrPatientInsuranceInfo> getEmrPatientInsuranceInfoList() {
		return emrPatientInsuranceInfoList;
	}

	public void setEmrPatientInsuranceInfoList(List<EmrPatientInsuranceInfo> emrPatientInsuranceInfoList) {
		this.emrPatientInsuranceInfoList = emrPatientInsuranceInfoList;
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
		InsProviderPlan other = (InsProviderPlan) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InsProviderPlan [rid=" + rid + "]";
	}

}