package com.optimiza.ehope.lis.lkp.helper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabTestActual;

public enum OperationStatus {

	REQUESTED("REQUESTED", 1),
	VALIDATED("VALIDATED", 2),
	COLLECTED("COLLECTED", 3),
	IN_PROGRESS("IN_PROGRESS", 4),
	RESULTS_ENTERED("RESULTS_ENTERED", 5),
	FINALIZED("FINALIZED", 6),
	// If we have any of the above statuses then it will be used as the getSmallestStatus(...) otherwise we will start getting from the below statuses
	CANCELLED("CANCELLED", 997),
	CLOSED("CLOSED", 998),
	ABORTED("ABORTED", 999);

	private OperationStatus(String value) {
		this.value = value;
	}

	private OperationStatus(String value, Integer order) {
		this.value = value;
		this.order = order;
	}

	private String value;
	private Integer order;

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get smallest status using the order
	 * 
	 * @param operationStatusList
	 * @return OperationStatus
	 */
	public static OperationStatus getSmallestStatus(List<OperationStatus> operationStatusList) {
		if (CollectionUtil.isCollectionEmpty(operationStatusList)) {
			return null;
		}
		OperationStatus result = operationStatusList.get(0);
		for (int i = 1; i < operationStatusList.size(); i++) {
			if (operationStatusList.get(i).getOrder() < result.getOrder()) {
				result = operationStatusList.get(i);
			}
		}
		return result;
	}

	/**
	 * Get the smallest status from visit, sample, test. By excluding the same object from the list and adding it's new status
	 * 
	 * @param set
	 * @param excludedRid
	 * @param newOperationStatus
	 * @return smallest OperationStatus
	 */
	public static OperationStatus getSmallestVisitSampleTestStatus(Set<?> set, Long excludedRid,
			OperationStatus newOperationStatus) {

		Class<?> clazz = ReflectionUtil.unproxy(set.iterator().next()).getClass();
		List<OperationStatus> testsOperationStatusList = null;
		if (clazz == EmrVisit.class) {
			testsOperationStatusList = set	.stream().map(EmrVisit.class::cast).filter(v -> !v.getRid().equals(excludedRid))
											.map(v -> OperationStatus.valueOf(v.getLkpOperationStatus().getCode()))
											.collect(Collectors.toList());
		} else if (clazz == LabSample.class) {
			testsOperationStatusList = set	.stream().map(LabSample.class::cast).filter(s -> !s.getRid().equals(excludedRid))
											.map(s -> OperationStatus.valueOf(s.getLkpOperationStatus().getCode()))
											.collect(Collectors.toList());
		} else {
			testsOperationStatusList = set	.stream().map(LabTestActual.class::cast).filter(t -> !t.getRid().equals(excludedRid))
											.map(t -> OperationStatus.valueOf(t.getLkpOperationStatus().getCode()))
											.collect(Collectors.toList());
		}
		testsOperationStatusList.add(newOperationStatus);
		return OperationStatus.getSmallestStatus(testsOperationStatusList);
	}

}
