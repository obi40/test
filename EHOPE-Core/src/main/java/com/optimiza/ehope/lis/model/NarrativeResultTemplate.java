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

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * NarrativeResultTemplate.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jun/26/2018
 **/
@Entity
@Table(name = "narrative_result_template")
@Audited
public class NarrativeResultTemplate extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_id")
	private TestResult result;

	@NotNull
	private String text;

	public NarrativeResultTemplate() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TestResult getResult() {
		return this.result;
	}

	public void setResult(TestResult result) {
		this.result = result;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}