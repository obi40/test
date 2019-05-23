package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpContainerType;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenStabilityUnit;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenTemperature;

/**
 * TestSpecimen.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/3/2017
 */
@Entity
@Table(name = "test_specimen")
@Audited
//TODO check each field and its requirements
public class TestSpecimen extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	private String description;

	@Column(name = "stability_digit")
	private Long stabilityDigit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stability_unit_id")
	private LkpSpecimenStabilityUnit stabilityUnit;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "container_type_id")
	private LkpContainerType containerType;

	@NotNull
	@Column(name = "container_count")
	private Integer containerCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specimen_temperature_id")
	private LkpSpecimenTemperature specimenTemperature;

	//TODO added new, check again
	@Column(name = "minimum_volume")
	private String minimumVolume;

	//TODO added new, check again
	@Column(name = "reject_due_to")
	private String rejectDueTo;

	//TODO added new, check again
	@Column(name = "specimen_requirements")
	private String specimenRequirements;

	//TODO added new, check again
	@Column(name = "specimen_volume")
	private String specimenVolume;

	@NotNull
	@Column(name = "is_default")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDefault;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestDefinition testDefinition;

	public TestSpecimen() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getStabilityDigit() {
		return stabilityDigit;
	}

	public void setStabilityDigit(Long stabilityDigit) {
		this.stabilityDigit = stabilityDigit;
	}

	public LkpSpecimenStabilityUnit getStabilityUnit() {
		return stabilityUnit;
	}

	public void setStabilityUnit(LkpSpecimenStabilityUnit stabilityUnit) {
		this.stabilityUnit = stabilityUnit;
	}

	public LkpContainerType getContainerType() {
		return containerType;
	}

	public void setContainerType(LkpContainerType containerType) {
		this.containerType = containerType;
	}

	public Integer getContainerCount() {
		return containerCount;
	}

	public void setContainerCount(Integer containerCount) {
		this.containerCount = containerCount;
	}

	public LkpSpecimenTemperature getSpecimenTemperature() {
		return specimenTemperature;
	}

	public void setSpecimenTemperature(LkpSpecimenTemperature specimenTemperature) {
		this.specimenTemperature = specimenTemperature;
	}

	public String getMinimumVolume() {
		return minimumVolume;
	}

	public void setMinimumVolume(String minimumVolume) {
		this.minimumVolume = minimumVolume;
	}

	public String getRejectDueTo() {
		return rejectDueTo;
	}

	public void setRejectDueTo(String rejectDueTo) {
		this.rejectDueTo = rejectDueTo;
	}

	public String getSpecimenRequirements() {
		return this.specimenRequirements;
	}

	public void setSpecimenRequirements(String specimenRequirements) {
		this.specimenRequirements = specimenRequirements;
	}

	public String getSpecimenVolume() {
		return this.specimenVolume;
	}

	public void setSpecimenVolume(String specimenVolume) {
		this.specimenVolume = specimenVolume;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

}