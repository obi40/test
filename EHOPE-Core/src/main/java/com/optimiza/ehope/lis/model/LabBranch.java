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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.core.lkp.model.LkpCity;
import com.optimiza.core.lkp.model.LkpCountry;

/**
 * LabBranch.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/07/2018
 **/
@Entity
@Table(name = "lab_branch")
@Audited
public class LabBranch extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "address")
	private TransField address;

	@NotNull
	@Column(name = "balance")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal balance;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private LkpCity city;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private LkpCountry country;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "name")
	private TransField name;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "code")
	private String code;

	@NotNull
	@Column(name = "phone_no")
	private String phoneNo;

	@NotNull
	@Column(name = "mobile_pattern")
	@Size(min = 1, max = 255)
	private String mobilePattern;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	//TODO:ignore?
	@Column(name = "integration_url")
	private String integrationUrl;

	//TODO:ignore?
	@Column(name = "integration_token")
	private String integrationToken;

	//bi-directional many-to-one association to LabSectionBranch
	@OneToMany(mappedBy = "labBranch", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("labBranch")
	private List<LabSectionBranch> labSectionBranches;

	public LabBranch() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getMobilePattern() {
		return mobilePattern;
	}

	public void setMobilePattern(String mobilePattern) {
		this.mobilePattern = mobilePattern;
	}

	public TransField getAddress() {
		return this.address;
	}

	public void setAddress(TransField address) {
		this.address = address;
	}

	public void setCountry(LkpCountry country) {
		this.country = country;
	}

	public LkpCity getCity() {
		return this.city;
	}

	public void setCity(LkpCity city) {
		this.city = city;
	}

	public LkpCountry getCountry() {
		return this.country;
	}

	public void setCountryId(LkpCountry country) {
		this.country = country;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getIntegrationUrl() {
		return integrationUrl;
	}

	public void setIntegrationUrl(String integrationUrl) {
		this.integrationUrl = integrationUrl;
	}

	public String getIntegrationToken() {
		return integrationToken;
	}

	public void setIntegrationToken(String integrationToken) {
		this.integrationToken = integrationToken;
	}

	public List<LabSectionBranch> getLabSectionBranches() {
		return this.labSectionBranches;
	}

	public void setLabSectionBranches(List<LabSectionBranch> labSectionBranches) {
		this.labSectionBranches = labSectionBranches;
	}

	public LabSectionBranch addLabSectionBranch(LabSectionBranch labSectionBranch) {
		getLabSectionBranches().add(labSectionBranch);
		labSectionBranch.setLabBranch(this);

		return labSectionBranch;
	}

	public LabSectionBranch removeLabSectionBranch(LabSectionBranch labSectionBranch) {
		getLabSectionBranches().remove(labSectionBranch);
		labSectionBranch.setLabBranch(null);

		return labSectionBranch;
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
		LabBranch other = (LabBranch) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabBranch [rid=" + rid + "]";
	}
}