package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * LabTestAnswer.java
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Jul/11/2017
 * 
 */
@Entity
@Table(name = "lab_test_answers")
@Audited
public class LabTestAnswer extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "answer_number")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal answerNumber;

	@Column(name = "answer_date")
	private Date answerDate;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "answer_boolean")
	private Boolean answerBoolean;

	@Column(name = "answer_narrative")
	private String answerNarrative;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "test_actual_id")
	private LabTestActual labTestActual;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "test_question_id")
	private TestQuestion testQuestion;

	public TestQuestion getTestQuestion() {
		return testQuestion;
	}

	public void setTestQuestion(TestQuestion testQuestion) {
		this.testQuestion = testQuestion;
	}

	public LabTestAnswer() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getAnswerNarrative() {
		return answerNarrative;
	}

	public void setAnswerNarrative(String answerNarrative) {
		this.answerNarrative = answerNarrative;
	}

	public BigDecimal getAnswerNumber() {
		return answerNumber;
	}

	public void setAnswerNumber(BigDecimal answerNumber) {
		this.answerNumber = answerNumber;
	}

	public Date getAnswerDate() {
		return answerDate;
	}

	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}

	public Boolean getAnswerBoolean() {
		return answerBoolean;
	}

	public void setAnswerBoolean(Boolean answerBoolean) {
		this.answerBoolean = answerBoolean;
	}

	public LabTestActual getLabTestActual() {
		return labTestActual;
	}

	public void setLabTestActual(LabTestActual labTestActual) {
		this.labTestActual = labTestActual;
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
		LabTestAnswer other = (LabTestAnswer) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabTestAnswer [rid=" + rid + "]";
	}

}