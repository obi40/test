package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;

/**
 * ActualResultNormalRange.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Apr/14/2019
 * 
 */
@Entity
@Table(name = "actual_result_normal_range")
@Audited
public class ActualResultNormalRange extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actual_result_id")
	private LabTestActualResult actualResult;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "normal_range_id")
	private TestNormalRange normalRange;

	public ActualResultNormalRange() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public LabTestActualResult getActualResult() {
		return actualResult;
	}

	public void setActualResult(LabTestActualResult actualResult) {
		this.actualResult = actualResult;
	}

	public TestNormalRange getNormalRange() {
		return normalRange;
	}

	public void setNormalRange(TestNormalRange normalRange) {
		this.normalRange = normalRange;
	}

}