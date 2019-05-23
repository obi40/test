package com.optimiza.ehope.lis.onboarding.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optimiza.core.admin.lkp.model.LkpUserStatus;
import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.model.SecRoleRight;
import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.admin.service.SecGroupRoleService;
import com.optimiza.core.admin.service.SecGroupService;
import com.optimiza.core.admin.service.SecRightService;
import com.optimiza.core.admin.service.SecRoleRightService;
import com.optimiza.core.admin.service.SecRoleService;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.admin.service.SecUserRoleService;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.Email;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.EmailUtil;
import com.optimiza.core.common.util.HttpUtil;
import com.optimiza.core.common.util.JSONUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.model.ComLanguage;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.core.lkp.service.ComLanguageService;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.lkp.model.LkpSerialFormat;
import com.optimiza.ehope.lis.lkp.model.LkpSerialType;
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.model.ComTenantMessage;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.model.LabUnit;
import com.optimiza.ehope.lis.model.SysSerial;
import com.optimiza.ehope.lis.onboarding.helper.PayPalResource;
import com.optimiza.ehope.lis.onboarding.helper.SubscriptionEmail;
import com.optimiza.ehope.lis.onboarding.helper.WebhookEvent;
import com.optimiza.ehope.lis.onboarding.model.BrdPlan;
import com.optimiza.ehope.lis.onboarding.model.BrdPlanField;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantPlanDetail;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantSubscription;
import com.optimiza.ehope.lis.onboarding.wrapper.TenantSubscriptionWrapper;
import com.optimiza.ehope.lis.service.BillBalanceTransactionService;
import com.optimiza.ehope.lis.service.BillClassificationService;
import com.optimiza.ehope.lis.service.ComTenantMessageService;
import com.optimiza.ehope.lis.service.LabBranchSeparationFactorService;
import com.optimiza.ehope.lis.service.LabBranchService;
import com.optimiza.ehope.lis.service.LabUnitService;
import com.optimiza.ehope.lis.service.SysSerialService;
import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PaymentDefinition;
import com.paypal.api.payments.Plan;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * SubscriptionService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/
@Service("SubscriptionService")
@Transactional(readOnly = false)
public class SubscriptionService extends HttpServlet {

	//paypal sandbox pass : 147852369
	//TODO: The flow looks fine but I would suggest you to block eCheck payments under payment receiving preference inside of your PayPal account profile
	//to stop potential pending payments as it will be too complicated.
	//TODO: Didn't receive any events regarding a user that didn't have any funding and couldn't pay for subscription
	//TODO: paypal from sandbox to live
	//TODO: delete tenant if canceled the payment before finishing wizard
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${system.paypal.clientId}")
	private String CLIENT_ID;
	@Value("${system.paypal.clientSecret}")
	private String CLIENT_SECRET;
	@Value("${system.website.url}")
	private String URL_CANCEL;
	@Value("${system.website.url}")
	private String URL_RETURN;
	private final String WEBHOOK_ID = "9GW353488W525850D";

	@Autowired
	private SecTenantService tenantService;
	@Autowired
	private BrdPlanService planService;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private BrdTenantSubscriptionService tenantSubscriptionService;
	@Autowired
	private ComTenantLanguageService tenantLanguageService;
	@Autowired
	private ComTenantMessageService tenantMessageService;
	@Autowired
	private SecUserService userService;
	@Autowired
	private SecUserRoleService userRoleService;
	@Autowired
	private SecGroupService groupService;
	@Autowired
	private SecGroupRoleService groupRoleService;
	@Autowired
	private SecRoleService roleService;
	@Autowired
	private SecRoleRightService roleRightService;
	@Autowired
	private SecRightService rightService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private BrdTenantPlanDetailService tenantPlanDetailService;
	@Autowired
	private BillBalanceTransactionService balanceService;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private SysSerialService serialService;
	@Autowired
	private LabBranchSeparationFactorService branchSeparationFactorService;
	@Autowired
	private LabUnitService unitService;
	@Autowired
	private BillClassificationService classificationService;
	@Autowired
	private ComLanguageService languageService;
	@Autowired
	private BrdPlanFieldService planFieldService;
	@Autowired
	private EntityManager entityManager;

	@PostConstruct
	public void initializing() {
		//since the initial value get populated later on by spring
		URL_RETURN = URL_RETURN.concat("subscription");
	}

	private APIContext getContext() {
		return new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");
	}

	private Plan createPlan(TenantSubscriptionWrapper tenantSubscriptionWrapper) {
		// set the price depending the selected plan either the price of the plan it self OR the custom fields
		// also to get the prices from DB
		List<BrdPlan> planList = planService.findPlanList(new ArrayList<>(), null, "planFieldList");
		BrdPlan selectedPlan = null;
		for (BrdPlan plan : planList) {
			if (plan.equals(tenantSubscriptionWrapper.getPlan())) {
				selectedPlan = plan;
				break;
			}
		}
		Long price = selectedPlan.getPrice();
		//is it a custom plan 
		if (price == null) {
			price = 0L;
			for (BrdPlanField planField : selectedPlan.getPlanFieldList()) {
				for (BrdPlanField userPlanField : tenantSubscriptionWrapper.getPlanFieldList()) {
					if (planField.equals(userPlanField)) {
						if (userPlanField.getAmount() < 0) {
							throw new BusinessException("amount cant be less than zero", "amount cant be less than zero",
									ErrorSeverity.ERROR);
						}
						price += (planField.getPrice() * userPlanField.getAmount());
						break;
					}
				}
			}
		}
		tenantSubscriptionWrapper.setPrice(price);
		// Build Plan object
		Plan plan = new Plan();
		plan.setName("AccuLab Plan Subscription");
		plan.setDescription("AccuLab Plan Subscription: " + selectedPlan.getName() + ", Price: " + price + " USD/Monthly");
		plan.setType("infinite");

		// Payment_definitions
		PaymentDefinition paymentDefinition = new PaymentDefinition();
		paymentDefinition.setName("Regular Monthly Payments");
		paymentDefinition.setType("REGULAR");
		paymentDefinition.setFrequency("MONTH");
		paymentDefinition.setFrequencyInterval("1");
		paymentDefinition.setCycles("0");//infinite

		// Currency
		Currency currency = new Currency();
		currency.setCurrency("USD");
		currency.setValue(Long.toString(price));

		paymentDefinition.setAmount(currency);

		// Charge_models
		//		ChargeModels chargeModels = new com.paypal.api.payments.ChargeModels();
		//		chargeModels.setType("SHIPPING");
		//		chargeModels.setAmount(currency);
		//		List<ChargeModels> chargeModelsList = new ArrayList<ChargeModels>();
		//		chargeModelsList.add(chargeModels);
		//		paymentDefinition.setChargeModels(chargeModelsList);

		// Payment_definition
		plan.setPaymentDefinitions(Arrays.asList(paymentDefinition));

		// Merchant_preferences
		MerchantPreferences merchantPreferences = new MerchantPreferences();
		merchantPreferences.setCancelUrl(URL_CANCEL);
		merchantPreferences.setReturnUrl(URL_RETURN);
		merchantPreferences.setMaxFailAttempts("1");
		merchantPreferences.setAutoBillAmount("YES");
		merchantPreferences.setInitialFailAmountAction("CANCEL");
		plan.setMerchantPreferences(merchantPreferences);
		Plan createdPlan = null;
		try {
			APIContext context = getContext();
			createdPlan = plan.create(context);
			// Set up plan activate PATCH request
			List<Patch> patchRequestList = new ArrayList<>();
			Map<String, String> value = new HashMap<>();
			value.put("state", "ACTIVE");
			// Create update object to activate plan
			Patch patch = new Patch();
			patch.setPath("/");
			patch.setValue(value);
			patch.setOp("replace");
			patchRequestList.add(patch);
			// Activate plan
			createdPlan.update(context, patchRequestList);

		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
			throw new BusinessException(e.getLocalizedMessage(), e.getMessage(), ErrorSeverity.ERROR);
		}
		return createdPlan;
	}

	public TenantSubscriptionWrapper createPlanAgreement(TenantSubscriptionWrapper tenantSubscriptionWrapper) {
		tenantService.checkIfDuplicatedTenant(true, tenantSubscriptionWrapper.getTenant().getEmail(),
				tenantSubscriptionWrapper.getTenant().getCode(), null);
		Plan createdPlan = createPlan(tenantSubscriptionWrapper);
		// Create new agreement
		Agreement agreement = new Agreement();
		agreement.setName("AccuLab Plan Agreement");
		agreement.setDescription("AccuLab Plan Agreement: " + tenantSubscriptionWrapper.getPlan().getName() + ", Price: "
				+ tenantSubscriptionWrapper.getPrice() + " USD/Monthly");
		agreement.setStartDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));//paypal format or else paypal throw exception
		// Set plan ID
		Plan plan = new Plan();
		plan.setId(createdPlan.getId());
		agreement.setPlan(plan);

		// Add payer details
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		agreement.setPayer(payer);
		try {
			APIContext context = getContext();
			agreement = agreement.create(context);
			SecTenant createdTenant = tenantService.createTenant(tenantSubscriptionWrapper.getTenant(), Boolean.FALSE);
			tenantPlanDetailService.createTenantPlanDetails(createdTenant, tenantSubscriptionWrapper.getPlanFieldList());
			List<ComTenantLanguage> tenantLanguages = tenantSubscriptionWrapper.getTenantLangauges();
			for (ComTenantLanguage ctl : tenantLanguages) {
				ctl.setTenantId(createdTenant.getRid());
			}
			tenantLanguages = tenantLanguageService.createTenantLanguages(tenantLanguages);
			tenantSubscriptionWrapper.setTenant(createdTenant);
			tenantSubscriptionWrapper.setTenantLangauges(tenantLanguages);
			for (Links links : agreement.getLinks()) {
				if ("approval_url".equals(links.getRel())) {
					tenantSubscriptionWrapper.setApprovalUrl(links.getHref());
					break;
				}
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			throw new BusinessException(e.getLocalizedMessage(), e.getDetails().getMessage(), ErrorSeverity.ERROR);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new BusinessException(e.getLocalizedMessage(), e.getMessage(), ErrorSeverity.ERROR);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new BusinessException(e.getLocalizedMessage(), e.getMessage(), ErrorSeverity.ERROR);
		}

		return tenantSubscriptionWrapper;
	}

	@SuppressWarnings("static-access")
	public TenantSubscriptionWrapper executePlanAgreement(TenantSubscriptionWrapper tenantSubscriptionWrapper) {
		Agreement agreement = new Agreement();
		agreement.setToken(tenantSubscriptionWrapper.getToken());
		try {
			APIContext newContext = getContext();
			Agreement createdAgreement = agreement.execute(newContext, agreement.getToken());
			String payerId = createdAgreement.getPayer().getPayerInfo().getPayerId();
			tenantSubscriptionWrapper.getTenant().setPayerId(payerId);
			tenantSubscriptionWrapper.getTenant().setIsActive(Boolean.FALSE);
			SecTenant updatedTenant = tenantService.updateTenantExcluded(tenantSubscriptionWrapper.getTenant());
			tenantSubscriptionService.createTenantSubscription(updatedTenant, null, tenantSubscriptionWrapper.getPlan());// date is null till the payment arrives

			tenantSubscriptionWrapper.setTenant(updatedTenant);
			tenantSubscriptionWrapper.setAgreement(createdAgreement.toJSON());
			sendSubscriptionEmail(updatedTenant, null, SubscriptionEmail.EMAIL_TENANT_EXECUTE);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			throw new BusinessException(e.getLocalizedMessage(), e.getMessage(), ErrorSeverity.ERROR);
		}
		return tenantSubscriptionWrapper;
	}

	/**
	 * A listener for the web hook that is sent by Paypal.
	 * 
	 * @param request
	 */
	public void webhookListener(HttpServletRequest request) {
		logger.info("Webhook Listener: Start");
		Map<String, Object> bodyMap = JSONUtil.convertJSONToMap(HttpUtil.getBody(request), String.class, Object.class);
		WebhookEvent webhookEvent = WebhookEvent.getWebhookByValue((String) bodyMap.get("event_type"));
		// In case web hook fired for events that we don't take any actions on
		if (webhookEvent == null) {
			logger.info("Webhook Event: " + (String) bodyMap.get("event_type") + " (No Action)");
			logger.info("Webhook Listener: End");
			return;
		}
		try {
			// Set the webhookId that you received when you created this web hook.
			APIContext context = getContext();
			context.addConfiguration(Constants.PAYPAL_WEBHOOK_ID, WEBHOOK_ID);
			String body = (String) bodyMap.get("body");
			//TODO: REMOVE below line
			webhookAction(bodyMap, webhookEvent);
			//			Boolean valid = Event.validateReceivedEvent(context, getHeadersInfo(request), body);
			//			if (valid) {
			//				webhookAction(bodyMap, webhookEvent);
			//				logger.info("Webhook Listener: End");
			//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The action related to the web hook event.
	 * 
	 * @param bodyMap
	 * @param webhookEvent
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	private void webhookAction(Map<String, Object> bodyMap, WebhookEvent webhookEvent) {
		logger.info("Webhook Action: Start");
		logger.info("Webhook Event: " + webhookEvent.getValue());
		String resourceType = ((String) bodyMap.get("resource_type")).toLowerCase().trim();
		Map<String, Object> resource = (Map<String, Object>) bodyMap.get("resource");
		String agreementId = null;
		String payerId = null;
		PayPalResource ppr = PayPalResource.getByValue(resourceType);
		// two ways of obtaining the payer id
		if (ppr == PayPalResource.SALE) {
			try {
				APIContext context = getContext();
				Agreement agreement = Agreement.get(context, (String) resource.get("billing_agreement_id"));
				payerId = agreement.getPayer().getPayerInfo().getPayerId();
				agreementId = agreement.getId();
			} catch (PayPalRESTException e) {
				e.printStackTrace();
				logger.info("Webhook Action: End");
				return;
			}
		} else if (ppr == PayPalResource.AGREEMENT) {
			Map<String, Object> payer = (Map<String, Object>) resource.get("payer");
			Map<String, Object> payerInfo = (Map<String, Object>) payer.get("payer_info");
			agreementId = (String) resource.get("id");
			payerId = (String) payerInfo.get("payer_id");
		}

		if (payerId == null) {
			logger.info("Couldn't get Payer Id (No Action)");
			logger.info("Webhook Action: End");
			return;
		}
		logger.info("Agreement Id: " + agreementId);
		logger.info("Payer Id: " + payerId);

		SecTenant tenant = tenantService.findTenantByPayerIdExcluded(payerId);
		if (tenant == null) {
			logger.info("Tenant Not Found With Payer Id: " + payerId + " (No Action)");
			logger.info("Webhook Action: End");
			return;
		}
		logger.info("Tenant Id: " + tenant.getRid());

		//sorting by rid because we cant use the expiry date as it may be null if it is a new subscription
		BrdTenantSubscription tenantSubscription = tenantSubscriptionService.findTenantSubscriptions(
				Arrays.asList(new SearchCriterion("tenant.rid", tenant.getRid(), FilterOperator.eq)), new Sort(Direction.DESC, "rid"),
				"tenant", "plan").get(0);
		if (tenantSubscription == null) {
			logger.info("Subscription Not Found With Tenant Id: " + tenant.getRid() + " (No Action)");
			logger.info("Webhook Action: End");
			return;
		}
		SubscriptionEmail emailType = null;
		if (webhookEvent == WebhookEvent.PAYMENT_SALE_COMPLETED || webhookEvent == WebhookEvent.BILLING_SUBSCRIPTION_REACTIVATED) {
			duplicateTenantData(tenant);
			tenant.setIsActive(Boolean.TRUE);
			tenantSubscription.setExpiryDate(DateUtil.addDays(new Date(), 30));
			emailType = SubscriptionEmail.EMAIL_TENANT_ACTIVE;
		} else if (webhookEvent == WebhookEvent.PAYMENT_SALE_DENIED || webhookEvent == WebhookEvent.BILLING_SUBSCRIPTION_SUSPENDED
				|| webhookEvent == WebhookEvent.BILLING_SUBSCRIPTION_CANCELLED) {
			tenant.setIsActive(Boolean.FALSE);
			tenantSubscription.setExpiryDate(null);
			emailType = SubscriptionEmail.EMAIL_TENANT_NOT_ACTIVE;
		} else {
			logger.info("(No Action)");
			logger.info("Webhook Action: End");
			return;
		}

		tenantSubscriptionService.updateTenantSubscription(tenantSubscription);
		tenantService.updateTenantExcluded(tenant);
		sendSubscriptionEmail(tenant, tenantSubscription, emailType);//sending email after updating data, in case of failure
		logger.info("Webhook Action: End");
	}

	/**
	 * Sending Email to Tenant regarding the state of their subscription.
	 * 
	 * @param tenant
	 * @param tenantSubscription : nullable
	 * @param type: type of the email
	 */
	private void sendSubscriptionEmail(SecTenant tenant, BrdTenantSubscription tenantSubscription, SubscriptionEmail subscriptionEmail) {

		Map<String, String> templateValues = new HashMap<>();

		if (subscriptionEmail == SubscriptionEmail.EMAIL_TENANT_EXECUTE) {
			templateValues.put("info",
					"Your Subscription has been successfully executed on Paypal, You can log in After setup phase & when we receive the payment by Paypal.");
		} else if (subscriptionEmail == SubscriptionEmail.EMAIL_TENANT_ACTIVE) {
			templateValues.put("info",
					"Your Subscription to AccuLab is Active until: " + DateUtil.formatDBDatetime(tenantSubscription.getExpiryDate()));
		} else if (subscriptionEmail == SubscriptionEmail.EMAIL_TENANT_NOT_ACTIVE) {
			templateValues.put("info",
					"Your Subscription to AccuLab is Not Active Due to Subscription Cancellation or Denial of Payment.");
		}

		Email email = new Email("email-subscription-new", tenant.getName(), tenant.getEmail(), templateValues);
		emailUtil.sendMailTemplate(email);

	}

	//TODO: turn off auditing ?
	public void duplicateTenantData(SecTenant tenant) {
		//check if tenant has data
		List<SearchCriterion> tenantFilter = Arrays.asList(
				new SearchCriterion("tenantId", tenant.getRid(), FilterOperator.eq));
		if (!CollectionUtil.isCollectionEmpty(tenantMessageService.findTenantMessagesExcluded(tenantFilter))) {
			return;
		}

		List<SearchCriterion> defaultTenantFilter = Arrays.asList(
				new SearchCriterion("tenantId", SecurityUtil.DEFAULT_TENANT, FilterOperator.eq));
		//Fetching each data before its own logic's block sometimes causes the @Filter of hibernate to fire
		//thats why I am fetching everything first then inserting
		List<ComTenantMessage> defaultMessages = tenantMessageService.findTenantMessagesExcluded(defaultTenantFilter);
		List<SecGroupRole> defaultGroupsRoles = groupRoleService.findGroupRolesExcluded(defaultTenantFilter, "secGroup", "secRole");
		List<SecRoleRight> defaultRoleRights = roleRightService.findRoleRightsExcluded(defaultTenantFilter, "secRole", "secRight");
		List<ComTenantLanguage> tenantLanguages = tenantLanguageService.findTenantLanguagesExcluded(tenantFilter,
				new Sort(Direction.ASC, "rid"), "comLanguage");
		List<LabUnit> defaultUnits = unitService.findUnitsExcluded(defaultTenantFilter);
		//This will joins classifications since the @OneToOne is not fixed yet,Sorting DESC so we can insert 
		List<BillClassification> defaultClassifications = classificationService.getClassificationsExcluded(defaultTenantFilter,
				new Sort(new Order(Direction.DESC, "parentClassification")), "parentClassification", "section");
		List<Class<?>> defaultLkps = lkpService.getLkpsByScope(BaseAuditableTenantedEntity.class);
		Map<Class<?>, List<BaseEntity>> lkpData = new HashMap<>();
		for (Class<?> clazz : defaultLkps) {
			lkpData.put(clazz, findAnyLkp(defaultTenantFilter, clazz));
		}

		//Duplicate lkp data
		Map<String, Object> result = duplicateLkps(tenant.getRid(), defaultLkps, lkpData);
		LkpGender gender = (LkpGender) result.get("gender");
		LkpUserStatus userStatus = (LkpUserStatus) result.get("userStatus");
		//Duplicate groups,roles,groupRoles and roleRights data
		duplicateSecurities(tenant.getRid(), defaultGroupsRoles, defaultRoleRights);
		//Create tenant admin user
		createTenantAdmin(tenant, tenantLanguages, gender, userStatus);
		//Duplicate BillClassification & LabSection
		duplicateClassificationsSections(tenant.getRid(), defaultClassifications);
		//Duplicate tenant messages data
		List<ComTenantMessage> duplicatedMsgs = new ArrayList<>();
		for (ComTenantMessage ctm : defaultMessages) {
			ComTenantMessage msg = new ComTenantMessage();
			copyData(ctm, msg, tenant.getRid());
			duplicatedMsgs.add(msg);
		}
		ReflectionUtil.getRepository(ComTenantMessage.class.getSimpleName()).save(duplicatedMsgs);
		//Duplicate lab units data
		List<LabUnit> duplicatedUnits = new ArrayList<>();
		for (LabUnit lu : defaultUnits) {
			LabUnit unit = new LabUnit();
			copyData(lu, unit, tenant.getRid());
			duplicatedUnits.add(unit);
		}
		ReflectionUtil.getRepository(LabUnit.class.getSimpleName()).save(duplicatedUnits);
	}

	public void duplicateClassificationsSections(Long tenantRid, List<BillClassification> defaultClassifications) {
		Map<Long, BillClassification> duplicatedParentsMap = new HashMap<>();
		Map<Long, LabSection> duplicatedSectionsMap = new HashMap<>();
		List<BillClassification> duplicatedClassifications = new ArrayList<>();
		//Save all parents with their sections, then save the none parent with their sections
		//check if sections are not duplicated
		for (BillClassification bc : defaultClassifications) {
			LabSection section = new LabSection();
			if (bc.getSection() != null) {
				if (duplicatedSectionsMap.containsKey(bc.getSection().getRid())) {
					section = duplicatedSectionsMap.get(bc.getSection().getRid());
				} else {
					copyData(bc.getSection(), section, tenantRid);
					section.setClassification(null);
					section.setLabSectionBranches(new ArrayList<>());
					section.setTestDefinitionList(new ArrayList<>());
					section = ReflectionUtil.getRepository(LabSection.class.getSimpleName()).save(section);
					duplicatedSectionsMap.put(bc.getSection().getRid(), section);
				}
			}

			BillClassification classification = new BillClassification();
			copyData(bc, classification, tenantRid);
			classification.setBillMasterItems(new ArrayList<>());

			// section can be null,checking for rid so we dont connect to an object without rid(spring error)
			if (section != null && section.getRid() != null) {
				classification.setSection(section);
			}
			if (bc.getParentClassification() == null) {
				classification = ReflectionUtil.getRepository(BillClassification.class.getSimpleName()).save(classification);
				duplicatedParentsMap.put(bc.getRid(), classification);
			} else {
				classification.setParentClassification(duplicatedParentsMap.get(bc.getParentClassification().getRid()));
				duplicatedClassifications.add(classification);
			}
		}
		ReflectionUtil.getRepository(BillClassification.class.getSimpleName()).save(duplicatedClassifications);
	}

	public void duplicateSecurities(Long tenantRid, List<SecGroupRole> defaultGroupsRoles, List<SecRoleRight> defaultRoleRights) {
		Map<Long, SecGroup> duplicatedGroupsMap = new HashMap<>();
		Map<Long, SecRole> duplicatedRolesMap = new HashMap<>();
		List<SecGroupRole> duplicatedGroupRoles = new ArrayList<>();
		List<SecRoleRight> duplicatedRoleRights = new ArrayList<>();

		// We are creating the SecGroupRole first then setting its group and roles.
		// Using Map so we if the role is set multiple times we dont recreate it.
		for (SecGroupRole groupRole : defaultGroupsRoles) {
			SecGroup g = null;
			SecGroup groupInGroupRole = groupRole.getSecGroup();
			if (duplicatedGroupsMap.containsKey(groupInGroupRole.getRid())) {
				g = duplicatedGroupsMap.get(groupInGroupRole.getRid());
			} else {
				g = new SecGroup();
				copyData(groupInGroupRole, g, tenantRid);
				g.setSecGroupRoles(new ArrayList<>());//emptying the bi-directional reference so we don't reference the same ones
				g.setSecGroupUsers(new ArrayList<>());
				g = ReflectionUtil.getRepository(SecGroup.class.getSimpleName()).save(g);
				duplicatedGroupsMap.put(groupInGroupRole.getRid(), g);
			}
			SecRole r = duplicateRole(groupRole.getSecRole(), tenantRid, duplicatedRolesMap);
			SecGroupRole gr = new SecGroupRole();
			copyData(groupRole, gr, tenantRid);
			gr.setSecRole(r);
			gr.setSecGroup(g);
			duplicatedGroupRoles.add(gr);
		}

		for (SecRoleRight roleRight : defaultRoleRights) {
			SecRole r = duplicateRole(roleRight.getSecRole(), tenantRid, duplicatedRolesMap);
			SecRoleRight rr = new SecRoleRight();
			copyData(roleRight, rr, tenantRid);
			rr.setSecRole(r);
			duplicatedRoleRights.add(rr);
		}
		ReflectionUtil.getRepository(SecGroupRole.class.getSimpleName()).save(duplicatedGroupRoles);
		ReflectionUtil.getRepository(SecRoleRight.class.getSimpleName()).save(duplicatedRoleRights);
	}

	public Map<String, Object> duplicateLkps(Long tenantRid, List<Class<?>> defaultLkps, Map<Class<?>, List<BaseEntity>> lkpData) {
		Map<String, Object> map = new HashMap<>();
		List<BaseEntity> duplicatedLkps = new ArrayList<>();
		for (Class<?> clazz : defaultLkps) {
			GenericRepository<BaseEntity> repo = ReflectionUtil.getRepository(clazz.getSimpleName());
			for (BaseEntity be : lkpData.get(clazz)) {
				try {
					BaseAuditableTenantedEntity obj;
					obj = (BaseAuditableTenantedEntity) clazz.newInstance();
					copyData(be, obj, tenantRid);
					duplicatedLkps.add(obj);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					throw new BusinessException(e.getLocalizedMessage(), e.getLocalizedMessage(), ErrorSeverity.ERROR);
				}
			}
			duplicatedLkps = repo.save(duplicatedLkps);
			if (!CollectionUtil.isCollectionEmpty(duplicatedLkps) && duplicatedLkps.get(0) instanceof LkpGender) {
				map.put("gender", duplicatedLkps.get(0));
			} else if (!CollectionUtil.isCollectionEmpty(duplicatedLkps) && duplicatedLkps.get(0) instanceof LkpUserStatus) {
				map.put("userStatus", duplicatedLkps.get(0));
			}
			duplicatedLkps = new ArrayList<>();//reset
		}
		return map;
	}

	public void createTenantAdmin(SecTenant tenant, List<ComTenantLanguage> tenantLanguages, LkpGender gender, LkpUserStatus userStatus) {
		String username = tenant.getCode() + "-admin";
		if (userService.findUserByUsername(username) != null) {
			username = tenant.getCode() + "-admin-" + tenant.getRid();
		}

		SecUser tenantAdminUser = userService.createTenantAdminUser(tenant, username, gender, userStatus, tenantLanguages);

		//Create a role that has all the system rights and assign the tenant admin user to it
		List<SecRight> allRights = rightService.find(new ArrayList<>(), SecRight.class);
		SecRole adminRole = new SecRole();
		TransField roleName = new TransField();
		for (Map.Entry<String, String> entry : tenantAdminUser.getFirstName().entrySet()) {
			roleName.put(entry.getKey(), "Admin Role");
		}
		adminRole.setName(roleName);
		adminRole.setTenantId(tenant.getRid());
		adminRole.setCreatedBy(SecurityUtil.getSystemUser().getRid());
		adminRole = ReflectionUtil.getRepository(SecRole.class.getSimpleName()).save(adminRole);
		List<SecRoleRight> roleRights = new ArrayList<>();
		for (SecRight right : allRights) {
			SecRoleRight srr = new SecRoleRight();
			srr.setSecRight(right);
			srr.setSecRole(adminRole);
			srr.setTenantId(tenant.getRid());
			srr.setCreatedBy(SecurityUtil.getSystemUser().getRid());
			roleRights.add(srr);
		}
		ReflectionUtil.getRepository(SecRoleRight.class.getSimpleName()).save(roleRights);
		SecUserRole sur = new SecUserRole();
		sur.setSecRole(adminRole);
		sur.setSecUser(tenantAdminUser);
		sur.setTenantId(tenant.getRid());
		sur.setCreatedBy(SecurityUtil.getSystemUser().getRid());
		ReflectionUtil.getRepository(SecUserRole.class.getSimpleName()).save(sur);
	}

	/**
	 * Check if role already created in Map otherwise create new one.
	 * 
	 * @param sourceRole
	 * @param tenantRid
	 * @param duplicatedRolesMap
	 * @return SecRole
	 */
	private SecRole duplicateRole(SecRole sourceRole, Long tenantRid, Map<Long, SecRole> duplicatedRolesMap) {
		SecRole r = new SecRole();
		if (duplicatedRolesMap.containsKey(sourceRole.getRid())) {
			r = duplicatedRolesMap.get(sourceRole.getRid());
		} else {
			copyData(sourceRole, r, tenantRid);
			r.setSecGroupRoles(new ArrayList<>());
			r.setSecRoleRights(new HashSet<>());
			r.setSecUserRoles(new ArrayList<>());
			r = ReflectionUtil.getRepository(SecRole.class.getSimpleName()).save(r);
			duplicatedRolesMap.put(sourceRole.getRid(), r);
		}
		return r;
	}

	/**
	 * Copy properties, set tenant Id , set UpdateBy so we dont throw exception.
	 * 
	 * @param source
	 * @param target
	 * @param tenantId
	 */
	private void copyData(BaseEntity source, BaseAuditableTenantedEntity target, Long tenantId) {
		BeanUtils.copyProperties(source, target, "rid");
		target.setTenantId(tenantId);
		target.setUpdatedBy(SecurityUtil.getSystemUser().getRid());// since the webhook updates the tenant and no user is set there
	}

	//DEV PURPOSES
	public void deleteTenantData(SecTenant tenant, Boolean isPurge) {

		if (tenant.getRid().equals(SecurityUtil.DEFAULT_TENANT)) {
			return;
		}
		List<SearchCriterion> tenantFilter = Arrays.asList(
				new SearchCriterion("tenantId", new Long(tenant.getRid()), FilterOperator.eq));

		List<ComTenantMessage> tenantMessages = tenantMessageService.findTenantMessagesExcluded(tenantFilter);
		ReflectionUtil.getRepository(ComTenantMessage.class.getSimpleName()).delete(tenantMessages);

		List<SecGroupRole> tenantGroupsRoles = groupRoleService.findGroupRolesExcluded(tenantFilter);
		List<SecRoleRight> tenantRoleRights = roleRightService.findRoleRightsExcluded(tenantFilter);
		List<SecUserRole> tenantUserRoles = userRoleService.findUserRolesExcluded(tenantFilter);
		List<SecGroup> tenantGroups = groupService.findGroupsExcluded(tenantFilter);
		List<SecRole> tenantRoles = roleService.findRolesExcluded(tenantFilter);

		ReflectionUtil.getRepository(SecGroupRole.class.getSimpleName()).delete(tenantGroupsRoles);
		ReflectionUtil.getRepository(SecRoleRight.class.getSimpleName()).delete(tenantRoleRights);
		ReflectionUtil.getRepository(SecUserRole.class.getSimpleName()).delete(tenantUserRoles);
		ReflectionUtil.getRepository(SecGroup.class.getSimpleName()).delete(tenantGroups);
		ReflectionUtil.getRepository(SecRole.class.getSimpleName()).delete(tenantRoles);
		SecUser tenantAdminUser = userService.findUserByUsername(tenant.getCode() + "-admin");//excluded service
		if (tenantAdminUser != null) {
			ReflectionUtil.getRepository(SecUser.class.getSimpleName()).delete(tenantAdminUser);
		}
		List<LabUnit> defaultUnits = unitService.findUnitsExcluded(tenantFilter);
		ReflectionUtil.getRepository(LabUnit.class.getSimpleName()).delete(defaultUnits);

		List<BillClassification> tenantClassifications = classificationService.getClassificationsExcluded(tenantFilter, null,
				"parentClassification", "section");
		List<LabSection> tenantSections = tenantClassifications	.stream().filter(bc -> bc.getSection() != null)
																.map(BillClassification::getSection).distinct()
																.collect(Collectors.toList());
		List<BillClassification> tenantParentClassifications = tenantClassifications.stream()
																					.filter(bc -> bc.getParentClassification() == null)
																					.collect(Collectors.toList());
		ReflectionUtil.getRepository(LabSection.class.getSimpleName()).delete(tenantSections);
		ReflectionUtil.getRepository(BillClassification.class.getSimpleName()).delete(tenantParentClassifications);
		ReflectionUtil.getRepository(BillClassification.class.getSimpleName()).delete(tenantClassifications);

		if (isPurge) {
			List<SearchCriterion> tenantRidFilter = Arrays.asList(
					new SearchCriterion("tenant.rid", new Long(tenant.getRid()), FilterOperator.eq));
			List<ComTenantLanguage> tenantLanguages = tenantLanguageService.findTenantLanguagesExcluded(tenantFilter, null);
			List<BrdTenantPlanDetail> tenantPlanDetails = tenantPlanDetailService.findTenantPlanDetails(tenantRidFilter, null, "tenant");
			List<BrdTenantSubscription> tenantSubsriptions = tenantSubscriptionService.findTenantSubscriptions(tenantRidFilter, null,
					"tenant");
			List<LabBranch> branches = branchService.findBranchesExcluded(tenantFilter, null);
			List<SysSerial> serials = serialService.findSerialsExcluded(tenantFilter, null);

			for (LabBranch branch : branches) {
				//				BillBalanceTransaction balanceDrawer = balanceService.getBalanceHolderExcluded(BalanceTransactionType.LAB_CASH_DRAWER, branch.getRid(),
				//						tenant.getRid());
				//				BillBalanceTransaction balanceSales = balanceService.getBalanceHolderExcluded(BalanceTransactionType.LAB_SALES, branch.getRid(),
				//						tenant.getRid());
				//				ReflectionUtil.getRepository(BillBalanceTransaction.class.getSimpleName()).delete(balanceDrawer);
				//				ReflectionUtil.getRepository(BillBalanceTransaction.class.getSimpleName()).delete(balanceSales);
				branchSeparationFactorService.deleteAllByBranch(branch.getRid());
			}
			ReflectionUtil.getRepository(SysSerial.class.getSimpleName()).delete(serials);
			ReflectionUtil.getRepository(LabBranch.class.getSimpleName()).delete(branches);
			ReflectionUtil.getRepository(ComTenantLanguage.class.getSimpleName()).delete(tenantLanguages);
			ReflectionUtil.getRepository(BrdTenantPlanDetail.class.getSimpleName()).delete(tenantPlanDetails);
			ReflectionUtil.getRepository(BrdTenantSubscription.class.getSimpleName()).delete(tenantSubsriptions);
			ReflectionUtil.getRepository(SecTenant.class.getSimpleName()).delete(tenant);
		}
		List<Class<?>> defaultLkps = lkpService.getLkpsByScope(BaseAuditableTenantedEntity.class);

		for (Class<?> clazz : defaultLkps) {
			GenericRepository<BaseEntity> repo = ReflectionUtil.getRepository(clazz.getSimpleName());
			List<BaseEntity> tenantLkps = findAnyLkp(tenantFilter, clazz);
			repo.delete(tenantLkps);
		}

	}

	//UNDER TESTING
	@InterceptorFree
	public void createFullTenantData() {
		List<SearchCriterion> defaultTenantFilter = Arrays.asList(
				new SearchCriterion("tenantId", SecurityUtil.DEFAULT_TENANT, FilterOperator.eq));
		SecTenant tenant = tenantService.findById(SecurityUtil.getCurrentUser().getTenantId());
		ComLanguage english = languageService.findOne(Arrays.asList(new SearchCriterion("locale", "en_us", FilterOperator.eq)),
				ComLanguage.class);
		ComTenantLanguage tenantLanguage = new ComTenantLanguage();
		tenantLanguage.setComLanguage(english);
		tenantLanguage.setIsPrimary(Boolean.TRUE);
		tenantLanguageService.createTenantLanguages(Arrays.asList(tenantLanguage));

		TransField tf = new TransField();
		tf.put("en_us", "Main Branch");
		LabBranch branch = new LabBranch();
		branch.setName(tf);
		branch.setAddress(tf);
		branch.setCode(tenant.getCode() + " branch");
		branch.setPhoneNo(tenant.getPhoneNo());
		branch.setMobilePattern("+999#########");
		branch.setCountry(tenant.getCountry());
		branch.setCity(tenant.getCity());
		branch.setIsActive(Boolean.TRUE);
		branch = branchService.createBranch(Arrays.asList(branch)).get(0);

		List<LkpSerialType> serialTypes = findAnyLkp(new ArrayList<>(), LkpSerialType.class);
		LkpSerialFormat serialFormat = (LkpSerialFormat) findAnyLkp(new ArrayList<>(), LkpSerialFormat.class).get(0);
		List<SysSerial> newTenantSerials = new ArrayList<>();
		for (LkpSerialType st : serialTypes) {
			SysSerial s = new SysSerial();
			s.setCurrentValue(0L);
			s.setDelimiter("-");
			s.setSerialType(st);
			s.setSerialFormat(serialFormat);
			newTenantSerials.add(s);
		}
		serialService.createSerial(newTenantSerials);
		BrdPlan plan = planService.findById(2L);
		tenantSubscriptionService.createTenantSubscription(tenant, DateUtil.addDays(new Date(), 365), plan);// date is null  till the payment arrives
		List<BrdPlanField> planFields = planFieldService.find(
				Arrays.asList(new SearchCriterion("rid", 2L, FilterOperator.eq, JunctionOperator.Or),
						new SearchCriterion("rid", 3L, FilterOperator.eq, JunctionOperator.Or)),
				BrdPlanField.class);
		List<BrdTenantPlanDetail> newTenantPlanFields = new ArrayList<>();
		for (BrdPlanField planField : planFields) {

			BrdTenantPlanDetail tpd = new BrdTenantPlanDetail();
			tpd.setAmount(new BigDecimal("1000000"));
			tpd.setCurrent(BigDecimal.ZERO);
			tpd.setPlanField(planField);
			tpd.setTenant(tenant);
			newTenantPlanFields.add(tpd);
		}
		tenantPlanDetailService.createTenantPlanDetails(newTenantPlanFields);
		entityManager.unwrap(Session.class).disableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
		duplicateTenantData(tenant);
	}

	@SuppressWarnings("unchecked")
	private <T extends BaseEntity> List<T> findAnyLkp(List<SearchCriterion> searchCriterionList, Class<?> clazz) {
		Class<BaseEntity> entityClass = (Class<BaseEntity>) clazz;
		return (List<T>) ReflectionUtil	.getRepository(entityClass.getSimpleName())
										.find(searchCriterionList, entityClass);
	}

}
