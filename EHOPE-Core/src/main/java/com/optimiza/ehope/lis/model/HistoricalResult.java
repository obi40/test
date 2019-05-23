package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.optimiza.core.base.entity.BaseHistoricalEntity;

/**
 * HistoricalOrder.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/11/2018
 * 
 */
@Entity
@Table(name = "historical_result")
public class HistoricalResult extends BaseHistoricalEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private HistoricalTest test;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "result_code")
	private String resultCode;

	@Size(max = 4000)
	@Column(name = "result_value")
	private String resultValue;

	@Size(max = 4000)
	@Column(name = "normal_range_prefix")
	private String normalRangePrefix;

	@Size(max = 4000)
	@Column(name = "conv_normal_range")
	private String convNormalRange;

	@Size(max = 4000)
	@Column(name = "si_normal_range")
	private String siNormalRange;

	@Size(max = 255)
	@Column(name = "conv_unit")
	private String convUnit;

	@Size(max = 4000)
	@Column(name = "si_unit")
	private String siUnit;

	public HistoricalResult() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public HistoricalTest getTest() {
		return test;
	}

	public void setTest(HistoricalTest test) {
		this.test = test;
	}

	public String getResultCode() {
		return this.resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultValue() {
		return this.resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public String getNormalRangePrefix() {
		return normalRangePrefix;
	}

	public void setNormalRangePrefix(String normalRangePrefix) {
		this.normalRangePrefix = normalRangePrefix;
	}

	public String getConvNormalRange() {
		return convNormalRange;
	}

	public void setConvNormalRange(String convNormalRange) {
		this.convNormalRange = convNormalRange;
	}

	public String getSiNormalRange() {
		return siNormalRange;
	}

	public void setSiNormalRange(String siNormalRange) {
		this.siNormalRange = siNormalRange;
	}

	public String getConvUnit() {
		return convUnit;
	}

	public void setConvUnit(String convUnit) {
		this.convUnit = convUnit;
	}

	public String getSiUnit() {
		return siUnit;
	}

	public void setSiUnit(String siUnit) {
		this.siUnit = siUnit;
	}

}