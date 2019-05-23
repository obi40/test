package com.optimiza.ehope.web.onboarding.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.TokenUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.onboarding.model.BrdPlan;
import com.optimiza.ehope.lis.onboarding.service.BrdPlanService;
import com.optimiza.ehope.lis.onboarding.service.SubscriptionService;
import com.optimiza.ehope.lis.onboarding.wrapper.TenantSubscriptionWrapper;
import com.optimiza.ehope.web.util.ImageUtil;

@RestController
@RequestMapping("/services")
public class OnBoardingController {

	@Autowired
	private BrdPlanService planService;
	@Autowired
	private SecTenantService tenantService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private TokenUtil tokenUtil;

	@RequestMapping(value = "/getPlanList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<BrdPlan>> getPlanList(@RequestBody FilterablePageRequest filterPageRequest) {
		return new ResponseEntity<Set<BrdPlan>>(
				new HashSet<>(planService.findPlanList(filterPageRequest.getFilters(), filterPageRequest.getSortObject(),
						"planFieldList.lkpPlanFieldType")),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/createTenantSubscription.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<TenantSubscriptionWrapper> createTenantSubscription(
			@RequestParam(value = "logo", required = false) MultipartFile logo,
			@RequestParam(value = "subscriptionWrapper") String subscriptionWrapper) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TenantSubscriptionWrapper tenantSubscriptionWrapper = mapper.readValue(subscriptionWrapper, TenantSubscriptionWrapper.class);
		if (logo != null) {
			tenantSubscriptionWrapper.getTenant().setLogo(ImageUtil.reduceSize(logo, 500, 500));
		}
		tenantSubscriptionWrapper = subscriptionService.createPlanAgreement(tenantSubscriptionWrapper);
		return new ResponseEntity<TenantSubscriptionWrapper>(tenantSubscriptionWrapper, HttpStatus.OK);
	}

	@RequestMapping(value = "/executeTenantSubscription.srvc", method = RequestMethod.POST)
	public ResponseEntity<TenantSubscriptionWrapper> executeTenantSubscription(
			@RequestBody TenantSubscriptionWrapper tenantSubscriptionWrapper) {
		return new ResponseEntity<TenantSubscriptionWrapper>(subscriptionService.executePlanAgreement(tenantSubscriptionWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/paypalNotifications.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> paypalNotifications(HttpServletRequest request)
			throws IOException, ServletException, MailException, InterruptedException, InstantiationException, IllegalAccessException {
		subscriptionService.webhookListener(request);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	//DEV PURPOSES
	@RequestMapping(value = "/crtDltTenantData.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> crtDltTenantData(@RequestBody Map<String, String> map)
			throws NumberFormatException, InstantiationException, IllegalAccessException {
		//		if (Boolean.valueOf(map.get("create"))) {
		//			subscriptionService.duplicateTenantData(tenantService.findById(Long.valueOf(map.get("rid"))));
		//		} else {
		//			subscriptionService.deleteTenantData(tenantService.findById(Long.valueOf(map.get("rid"))), Boolean.valueOf(map.get("purge")));
		//		}
		return new ResponseEntity<Object>("Success", HttpStatus.OK);
	}

	//DEV PURPOSES
	@RequestMapping(value = "/fullTenantData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> fullTenantData(@RequestBody Long tenantRid)
			throws NumberFormatException, InstantiationException, IllegalAccessException {
		subscriptionService.createFullTenantData();
		return new ResponseEntity<Object>("Success", HttpStatus.OK);
	}

	//DEV PURPOSES
	@RequestMapping(value = "/createTenantDEV.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> createTenantDEV(@RequestBody SecTenant tenant) {
		tenant = tenantService.createTenant(tenant, Boolean.TRUE);
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(EhopeRights.VIEW_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.ADD_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.UPD_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.DEL_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.VIEW_SERIAL));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.ADD_SERIAL));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.UPD_SERIAL));
		return new ResponseEntity<Object>(tokenUtil.generateTenantOnboardingToken(tenant.getRid(), authorities), HttpStatus.OK);
	}

}
