package com.optimiza.ehope.lis.wrapper;

import java.util.List;

import com.optimiza.ehope.lis.lkp.model.LkpContainerType;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.TestDefinition;

public class WorksheetWrapper {

	private String barcode;
	private String section;
	private EmrVisit emrVisit;
	private LkpContainerType lkpContainerType;
	private List<LabTestActual> labTestActual;
	private String testDestination;

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

	//	private List<TestDisclaimer> neonatalTestDisclaimerList;
	//
	//	public List<TestDisclaimer> getTestDisclaimerList() {
	//		return neonatalTestDisclaimerList;
	//	}
	//
	//	public void setTestDisclaimerList(List<TestDisclaimer> neonatalTestDisclaimerList) {
	//		this.neonatalTestDisclaimerList = neonatalTestDisclaimerList;
	//	}

	public String getTestDestination() {
		return testDestination;
	}

	public void setTestDestination(String testDestination) {
		this.testDestination = testDestination;
	}

	public List<LabTestActual> getLabTestActual() {
		return labTestActual;
	}

	public void setLabTestActual(List<LabTestActual> labTestActual) {
		this.labTestActual = labTestActual;
	}

	public LkpContainerType getLkpContainerType() {
		return lkpContainerType;
	}

	public void setLkpContainerType(LkpContainerType lkpContainerType) {
		this.lkpContainerType = lkpContainerType;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

}
