package com.optimiza.ehope.web.lkp.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.lkp.model.LkpMaster;
import com.optimiza.core.lkp.service.LkpMasterService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.web.lkp.wrapper.LkpWrapper;

@RestController
@RequestMapping("/services")
public class LkpsController {

	@Autowired
	private LkpMasterService lkpMasterService;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private EntityManager entityManager;

	@RequestMapping(value = "/getLkpMasterList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LkpMaster>> getLkpMasterList() {
		return new ResponseEntity<List<LkpMaster>>(lkpMasterService.findLkpMasters(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getLkpByClass.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> getLkpByClass(@RequestBody LkpWrapper lkpWrapper) {

		Class<?> entityClass = ReflectionUtil.getEntityClassByName(lkpWrapper.getClassName(), entityManager);
		List<SearchCriterion> filters = lkpWrapper.getFilterableFilters();
		String[] joins = lkpWrapper.getJoins();
		Sort sort = lkpWrapper.getFilterableSort();

		if (lkpService.isLkpTenanted(entityClass)) {
			return new ResponseEntity<Object>(lkpService.findTenantedLkp(filters, entityClass, sort, joins), HttpStatus.OK);
		} else {
			return new ResponseEntity<Object>(lkpService.findNonTenantedLkp(filters, entityClass, sort, joins), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getAnyLkpByClass.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> getAnyLkpByClass(@RequestBody LkpWrapper lkpWrapper) {
		Class<?> entityClass = ReflectionUtil.getEntityClassByName(lkpWrapper.getClassName(), entityManager);
		List<SearchCriterion> filters = lkpWrapper.getFilterableFilters();
		String[] joins = lkpWrapper.getJoins();
		Sort sort = lkpWrapper.getFilterableSort();
		return new ResponseEntity<Object>(lkpService.findAnyLkp(filters, entityClass, sort, joins), HttpStatus.OK);
	}

	@RequestMapping(value = "/getOneLkpByClass.srvc", method = RequestMethod.POST)
	public ResponseEntity<BaseEntity> getOneLkpByClass(@RequestBody LkpWrapper lkpWrapper) {
		Class<?> entityClass = ReflectionUtil.getEntityClassByName(lkpWrapper.getClassName(), entityManager);
		List<SearchCriterion> filters = lkpWrapper.getFilterableFilters();
		String[] joins = lkpWrapper.getJoins();
		BaseEntity be = lkpService.findOneAnyLkp(filters, entityClass, joins);
		return new ResponseEntity<BaseEntity>(be, HttpStatus.OK);
	}

	@RequestMapping(value = "/createLkp.srvc", method = RequestMethod.POST)
	public ResponseEntity<BaseEntity> createLkp(@RequestBody Map<String, String> map) {

		Class<? extends BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(map.get("className"), entityManager);
		BaseEntity entity = ReflectionUtil.getObjectFromUknownType(map.get("className"), map.get("object"), entityManager);

		if (lkpService.isLkpTenanted(entityClass)) {
			return new ResponseEntity<BaseEntity>(lkpService.createTenantedLkp(entityClass, entity), HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseEntity>(lkpService.createNonTenantedLkp(entity), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/updateLkp.srvc", method = RequestMethod.POST)
	public ResponseEntity<BaseEntity> updateLkp(@RequestBody Map<String, String> map) {
		Class<? extends BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(map.get("className"), entityManager);
		BaseEntity entity = ReflectionUtil.getObjectFromUknownType(map.get("className"), map.get("object"), entityManager);

		if (lkpService.isLkpTenanted(entityClass)) {
			return new ResponseEntity<BaseEntity>(lkpService.updateTenantedLkp(entityClass, entity), HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseEntity>(lkpService.updateNonTenantedLkp(entity), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/deleteLkp.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteLkp(@RequestBody Map<String, String> map) {
		Class<? extends BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(map.get("className"), entityManager);
		BaseEntity entity = ReflectionUtil.getObjectFromUknownType(map.get("className"), map.get("object"), entityManager);

		if (lkpService.isLkpTenanted(entityClass)) {
			lkpService.deleteTenantedLkp(entityClass, entity);
		} else {
			lkpService.deleteNonTenantedLkp(entity);

		}
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}
