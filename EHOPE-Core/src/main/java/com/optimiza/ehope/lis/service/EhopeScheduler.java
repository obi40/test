package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.helper.Email;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.EmailUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.onboarding.helper.PlanFieldType;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantPlanDetail;
import com.optimiza.ehope.lis.onboarding.service.BrdTenantPlanDetailService;
import com.optimiza.ehope.lis.repo.EmrVisitRepo;
import com.optimiza.ehope.lis.repo.LabSampleRepo;
import com.optimiza.ehope.lis.repo.LabTestActualRepo;
import com.optimiza.ehope.lis.util.NumberUtil;

/**
 * EhopeScheduler.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/15/2018
 **/
@Component
@InterceptorFree
public class EhopeScheduler {

	//https://dzone.com/articles/running-on-time-with-springs-scheduled-tasks

	@Autowired
	private BrdTenantPlanDetailService tenantPlanDetailService;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private EmrVisitRepo visitRepo;
	@Autowired
	private LabSampleRepo sampleRepo;
	@Autowired
	private LabTestActualRepo testActualRepo;

	/**
	 * To check the current tenant's counters and send email if it is below the grace limit
	 */
	@Scheduled(cron = "59 59 23 * * ?")
	public void countersTask() {
		BigDecimal gracePercentage = new BigDecimal(10);
		BigDecimal percentage = gracePercentage.divide(NumberUtil.MAX_PERCENTAGE);
		Sort sort = new Sort(new Order(Direction.ASC, "tenant.rid"));
		SearchCriterion isActiveFilter = new SearchCriterion("tenant.isActive", true, FilterOperator.eq);
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(isActiveFilter);
		Map<SecTenant, List<BrdTenantPlanDetail>> tenantPlanDetailsMap = tenantPlanDetailService.find(filters,
				BrdTenantPlanDetail.class, sort, "tenant", "planField.lkpPlanFieldType").stream().collect(
						Collectors.groupingBy(BrdTenantPlanDetail::getTenant, Collectors.mapping(b -> b, Collectors.toList())));
		Map<String, String> templateValues = new HashMap<>();
		for (Map.Entry<SecTenant, List<BrdTenantPlanDetail>> entry : tenantPlanDetailsMap.entrySet()) {
			SecTenant tenant = entry.getKey();
			boolean toSend = false;
			for (BrdTenantPlanDetail tpd : entry.getValue()) {
				BigDecimal grace = tpd.getAmount().multiply(percentage);
				BigDecimal remaining = tpd.getAmount().subtract(tpd.getCurrent());
				PlanFieldType planFieldType = PlanFieldType.getByValue(tpd.getPlanField().getLkpPlanFieldType().getCode());
				switch (planFieldType) {
					case ORDERS:
						templateValues.put("ordersCurrent", tpd.getCurrent().toString());
						templateValues.put("ordersMaxAmount", tpd.getAmount().toString());
						break;
					case USERS:
						templateValues.put("usersCurrent", tpd.getCurrent().toString());
						templateValues.put("usersMaxAmount", tpd.getAmount().toString());
						break;
				}

				//send an email only if the remaining is less than the grace in any of the fields
				if (remaining.compareTo(grace) <= 0) {
					toSend = true;
				}
			}
			if (toSend) {
				Email email = new Email("email-counter", tenant.getName(), tenant.getEmail(), templateValues);
				emailUtil.sendMailTemplate(email);
			}

			templateValues = new HashMap<>();
		}
	}

	/**
	 * If the order creation date has exceeded a day and it is still requested then abort it.
	 * 
	 */
	@Scheduled(cron = "59 59 23 * * ?")
	@Transactional(readOnly = false)
	public void visitsAborter() {
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("lkpOperationStatus.code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq));
		filters.add(new SearchCriterion("creationDate", DateUtil.addDays(new Date(), -2), FilterOperator.gte));
		filters.add(new SearchCriterion("creationDate", DateUtil.addDays(new Date(), -1), FilterOperator.lte));
		LkpOperationStatus abortedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.ABORTED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		Set<EmrVisit> visits = new HashSet<>(
				visitService.findExcluded(filters, null, "lkpOperationStatus", "labSamples.labTestActualSet"));
		List<Long> visitRids = visits.stream().map(EmrVisit::getRid).collect(Collectors.toList());
		List<Long> sampleRids = visits.stream().flatMap(v -> v.getLabSamples().stream()).map(s -> s.getRid()).collect(Collectors.toList());
		List<Long> testRids = visits.stream().flatMap(v -> v.getLabSamples().stream()).flatMap(s -> s.getLabTestActualSet().stream())
									.map(t -> t.getRid()).collect(Collectors.toList());
		//can't use normal save since we don't have a user.
		if (!CollectionUtil.isCollectionEmpty(visitRids)) {
			visitRepo.updateOperationStatus(visitRids, abortedOperationStatus);
			if (!CollectionUtil.isCollectionEmpty(sampleRids)) {
				sampleRepo.updateOperationStatus(sampleRids, abortedOperationStatus);
				if (!CollectionUtil.isCollectionEmpty(testRids)) {
					testActualRepo.updateOperationStatus(testRids, abortedOperationStatus);
				}
			}

		}

	}
}
