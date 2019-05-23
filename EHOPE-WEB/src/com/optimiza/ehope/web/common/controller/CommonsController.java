package com.optimiza.ehope.web.common.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.model.ComLanguage;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.service.ComLanguageService;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.ehope.lis.model.ComTenantMessage;
import com.optimiza.ehope.lis.service.ComTenantMessageService;

/**
 * CommonsController.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/
@RestController
@RequestMapping("/services")
public class CommonsController {

	@Autowired
	private ComTenantMessageService comTenantMessageService;

	@Autowired
	private ComLanguageService languageService;

	@Autowired
	private ComTenantLanguageService tenantLanguageService;

	@RequestMapping(value = "/getTenantMessagesList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<ComTenantMessage>> getTenantMessagesList(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<ComTenantMessage>>(comTenantMessageService.findTenantMessagesList(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getLabels.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantMessage>> getLabels() {
		List<ComTenantMessage> messages = new ArrayList<>();
		//Some times this api is called when having a dummy token, so the cache generator of findLabels will throw exception
		//if no tenant id
		if (SecurityUtil.isUserLoggedIn() && SecurityUtil.getCurrentUser().getTenantId() != null) {
			messages = comTenantMessageService.findLabels(ComTenantMessage.class);
		} else {
			messages = comTenantMessageService.findDefaultLabels(ComTenantMessage.class);
		}
		return new ResponseEntity<List<ComTenantMessage>>(messages, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTenantMessage.srvc", method = RequestMethod.POST)
	public ResponseEntity<ComTenantMessage> updateTenantMessage(@RequestBody ComTenantMessage tenantMessage) {

		return new ResponseEntity<ComTenantMessage>(
				comTenantMessageService.updateTenantMessage(tenantMessage, ComTenantMessage.class),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/createTenantMessage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> createTenantMessage(@RequestBody ComTenantMessage tenantMessage) {
		comTenantMessageService.createTenantMessage(tenantMessage, ComTenantMessage.class);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTenantMessage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteTenantMessage(@RequestBody ComTenantMessage tenantMessage) {
		comTenantMessageService.deleteTenantMessage(tenantMessage);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getSupportedLanguages.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComLanguage>> getSupportedLanguages() {
		return new ResponseEntity<List<ComLanguage>>(languageService.find(new ArrayList<>(), ComLanguage.class), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTenantLanguages.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantLanguage>> getTenantLanguages() {
		return new ResponseEntity<List<ComTenantLanguage>>(
				tenantLanguageService.findTenantLanguages(new ArrayList<>(), null, "comLanguage"), HttpStatus.OK);
	}

	@RequestMapping(value = "/setTenantLanguages.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantLanguage>> setTenantLanguages(@RequestBody List<ComTenantLanguage> tenantLanguages) {
		return new ResponseEntity<List<ComTenantLanguage>>(tenantLanguageService.updateTenantLanguages(tenantLanguages), HttpStatus.OK);
	}

}
