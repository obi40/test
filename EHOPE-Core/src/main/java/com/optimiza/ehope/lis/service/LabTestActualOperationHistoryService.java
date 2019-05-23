package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualOperationHistory;
import com.optimiza.ehope.lis.repo.LabTestActualOperationHistoryRepo;

/**
 * LabTestActualOperationHistoryService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/07/2018
 **/

@Service("LabTestActualOperationHistoryService")
public class LabTestActualOperationHistoryService extends GenericService<LabTestActualOperationHistory, LabTestActualOperationHistoryRepo> {

	@Autowired
	private LabTestActualOperationHistoryRepo testActualOperationHistoryRepo;

	@Autowired
	private LkpService lkpService;
	@Autowired
	private SecUserService userService;

	public void deleteAllByTestActual(LabTestActual lta) {
		getRepository().deleteAllByLabTestActual(lta);
	}

	public void deleteTestActualOperationHistory(List<LabTestActualOperationHistory> testActualOperationHistoryList) {
		getRepository().delete(testActualOperationHistoryList);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORDER_MANAGEMENT + "')")
	public List<LabTestActualOperationHistory> findTestActualOperationHistoryList(List<SearchCriterion> filters, Sort sort,
			String... joins) {
		List<LabTestActualOperationHistory> historyList = getRepository().find(filters, LabTestActualOperationHistory.class, sort, joins);
		for (LabTestActualOperationHistory history : historyList) {
			SecUser user = userService.findById(history.getOperationBy());
			history.setOperationByUser(user);
		}
		return historyList;
	}

	public List<LabTestActualOperationHistory> findByTestActual(Long testActualRid) {

		return getRepository().find(Arrays.asList(new SearchCriterion("labTestActual.rid", testActualRid, FilterOperator.eq)),
				LabTestActualOperationHistory.class, "labTestActual");
	}

	/**
	 * Create a new LabTestActualOperationHistory depending wither it has previous records or not
	 * 
	 * @param testActualList
	 * @return List of LabTestActualOperationHistory
	 */
	public List<LabTestActualOperationHistory> createTestActualOperationHistory(List<LabTestActual> testActualList, String comment) {
		Date currentDate = new Date();
		Sort desc = new Sort(new Order(Direction.DESC, "rid"));
		LkpOperationStatus requestedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		List<LabTestActualOperationHistory> newTestActualOperationHistoryList = new ArrayList<>();
		for (LabTestActual testActual : testActualList) {

			LkpOperationStatus currentTestActualOperationStatus = testActual.getLkpOperationStatus();
			currentTestActualOperationStatus = ReflectionUtil.unproxy(currentTestActualOperationStatus);
			SearchCriterion testActualFilter = new SearchCriterion("labTestActual.rid", testActual.getRid(), FilterOperator.eq);

			List<LabTestActualOperationHistory> prevTestActualOperationHistoryList = findTestActualOperationHistoryList(
					Arrays.asList(testActualFilter), desc, "labTestActual");
			LkpOperationStatus lastTestActualOperationStatus = null;
			if (!CollectionUtil.isCollectionEmpty(prevTestActualOperationHistoryList)) {
				lastTestActualOperationStatus = prevTestActualOperationHistoryList.get(0).getNewOperationStatus();
				lastTestActualOperationStatus = ReflectionUtil.unproxy(lastTestActualOperationStatus);
			}
			LabTestActualOperationHistory testActualOperationHistory = new LabTestActualOperationHistory();
			testActualOperationHistory.setLabTestActual(testActual);
			testActualOperationHistory.setOperationDate(currentDate);
			testActualOperationHistory.setOperationBy(SecurityUtil.getCurrentUser().getRid());
			if (!StringUtil.isEmpty(comment)) {
				testActualOperationHistory.setComment(comment);
			}
			if (CollectionUtil.isCollectionEmpty(prevTestActualOperationHistoryList)) {
				testActualOperationHistory.setOldOperationStatus(requestedOperationStatus);
				testActualOperationHistory.setNewOperationStatus(requestedOperationStatus);
				newTestActualOperationHistoryList.add(testActualOperationHistory);
			} else if (!lastTestActualOperationStatus.equals(currentTestActualOperationStatus)) {
				testActualOperationHistory.setOldOperationStatus(lastTestActualOperationStatus);
				testActualOperationHistory.setNewOperationStatus(currentTestActualOperationStatus);
				newTestActualOperationHistoryList.add(testActualOperationHistory);
			}

		}
		newTestActualOperationHistoryList = getRepository().save(newTestActualOperationHistoryList);
		return newTestActualOperationHistoryList;

	}

	public List<LabTestActualOperationHistory> createTestActualOperationHistory(
			List<LabTestActualOperationHistory> testActualOperationHistories) {
		return getRepository().save(testActualOperationHistories);
	}

	@Override
	protected LabTestActualOperationHistoryRepo getRepository() {
		return this.testActualOperationHistoryRepo;
	}

}
