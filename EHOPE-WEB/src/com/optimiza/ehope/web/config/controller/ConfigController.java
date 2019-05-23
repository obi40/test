package com.optimiza.ehope.web.config.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.ConfTenantSMS;
import com.optimiza.ehope.lis.service.ConfTenantSMSService;

@RestController
@RequestMapping("/services")
public class ConfigController {

	@Autowired
	private ConfTenantSMSService tenantSMSService;

	@RequestMapping(value = "/getSMSConfig.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ConfTenantSMS>> getSMSConfig() {
		return new ResponseEntity<List<ConfTenantSMS>>(
				tenantSMSService.getSMSConfig(new ArrayList<>(), new Sort(new Order(Direction.ASC, "smsKey.code")), "smsKey"),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/setSMSConfig.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ConfTenantSMS>> setSMSConfig(@RequestBody List<ConfTenantSMS> confTenantSMSList) {
		return new ResponseEntity<List<ConfTenantSMS>>(tenantSMSService.setSMSConfig(confTenantSMSList),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/testSMSConfig.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> testSMSConfig(@RequestBody Map<String, String> map) {
		tenantSMSService.testSMSConfig(map.get("message"), map.get("mobile"));
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}
