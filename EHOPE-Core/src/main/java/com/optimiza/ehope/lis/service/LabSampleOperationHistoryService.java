package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabSampleOperationHistory;
import com.optimiza.ehope.lis.repo.LabSampleOperationHistoryRepo;

/**
 * LabSampleOperationHistoryService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/07/2018
 **/

@Service("LabSampleOperationHistoryService")
public class LabSampleOperationHistoryService extends GenericService<LabSampleOperationHistory, LabSampleOperationHistoryRepo> {

	@Autowired
	private LabSampleOperationHistoryRepo labSampleOperationHistoryRepo;

	@Autowired
	private LkpService lkpService;
	@Autowired
	private SecUserService userService;
	@Autowired
	private LabSampleService sampleService;

	public void deleteAllBySample(LabSample sample) {
		getRepository().deleteAllByLabSample(sample);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORDER_MANAGEMENT + "')")
	public List<LabSampleOperationHistory> findSampleOperationHistoryList(List<SearchCriterion> filters, Sort sort,
			String... joins) {
		List<LabSampleOperationHistory> historyList = getRepository().find(filters, LabSampleOperationHistory.class, sort, joins);
		for (LabSampleOperationHistory history : historyList) {
			SecUser user = userService.findById(history.getOperationBy());
			history.setOperationByUser(user);
		}
		return historyList;
	}

	/**
	 * Create a new LabSampleOperationHistory depending wither it has previous records or not
	 * 
	 * @param labSampleList
	 * @param comment : nullable
	 * @return LabSampleOperationHistory
	 */
	public List<LabSampleOperationHistory> createSampleOperationHistory(List<Long> labSamplesRid, String comment) {
		if (CollectionUtil.isCollectionEmpty(labSamplesRid)) {
			return new ArrayList<>();
		}
		Date currentDate = new Date();
		Sort desc = new Sort(new Order(Direction.DESC, "rid"));

		List<LabSampleOperationHistory> newSampleOperationHistoryList = new ArrayList<>();
		//fetching the required data to create sample operation histories
		List<SearchCriterion> filters = labSamplesRid.stream().map(
				r -> new SearchCriterion("rid", r, FilterOperator.eq, JunctionOperator.Or)).collect(Collectors.toList());
		List<LabSample> fetchedSamples = sampleService.find(filters, LabSample.class, "lkpOperationStatus");
		for (LabSample sample : fetchedSamples) {

			LkpOperationStatus currentSampleOperationStatus = sample.getLkpOperationStatus();
			currentSampleOperationStatus = ReflectionUtil.unproxy(currentSampleOperationStatus);
			SearchCriterion sampleFilter = new SearchCriterion("labSample.rid", sample.getRid(), FilterOperator.eq);

			List<LabSampleOperationHistory> prevSampleOperationHistoryList = findSampleOperationHistoryList(
					Arrays.asList(sampleFilter), desc, "labSample");
			LkpOperationStatus lastSampleOperationStatus = null;
			if (!CollectionUtil.isCollectionEmpty(prevSampleOperationHistoryList)) {
				lastSampleOperationStatus = prevSampleOperationHistoryList.get(0).getNewOperationStatus();
				lastSampleOperationStatus = ReflectionUtil.unproxy(lastSampleOperationStatus);
			}
			LabSampleOperationHistory sampleOperationHistory = new LabSampleOperationHistory();
			sampleOperationHistory.setLabSample(sample);
			sampleOperationHistory.setOperationDate(currentDate);
			sampleOperationHistory.setOperationBy(SecurityUtil.getCurrentUser().getRid());
			if (!StringUtil.isEmpty(comment)) {
				sampleOperationHistory.setComment(comment);
			}
			//if it has a history then set the OldStatus
			if (CollectionUtil.isCollectionEmpty(prevSampleOperationHistoryList)) {
				LkpOperationStatus requestedOperationStatus = lkpService.findOneAnyLkp(
						Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq)),
						LkpOperationStatus.class);
				sampleOperationHistory.setOldOperationStatus(requestedOperationStatus);
				sampleOperationHistory.setNewOperationStatus(requestedOperationStatus);
				newSampleOperationHistoryList.add(sampleOperationHistory);
			} else if (!lastSampleOperationStatus.equals(currentSampleOperationStatus)) {
				sampleOperationHistory.setOldOperationStatus(lastSampleOperationStatus);
				sampleOperationHistory.setNewOperationStatus(currentSampleOperationStatus);
				newSampleOperationHistoryList.add(sampleOperationHistory);
			}

		}
		newSampleOperationHistoryList = getRepository().save(newSampleOperationHistoryList);
		return newSampleOperationHistoryList;

	}

	@Override
	protected LabSampleOperationHistoryRepo getRepository() {
		return this.labSampleOperationHistoryRepo;
	}

}
