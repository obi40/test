package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.SerialFormat;
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.model.SysSerial;
import com.optimiza.ehope.lis.repo.SysSerialRepo;

@Service("SysSerialService")
public class SysSerialService extends GenericService<SysSerial, SysSerialRepo> {

	@Autowired
	private SysSerialRepo repo;
	@Autowired
	private SecTenantService tenantService;

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_SERIAL + "')")
	public Map<Long, List<SysSerial>> getSerialsData() {
		Sort sort = new Sort(new Order(Direction.ASC, "serialType.code"));
		Map<Long, List<SysSerial>> map = new HashMap<>();
		List<SysSerial> serials = getRepository().find(
				Arrays.asList(new SearchCriterion("serialType.code", SerialType.SAMPLE_BARCODE.getValue(), FilterOperator.neq)),
				SysSerial.class, sort, "serialType", "serialFormat", "labBranch");
		map.put(-1L, new ArrayList<>());
		for (SysSerial ss : serials) {
			if (ss.getLabBranch() == null) {
				map.get(-1L).add(ss);
			} else {
				if (map.containsKey(ss.getLabBranch().getRid())) {
					map.get(ss.getLabBranch().getRid()).add(ss);
				} else {
					List<SysSerial> tempSerials = new ArrayList<>();
					tempSerials.add(ss);
					map.put(ss.getLabBranch().getRid(), tempSerials);
				}
			}

		}
		return map;
	}

	@InterceptorFree
	public List<SysSerial> findSerialsExcluded(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, SysSerial.class, joins);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_SERIAL + "')")
	public List<SysSerial> createSerial(List<SysSerial> serials) {
		return getRepository().save(serials);
	}

	public List<SysSerial> createSerialNoAuth(List<SysSerial> serials) {
		return getRepository().save(serials);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_SERIAL + "')")
	public List<SysSerial> updateSerial(List<SysSerial> serial) {
		return getRepository().save(serial);
	}

	public SysSerial updateSerial(SysSerial serial) {
		return getRepository().save(serial);
	}

	/**
	 * Get the required data so the generation of sequence can work probably.
	 * 
	 * @param serialType
	 * @return SysSerial
	 */
	public SysSerial getSerialGenerationData(SerialType serialType) {
		List<SysSerial> serials = getRepository().findBySerialTypeBranch(serialType.getValue(),
				SecurityUtil.getCurrentUser().getBranchId());
		SysSerial tenantSerial = null;
		SysSerial branchSerial = null;
		for (SysSerial ss : serials) {
			if (ss.getLabBranch() == null) {
				tenantSerial = ss;
			} else {
				branchSerial = ss;
			}
		}
		SysSerial selectedSerial = tenantSerial.getIsBranchLevel() ? branchSerial : tenantSerial;
		if (serialType == SerialType.SAMPLE_BARCODE) {//if bar code then always user branch's serial
			selectedSerial = branchSerial;
		} else if (serialType == SerialType.PATIENT_FILE_NO) {//if patient file no then always user tenant's serial
			selectedSerial = tenantSerial;
		}
		selectedSerial.setTenant(tenantService.findById(selectedSerial.getTenantId()));
		return selectedSerial;
	}

	/**
	 * The normal way of generating a sequence.
	 * 
	 * @param serialType
	 * @return sequence
	 */
	public String sequenceGeneration(SerialType serialType) {
		SysSerial serial = getSerialGenerationData(serialType);
		String sequence = sequenceBuilder(serial);
		updateSerial(serial);
		return sequence;
	}

	/**
	 * Actual generator of the sequence.
	 * 
	 * @param serial
	 * @return sequence
	 */
	public String sequenceBuilder(SysSerial serial) {

		// we are saving the new value in serial outside this function
		Long newValue = serial.getCurrentValue() + 1;
		String sequence = String.valueOf(newValue);
		String delimiter = StringUtil.isEmpty(serial.getDelimiter()) ? "" : serial.getDelimiter();
		StringBuilder builder = new StringBuilder();
		SerialFormat format = SerialFormat.valueOf(serial.getSerialFormat().getCode());
		switch (format) {
			case ANNUAL:
				String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
				year = year.substring(year.length() - 2);
				builder.append(year);
				builder.append(delimiter);
				if (!StringUtil.isEmpty(serial.getLastSerial()) && !serial.getLastSerial().startsWith(year)) {
					newValue = 0L;//reset if new year
					sequence = "0";//reset if new year
				}
				break;
			case LOCATION:
				builder.append(serial.getTenant().getCode());
				builder.append(delimiter);
				if (serial.getLabBranch() != null) {
					builder.append(serial.getLabBranch().getCode());
					builder.append(delimiter);
				}
				break;
			case SERIAL://nothing here since we always add the sequence number
				break;
		}
		if (serial.getFiller() != null && serial.getFiller() > 0) {
			sequence = String.format("%0" + serial.getFiller() + "d", newValue);
		}
		builder.append(sequence);
		serial.setCurrentValue(newValue);
		serial.setLastSerial(builder.toString());
		return builder.toString();
	}

	//	public String getLastSerialByType(SerialType serialType) {
	//		BaseEntity entity = null;
	//		Pageable pageable = new PageRequest(0, 1, Sort.Direction.ASC, "rid");
	//		Sort sort = new Sort(new Order(Direction.DESC, "rid"));
	//		Class<BaseEntity> entityClass = null;
	//		switch (serialType) {
	//			case CHARGE_SLIP_NO:
	//				Class<?> s = BillChargeSlip.class;
	//				entityClass = (Class<BaseEntity>) s;
	//				List<BaseEntity> e = ReflectionUtil	.getRepository("BillChargeSlip")
	//													.find(new ArrayList<SearchCriterion>(), pageable, entityClass).getContent();
	//				if (!CollectionUtil.isCollectionEmpty(e)) {
	//					entity = e.get(0);
	//				}
	//				break;
	//			case PATIENT_FILE_NO:
	//				break;
	//			case CANCEL_NO:
	//			case PAYMENT_NO:
	//			case RECALCULATE_NO:
	//			case REFUND_NO:
	//				break;
	//			case SAMPLE_BARCODE:
	//			case SAMPLE_NO:
	//				break;
	//			case VISIT_ADMISSION_NO:
	//			case INVOICE_NO:
	//				break;
	//		}
	//		return "";
	//	}

	//	private long getNextWithRetry() {
	//	    int retryCount = 10;
	//	    while(--retryCount >=0) {
	//	        try {
	//	            return shunyaCounterService.incrementAndGetNextOptimistic(CounterType.MISC_PAYMENT);
	//	        } catch (HibernateOptimisticLockingFailureException e) {
	//	            logger.warn("Mid air collision detected, retrying - " + e.getMessage());
	//	            try {
	//	                Thread.sleep(100);
	//	            } catch (InterruptedException e1) {
	//	                e1.printStackTrace();
	//	            }
	//	        }
	//	    }
	//	    throw  new RuntimeException("Maximum retry limit exceeded");
	//	}

	@Override
	protected SysSerialRepo getRepository() {
		return repo;
	}

}
