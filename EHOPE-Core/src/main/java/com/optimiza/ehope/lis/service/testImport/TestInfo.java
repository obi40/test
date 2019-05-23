package com.optimiza.ehope.lis.service.testImport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestInfo {

	private String testId;

	private String aliases;

	@JsonProperty("useful_for")
	private String usefulFor;

	@JsonProperty("testing_algorithm")
	private String testingAlgorithm;

	@JsonProperty("clinical_information")
	private String clinicalInformation;

	@JsonProperty("reference_values")
	private String referenceValues;

	private String interpretation;

	private String cautions;

	@JsonProperty("clinical_reference")
	private String clinicalReference;

	private String loincCode;

	@JsonProperty("special_instructions")
	private String specialInstructions;

	@JsonProperty("supportive_data")
	private String supportiveData;

	@JsonProperty("genetics_test_information")
	private String geneticsTestInformation;

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getAliases() {
		return aliases;
	}

	public void setAliases(String aliases) {
		this.aliases = aliases;
	}

	public String getUsefulFor() {
		return usefulFor;
	}

	public void setUsefulFor(String usefulFor) {
		this.usefulFor = usefulFor;
	}

	public String getTestingAlgorithm() {
		return testingAlgorithm;
	}

	public void setTestingAlgorithm(String testingAlgorithm) {
		this.testingAlgorithm = testingAlgorithm;
	}

	public String getClinicalInformation() {
		return clinicalInformation;
	}

	public void setClinicalInformation(String clinicalInformation) {
		this.clinicalInformation = clinicalInformation;
	}

	public String getReferenceValues() {
		return referenceValues;
	}

	public void setReferenceValues(String referenceValues) {
		this.referenceValues = referenceValues;
	}

	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}

	public String getCautions() {
		return cautions;
	}

	public void setCautions(String cautions) {
		this.cautions = cautions;
	}

	public String getClinicalReference() {
		return clinicalReference;
	}

	public void setClinicalReference(String clinicalReference) {
		this.clinicalReference = clinicalReference;
	}

	public String getLoincCode() {
		return loincCode;
	}

	public void setLoincCode(String loincCode) {
		this.loincCode = loincCode;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	public String getSupportiveData() {
		return supportiveData;
	}

	public void setSupportiveData(String supportiveData) {
		this.supportiveData = supportiveData;
	}

	public String getGeneticsTestInformation() {
		return geneticsTestInformation;
	}

	public void setGeneticsTestInformation(String geneticsTestInformation) {
		this.geneticsTestInformation = geneticsTestInformation;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestInfo [testId=");
		builder.append(testId);
		builder.append(", usefulFor=");
		builder.append(usefulFor);
		builder.append(", testingAlgorithm=");
		builder.append(testingAlgorithm);
		builder.append(", clinicalInformation=");
		builder.append(clinicalInformation);
		builder.append(", referenceValues=");
		builder.append(referenceValues);
		builder.append(", interpretation=");
		builder.append(interpretation);
		builder.append(", cautions=");
		builder.append(cautions);
		builder.append(", clinicalReference=");
		builder.append(clinicalReference);
		builder.append(", specialInstructions=");
		builder.append(specialInstructions);
		builder.append(", supportiveData=");
		builder.append(supportiveData);
		builder.append(", geneticsTestInformation=");
		builder.append(geneticsTestInformation);
		builder.append("]");
		return builder.toString();
	}

}
