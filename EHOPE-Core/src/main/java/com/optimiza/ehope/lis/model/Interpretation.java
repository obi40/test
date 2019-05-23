package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * Interpretation.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/16/2018
 * 
 */
@Entity
@Table(name = "interpretation")
@Audited
public class Interpretation extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "interpretation_class")
	private String interpretationClass;

	@Column(name = "explanation")
	private String explanation;

	@Size(max = 2)
	@Column(name = "max_concentration_comparator")
	private String maxConcentrationComparator;

	@Column(name = "max_concentration_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal maxConcentrationValue;

	@Size(max = 2)
	@Column(name = "min_concentration_comparator")
	private String minConcentrationComparator;

	@Column(name = "min_concentration_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal minConcentrationValue;

	@NotNull
	@Column(name = "print_order")
	private Integer printOrder;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestDefinition test;

	public Interpretation() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getInterpretationClass() {
		return interpretationClass;
	}

	public void setInterpretationClass(String interpretationClass) {
		this.interpretationClass = interpretationClass;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getMaxConcentrationComparator() {
		return maxConcentrationComparator;
	}

	public void setMaxConcentrationComparator(String maxConcentrationComparator) {
		this.maxConcentrationComparator = maxConcentrationComparator;
	}

	public BigDecimal getMaxConcentrationValue() {
		return maxConcentrationValue;
	}

	public void setMaxConcentrationValue(BigDecimal maxConcentrationValue) {
		this.maxConcentrationValue = maxConcentrationValue;
	}

	public String getMinConcentrationComparator() {
		return minConcentrationComparator;
	}

	public void setMinConcentrationComparator(String minConcentrationComparator) {
		this.minConcentrationComparator = minConcentrationComparator;
	}

	public BigDecimal getMinConcentrationValue() {
		return minConcentrationValue;
	}

	public void setMinConcentrationValue(BigDecimal minConcentrationValue) {
		this.minConcentrationValue = minConcentrationValue;
	}

	public Integer getPrintOrder() {
		return printOrder;
	}

	public void setPrintOrder(Integer printOrder) {
		this.printOrder = printOrder;
	}

	public TestDefinition getTest() {
		return test;
	}

	public void setTest(TestDefinition test) {
		this.test = test;
	}

}