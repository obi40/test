package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * LoincAttributes.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Sep/25/2017
 * 
 */
@Entity
@Table(name = "loinc_attributes")
@Audited
public class LoincAttributes extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "ask_at_order_entry")
	private String askAtOrderEntry;

	@Column(name = "associated_observations")
	private String associatedObservations;

	@Column(name = "cdisc_common_tests")
	private String cdiscCommonTests;

	@Column(name = "change_reason_public")
	private String changeReasonPublic;

	@Column(name = "change_type")
	private String changeType;

	@Column(name = "class")
	private String class_;

	@Column(name = "class_type")
	private String classType;

	@Column(name = "common_order_rank")
	private String commonOrderRank;

	@Column(name = "common_si_test_rank")
	private String commonSiTestRank;

	@Column(name = "common_test_rank")
	private String commonTestRank;

	private String component;

	@Column(name = "consumer_name")
	private String consumerName;

	@Column(name = "definition_description")
	private String definitionDescription;

	@Column(name = "document_section")
	private String documentSection;

	@Column(name = "example_answers")
	private String exampleAnswers;

	@Column(name = "example_si_ucum_units")
	private String exampleSiUcumUnits;

	@Column(name = "example_ucum_units")
	private String exampleUcumUnits;

	@Column(name = "example_units")
	private String exampleUnits;

	@Column(name = "external_copyright_link")
	private String externalCopyrightLink;

	@Column(name = "external_copyright_notice")
	private String externalCopyrightNotice;

	private String formula;

	@Column(name = "hl7_attachment_structure")
	private String hl7AttachmentStructure;

	@Column(name = "hl7_field_subfield_id")
	private String hl7FieldSubfieldId;

	@Column(name = "loinc_num")
	private String loincNum;

	@Column(name = "long_common_name")
	private String longCommonName;

	@Column(name = "method_type")
	private String methodType;

	@Column(name = "order_observation")
	private String orderObservation;

	@Column(name = "panel_type")
	private String panelType;

	private String property;

	@Column(name = "related_names_2")
	private String relatedNames2;

	@Column(name = "scale_type")
	private String scaleType;

	@Column(name = "short_name")
	private String shortName;

	private String species;

	private String status;

	@Column(name = "status_reason")
	private String statusReason;

	@Column(name = "status_text")
	private String statusText;

	@Column(name = "submitted_units")
	private String submittedUnits;

	@Column(name = "survey_question_source")
	private String surveyQuestionSource;

	@Column(name = "survey_question_text")
	private String surveyQuestionText;

	private String system;

	@Column(name = "time_aspect")
	private String timeAspect;

	@Column(name = "units_and_range")
	private String unitsAndRange;

	@Column(name = "units_required")
	private String unitsRequired;

	@Column(name = "valid_hl7_attachment_request")
	private String validHl7AttachmentRequest;

	@Column(name = "version_first_released")
	private String versionFirstReleased;

	@Column(name = "version_last_changed")
	private String versionLastChanged;

	public LoincAttributes() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getAskAtOrderEntry() {
		return this.askAtOrderEntry;
	}

	public void setAskAtOrderEntry(String askAtOrderEntry) {
		this.askAtOrderEntry = askAtOrderEntry;
	}

	public String getAssociatedObservations() {
		return this.associatedObservations;
	}

	public void setAssociatedObservations(String associatedObservations) {
		this.associatedObservations = associatedObservations;
	}

	public String getCdiscCommonTests() {
		return this.cdiscCommonTests;
	}

	public void setCdiscCommonTests(String cdiscCommonTests) {
		this.cdiscCommonTests = cdiscCommonTests;
	}

	public String getChangeReasonPublic() {
		return this.changeReasonPublic;
	}

	public void setChangeReasonPublic(String changeReasonPublic) {
		this.changeReasonPublic = changeReasonPublic;
	}

	public String getChangeType() {
		return this.changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public String getClass_() {
		return this.class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public String getClassType() {
		return this.classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getCommonOrderRank() {
		return this.commonOrderRank;
	}

	public void setCommonOrderRank(String commonOrderRank) {
		this.commonOrderRank = commonOrderRank;
	}

	public String getCommonSiTestRank() {
		return this.commonSiTestRank;
	}

	public void setCommonSiTestRank(String commonSiTestRank) {
		this.commonSiTestRank = commonSiTestRank;
	}

	public String getCommonTestRank() {
		return this.commonTestRank;
	}

	public void setCommonTestRank(String commonTestRank) {
		this.commonTestRank = commonTestRank;
	}

	public String getComponent() {
		return this.component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getConsumerName() {
		return this.consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getDefinitionDescription() {
		return this.definitionDescription;
	}

	public void setDefinitionDescription(String definitionDescription) {
		this.definitionDescription = definitionDescription;
	}

	public String getDocumentSection() {
		return this.documentSection;
	}

	public void setDocumentSection(String documentSection) {
		this.documentSection = documentSection;
	}

	public String getExampleAnswers() {
		return this.exampleAnswers;
	}

	public void setExampleAnswers(String exampleAnswers) {
		this.exampleAnswers = exampleAnswers;
	}

	public String getExampleSiUcumUnits() {
		return this.exampleSiUcumUnits;
	}

	public void setExampleSiUcumUnits(String exampleSiUcumUnits) {
		this.exampleSiUcumUnits = exampleSiUcumUnits;
	}

	public String getExampleUcumUnits() {
		return this.exampleUcumUnits;
	}

	public void setExampleUcumUnits(String exampleUcumUnits) {
		this.exampleUcumUnits = exampleUcumUnits;
	}

	public String getExampleUnits() {
		return this.exampleUnits;
	}

	public void setExampleUnits(String exampleUnits) {
		this.exampleUnits = exampleUnits;
	}

	public String getExternalCopyrightLink() {
		return this.externalCopyrightLink;
	}

	public void setExternalCopyrightLink(String externalCopyrightLink) {
		this.externalCopyrightLink = externalCopyrightLink;
	}

	public String getExternalCopyrightNotice() {
		return this.externalCopyrightNotice;
	}

	public void setExternalCopyrightNotice(String externalCopyrightNotice) {
		this.externalCopyrightNotice = externalCopyrightNotice;
	}

	public String getFormula() {
		return this.formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getHl7AttachmentStructure() {
		return this.hl7AttachmentStructure;
	}

	public void setHl7AttachmentStructure(String hl7AttachmentStructure) {
		this.hl7AttachmentStructure = hl7AttachmentStructure;
	}

	public String getHl7FieldSubfieldId() {
		return this.hl7FieldSubfieldId;
	}

	public void setHl7FieldSubfieldId(String hl7FieldSubfieldId) {
		this.hl7FieldSubfieldId = hl7FieldSubfieldId;
	}

	public String getLoincNum() {
		return this.loincNum;
	}

	public void setLoincNum(String loincNum) {
		this.loincNum = loincNum;
	}

	public String getLongCommonName() {
		return this.longCommonName;
	}

	public void setLongCommonName(String longCommonName) {
		this.longCommonName = longCommonName;
	}

	public String getMethodType() {
		return this.methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getOrderObservation() {
		return this.orderObservation;
	}

	public void setOrderObservation(String orderObservation) {
		this.orderObservation = orderObservation;
	}

	public String getPanelType() {
		return this.panelType;
	}

	public void setPanelType(String panelType) {
		this.panelType = panelType;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getRelatedNames2() {
		return this.relatedNames2;
	}

	public void setRelatedNames2(String relatedNames2) {
		this.relatedNames2 = relatedNames2;
	}

	public String getScaleType() {
		return this.scaleType;
	}

	public void setScaleType(String scaleType) {
		this.scaleType = scaleType;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getSpecies() {
		return this.species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusReason() {
		return this.statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public String getStatusText() {
		return this.statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getSubmittedUnits() {
		return this.submittedUnits;
	}

	public void setSubmittedUnits(String submittedUnits) {
		this.submittedUnits = submittedUnits;
	}

	public String getSurveyQuestionSource() {
		return this.surveyQuestionSource;
	}

	public void setSurveyQuestionSource(String surveyQuestionSource) {
		this.surveyQuestionSource = surveyQuestionSource;
	}

	public String getSurveyQuestionText() {
		return this.surveyQuestionText;
	}

	public void setSurveyQuestionText(String surveyQuestionText) {
		this.surveyQuestionText = surveyQuestionText;
	}

	public String getSystem() {
		return this.system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getTimeAspect() {
		return this.timeAspect;
	}

	public void setTimeAspect(String timeAspect) {
		this.timeAspect = timeAspect;
	}

	public String getUnitsAndRange() {
		return this.unitsAndRange;
	}

	public void setUnitsAndRange(String unitsAndRange) {
		this.unitsAndRange = unitsAndRange;
	}

	public String getUnitsRequired() {
		return this.unitsRequired;
	}

	public void setUnitsRequired(String unitsRequired) {
		this.unitsRequired = unitsRequired;
	}

	public String getValidHl7AttachmentRequest() {
		return this.validHl7AttachmentRequest;
	}

	public void setValidHl7AttachmentRequest(String validHl7AttachmentRequest) {
		this.validHl7AttachmentRequest = validHl7AttachmentRequest;
	}

	public String getVersionFirstReleased() {
		return this.versionFirstReleased;
	}

	public void setVersionFirstReleased(String versionFirstReleased) {
		this.versionFirstReleased = versionFirstReleased;
	}

	public String getVersionLastChanged() {
		return this.versionLastChanged;
	}

	public void setVersionLastChanged(String versionLastChanged) {
		this.versionLastChanged = versionLastChanged;
	}

}