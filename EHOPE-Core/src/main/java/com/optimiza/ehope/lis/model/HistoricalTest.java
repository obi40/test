package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseHistoricalEntity;

/**
 * HistoricalTest.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/14/2018
 * 
 */
@Entity
@Table(name = "historical_test")
public class HistoricalTest extends BaseHistoricalEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private HistoricalOrder order;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "test_code")
	private String testCode;

	@Size(max = 4000)
	@Column(name = "comments")
	private String comments;

	//bi-directional many-to-one association to HistoricalResult
	@OneToMany(mappedBy = "test", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "test" }, allowSetters = true)
	private Set<HistoricalResult> results;

	public HistoricalTest() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public HistoricalOrder getOrder() {
		return order;
	}

	public void setOrder(HistoricalOrder order) {
		this.order = order;
	}

	public String getTestCode() {
		return this.testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<HistoricalResult> getResults() {
		return results;
	}

	public void setResults(Set<HistoricalResult> results) {
		this.results = results;
	}

}