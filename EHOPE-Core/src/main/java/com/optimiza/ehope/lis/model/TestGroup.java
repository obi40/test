package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * TestGroup.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/11/2018
 **/
@Entity
@Table(name = "test_group")
@Audited
public class TestGroup extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Column(name = "name")
	private String name;

	@Column(name = "is_profile")
	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isProfile;

	@Column(name = "is_active")
	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@Column(name = "discount_percentage")
	@Digits(integer = 3, fraction = 3)
	@Max(value = 100L)
	@Min(value = 0L)
	private BigDecimal discountPercentage;

	@Column(name = "discount_amount")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal discountAmount;

	@Transient
	@JsonProperty
	private BigDecimal totalPrice;

	@Transient
	@JsonProperty
	private BigDecimal groupPrice;

	@OneToMany(mappedBy = "testGroup", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testGroup", allowSetters = true)
	private Set<TestGroupDefinition> groupDefinitions;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "group", allowSetters = true)
	private Set<TestGroupDetail> groupDetails;

	//bi-directional many-to-one association to LabSample
	@OneToMany(mappedBy = "testGroup", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "testGroup" }, allowSetters = true)
	private Set<EmrVisitGroup> visitGroups;

	@Override
	public Long getRid() {
		return this.rid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestGroup other = (TestGroup) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestGroup [rid=" + rid + "]";
	}

	public Set<EmrVisitGroup> getVisitGroups() {
		return visitGroups;
	}

	public void setVisitGroups(Set<EmrVisitGroup> visitGroups) {
		this.visitGroups = visitGroups;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getGroupPrice() {
		return groupPrice;
	}

	public void setGroupPrice(BigDecimal groupPrice) {
		this.groupPrice = groupPrice;
	}

	public Boolean getIsProfile() {
		return isProfile;
	}

	public Set<TestGroupDetail> getGroupDetails() {
		return groupDetails;
	}

	public void setGroupDetails(Set<TestGroupDetail> groupDetails) {
		this.groupDetails = groupDetails;
	}

	public void setIsProfile(Boolean isProfile) {
		this.isProfile = isProfile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<TestGroupDefinition> getGroupDefinitions() {
		return groupDefinitions;
	}

	public void setGroupDefinitions(Set<TestGroupDefinition> groupDefinitions) {
		this.groupDefinitions = groupDefinitions;
	}

}