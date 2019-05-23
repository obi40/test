package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.ResultValueType;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.wrapper.MachineResult;

@Service("MachineIntergrationService")
public class MachineIntergrationService {

	@Autowired
	private LabTestActualResultService labTestActualResultService;

	@Autowired
	private LabSampleService sampleService;

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_TEST_ACTUAL_RESULT + "')")
	public List<MachineResult> submitActualTestResults(List<MachineResult> machineResults) {
		List<LabTestActualResult> actualsResults = new ArrayList<LabTestActualResult>();
		Long visitRid = null;
		for (MachineResult machineResult : machineResults) {
			String resultCode = machineResult.getTestCode();
			String barcode = machineResult.getBarcode();
			String result = machineResult.getResult();

			if (visitRid == null) {
				LabSample sample = sampleService.findOne(
						Arrays.asList(new SearchCriterion("barcode", barcode, FilterOperator.eq)),
						LabSample.class, "emrVisit");
				visitRid = sample.getEmrVisit().getRid();
			}

			LabTestActualResult labTestActualResult = labTestActualResultService.findByResultCodeAndBarcode(resultCode, barcode);
			if (labTestActualResult == null) {
				machineResult.setIsAccepted(Boolean.FALSE);
				continue;
			}
			machineResult.setIsAccepted(Boolean.TRUE);

			TestResult testResult = labTestActualResult.getLabResult();
			ResultValueType resultValueType = ResultValueType.valueOf(testResult.getResultValueType().getCode());
			switch (resultValueType) {
				case NAR:
					labTestActualResult.setNarrativeText(result);
					break;
				case QN:
					if (testResult.getIsDifferential()) {
						labTestActualResult.setPercentage(Integer.parseInt(machineResult.getResult()));
					} else {
						labTestActualResult.setPrimaryResultValue(result);
					}
					break;
				case QN_SC:
					labTestActualResult.setPrimaryResultValue(result);
					break;
			}

			actualsResults.add(labTestActualResult);
		}
		labTestActualResultService.editTestActualResults(actualsResults, visitRid, true);
		return machineResults;
	}

}
