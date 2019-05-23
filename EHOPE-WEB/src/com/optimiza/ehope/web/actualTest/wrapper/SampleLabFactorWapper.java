package com.optimiza.ehope.web.actualTest.wrapper;

import java.util.List;

import com.optimiza.ehope.lis.model.LabBranchSeparationFactor;
import com.optimiza.ehope.lis.model.LabSample;

public class SampleLabFactorWapper {

	private LabSample labSample;

	private List<LabBranchSeparationFactor> labBranchSeparationFactorList;

	public LabSample getLabSample() {
		return labSample;
	}

	public void setLabSample(LabSample labSample) {
		this.labSample = labSample;
	}

	public List<LabBranchSeparationFactor> getLabBranchSeparationFactorList() {
		return labBranchSeparationFactorList;
	}

	public void setLabBranchSeparationFactorList(List<LabBranchSeparationFactor> labBranchSeparationFactorList) {
		this.labBranchSeparationFactorList = labBranchSeparationFactorList;
	}

}
