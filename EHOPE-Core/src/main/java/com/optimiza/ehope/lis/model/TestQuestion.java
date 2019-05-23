package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.ehope.lis.lkp.model.LkpQuestionType;

/**
 * TestQuestion.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/3/2017
 * 
 */
@Entity
@Table(name = "test_question")
@Audited
public class TestQuestion extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Column(name = "description")
	private String description;

	@NotNull
	@Column(name = "standard_code")
	private String standardCode;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_type_id")
	private LkpQuestionType lkpQuestionType;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestDefinition testDefinition;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "extra_test_id")
	private ExtraTest extraTest;

	//bi-directional many-to-one association to TestQuestionOption
	@OneToMany(mappedBy = "testQuestion", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("testQuestion")
	private Set<TestQuestionOption> testQuestionOptions;

	@Transient
	@JsonDeserialize
	private List<TestQuestionOption> testQuestionOptionList;

	public TestQuestion() {
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

	public String getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public LkpQuestionType getLkpQuestionType() {
		return lkpQuestionType;
	}

	public void setLkpQuestionType(LkpQuestionType lkpQuestionType) {
		this.lkpQuestionType = lkpQuestionType;
	}

	public TestDefinition getTestDefinition() {
		return this.testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public ExtraTest getExtraTest() {
		return extraTest;
	}

	public void setExtraTest(ExtraTest extraTest) {
		this.extraTest = extraTest;
	}

	public Set<TestQuestionOption> getTestQuestionOptions() {
		return this.testQuestionOptions;
	}

	public void setTestQuestionOptions(Set<TestQuestionOption> testQuestionOptions) {
		this.testQuestionOptions = testQuestionOptions;
	}

	public List<TestQuestionOption> getTestQuestionOptionList() {
		return testQuestionOptionList;
	}

	public void setTestQuestionOptionList(List<TestQuestionOption> testQuestionOptionList) {
		this.testQuestionOptionList = testQuestionOptionList;
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
		TestQuestion other = (TestQuestion) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestQuestion [rid=" + rid + "]";
	}

}