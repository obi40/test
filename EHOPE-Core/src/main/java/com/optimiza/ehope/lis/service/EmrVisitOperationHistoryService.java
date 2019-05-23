package com.optimiza.ehope.lis.service;

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
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitOperationHistory;
import com.optimiza.ehope.lis.repo.EmrVisitOperationHistoryRepo;

/**
 * EmrVisitOperationHistoryService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Apr/29/2018
 **/

@Service("EmrVisitOperationHistoryService")
public class EmrVisitOperationHistoryService extends GenericService<EmrVisitOperationHistory, EmrVisitOperationHistoryRepo> {

	@Autowired
	private EmrVisitOperationHistoryRepo visitOperationHistoryRepo;

	@Autowired
	private LkpService lkpService;
	@Autowired
	private SecUserService userService;
	@Autowired
	private EmrVisitService visitService;

	/**
	 * Create a new EmrVisitOperationHistory depending wither it has previous records or not
	 * 
	 * @param visit
	 * @return EmrVisitOperationHistory
	 */
	public EmrVisitOperationHistory createVisitOperationHistory(Long visitRid, String comment) {
		Date currentDate = new Date();
		Sort desc = new Sort(new Order(Direction.DESC, "rid"));
		EmrVisit visit = visitService.findOne(Arrays.asList(new SearchCriterion("rid", visitRid, FilterOperator.eq)), EmrVisit.class,
				"lkpOperationStatus");

		LkpOperationStatus currentVisitOperationStatus = visit.getLkpOperationStatus();
		currentVisitOperationStatus = ReflectionUtil.unproxy(currentVisitOperationStatus);
		SearchCriterion visitFilter = new SearchCriterion("emrVisit.rid", visit.getRid(), FilterOperator.eq);

		List<EmrVisitOperationHistory> prevVisitOperationHistoryList = findVisitOperationHistoryList(
				Arrays.asList(visitFilter), desc, "emrVisit");
		LkpOperationStatus lastVisitOperationStatus = null;
		if (!CollectionUtil.isCollectionEmpty(prevVisitOperationHistoryList)) {
			lastVisitOperationStatus = prevVisitOperationHistoryList.get(0).getNewOperationStatus();
			lastVisitOperationStatus = ReflectionUtil.unproxy(lastVisitOperationStatus);
		}
		EmrVisitOperationHistory visitOperationHistory = new EmrVisitOperationHistory();
		visitOperationHistory.setEmrVisit(visit);
		visitOperationHistory.setOperationDate(currentDate);
		visitOperationHistory.setOperationBy(SecurityUtil.getCurrentUser().getRid());
		if (!StringUtil.isEmpty(comment)) {
			visitOperationHistory.setComment(comment);
		}
		if (CollectionUtil.isCollectionEmpty(prevVisitOperationHistoryList)) {
			LkpOperationStatus requestedOperationStatus = lkpService.findOneAnyLkp(
					Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq)),
					LkpOperationStatus.class);
			visitOperationHistory.setOldOperationStatus(requestedOperationStatus);
			visitOperationHistory.setNewOperationStatus(requestedOperationStatus);
			visitOperationHistory = getRepository().save(visitOperationHistory);
		} else if (!lastVisitOperationStatus.equals(currentVisitOperationStatus)) {
			visitOperationHistory.setOldOperationStatus(lastVisitOperationStatus);
			visitOperationHistory.setNewOperationStatus(currentVisitOperationStatus);
			visitOperationHistory = getRepository().save(visitOperationHistory);
		}

		return visitOperationHistory;
	}

	public void deleteAllByVisit(EmrVisit visit) {
		getRepository().deleteAllByEmrVisit(visit);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORDER_MANAGEMENT + "')")
	public List<EmrVisitOperationHistory> findVisitOperationHistoryList(List<SearchCriterion> filters, Sort sort,
			String... joins) {
		List<EmrVisitOperationHistory> historyList = getRepository().find(filters, EmrVisitOperationHistory.class, sort, joins);
		for (EmrVisitOperationHistory history : historyList) {
			SecUser user = userService.findById(history.getOperationBy());
			history.setOperationByUser(user);
		}
		return historyList;
	}

	public EmrVisitOperationHistory updateVisitOperationHistory(EmrVisitOperationHistory visitOperationHistory) {
		return getRepository().save(visitOperationHistory);
	}

	@Override
	protected EmrVisitOperationHistoryRepo getRepository() {
		return this.visitOperationHistoryRepo;
	}

}
