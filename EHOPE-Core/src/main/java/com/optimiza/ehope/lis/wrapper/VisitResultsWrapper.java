package com.optimiza.ehope.lis.wrapper;

import java.util.List;
import java.util.Set;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.TestDefinition;

public class VisitResultsWrapper implements Comparable<VisitResultsWrapper> {

	private SecUser user;
	private EmrVisit emrVisit;
	private List<LabSample> labSample;
	private Set<LabTestActual> labTestActual;
	private Set<LabTestActual> labTestActualPrevious;
	private String reportName;
	private String reportingType;
	private SecTenant tenant;
	private TestDefinition testDefinition;
	private Integer neonatalResultsCount;

	public Integer getNeonatalResultsCount() {
		return neonatalResultsCount;
	}

	public void setNeonatalResultsCount(Integer neonatalResultsCount) {
		this.neonatalResultsCount = neonatalResultsCount;
	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	////////DELETE/////////////
	private String disclaimers;

	public String getDisclaimers() {
		return disclaimers;
	}

	public void setDisclaimers(String disclaimers) {
		this.disclaimers = disclaimers;
	}
	////////DELETE/////////////

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	private WorksheetWrapper worksheetWrapper;

	//////STOOL STUFF//////
	private Set<LabTestActual> stoolMainTest;
	private Set<LabTestActual> stoolOtherTests;
	//////STOOL STUFF//////

	//////CULTURE STUFF//////
	private Set<LabTestActualResult> otherCultureTestActualSet;
	//////CULTURE STUFF//////

	//////CBC STUFF/////////
	private Set<LabTestActual> cbcMainTest;
	private Set<LabTestActual> cbcOtherTests;
	//////CBC STUFF////////

	public SecUser getUser() {
		return user;
	}

	public Set<LabTestActual> getCbcMainTest() {
		return cbcMainTest;
	}

	public void setCbcMainTest(Set<LabTestActual> cbcMainTest) {
		this.cbcMainTest = cbcMainTest;
	}

	public Set<LabTestActual> getCbcOtherTests() {
		return cbcOtherTests;
	}

	public void setCbcOtherTests(Set<LabTestActual> cbcOtherTests) {
		this.cbcOtherTests = cbcOtherTests;
	}

	public void setUser(SecUser user) {
		this.user = user;
	}

	public Set<LabTestActual> getLabTestActualPrevious() {
		return labTestActualPrevious;
	}

	public void setLabTestActualPrevious(Set<LabTestActual> labTestActualPrevious) {
		this.labTestActualPrevious = labTestActualPrevious;
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public List<LabSample> getLabSample() {
		return labSample;
	}

	public void setLabSample(List<LabSample> labSample) {
		this.labSample = labSample;
	}

	public Set<LabTestActual> getLabTestActual() {
		return labTestActual;
	}

	public void setLabTestActual(Set<LabTestActual> labTestActual) {
		this.labTestActual = labTestActual;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	//////STOOL STUFF//////
	public Set<LabTestActual> getStoolOtherTests() {
		return stoolOtherTests;
	}

	public Set<LabTestActual> getStoolMainTest() {
		return stoolMainTest;
	}

	public void setStoolMainTest(Set<LabTestActual> stoolMainTest) {
		this.stoolMainTest = stoolMainTest;
	}

	public void setStoolOtherTests(Set<LabTestActual> stoolOtherTests) {
		this.stoolOtherTests = stoolOtherTests;
	}
	//////STOOL STUFF//////

	//////CULTURE STUFF//////
	public Set<LabTestActualResult> getOtherCultureTestActualSet() {
		return otherCultureTestActualSet;
	}

	public void setOtherCultureTestActualSet(Set<LabTestActualResult> otherCultureTestActualSet) {
		this.otherCultureTestActualSet = otherCultureTestActualSet;
	}
	//////CULTURE STUFF//////

	@Override
	public int compareTo(VisitResultsWrapper o) {
		return labTestActual.iterator().next().compareTo(o.getLabTestActual().iterator().next());
	}

	public String getReportingType() {
		return reportingType;
	}

	public void setReportingType(String reportingType) {
		this.reportingType = reportingType;
	}

	public WorksheetWrapper getWorksheetWrapper() {
		return worksheetWrapper;
	}

	public void setWorksheetWrapper(WorksheetWrapper worksheetWrapper) {
		this.worksheetWrapper = worksheetWrapper;
	}

}
