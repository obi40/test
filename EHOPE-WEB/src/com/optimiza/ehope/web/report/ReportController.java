package com.optimiza.ehope.web.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.HttpUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.common.util.TokenUtil;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.ehope.lis.helper.SMSKey;
import com.optimiza.ehope.lis.model.Doctor;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.service.EmrVisitService;
import com.optimiza.ehope.lis.util.SMSUtil;
import com.optimiza.ehope.lis.wrapper.VisitResultsWrapper;
import com.optimiza.ehope.web.util.ReportUtil;
import com.optimiza.ehope.web.visit.wrapper.ResultsDataWrapper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
@RequestMapping("/services")
public class ReportController {

	@Autowired
	private ComTenantLanguageService comTenantLanguageService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private EmrVisitService emrVisitService;
	private SMSUtil smsUtil;
	@Autowired
	private TokenUtil tokenUtil;
	@Value("${system.website.url}")
	private String serverUrl;

	@RequestMapping(value = "/generateOutstandingBalancesReport.srvc", method = RequestMethod.POST)
	public void generateOutstandingBalancesReport(HttpServletResponse response,
			@RequestBody Map<String, Object> filtersInformation) {
		ReportUtil.createMultipleJasperViews(response, "outstanding_balances",
				reportService.generateOutstandingBalancesReport(filtersInformation));
	}

	@RequestMapping(value = "/generatePatientOutstandingBalancesReport.srvc", method = RequestMethod.POST)
	public void generatePatientOutstandingBalancesReport(HttpServletResponse response,
			@RequestBody Map<String, Object> patientInformation) {
		ReportUtil.createMultipleJasperViews(response, "outstanding_balances",
				reportService.generatePatientOutstandingBalancesReport(patientInformation));
	}

	@RequestMapping(value = "/generateInvoiceReport.srvc", method = RequestMethod.POST)
	public void generateInvoiceReport(HttpServletResponse response,
			@RequestBody Map<String, Object> visitInformation) {
		ReportUtil.createMultipleJasperViews(response, "Invoice", reportService.generateInvoiceReport(visitInformation));
	}

	@RequestMapping(value = "/generateInsuranceInvoiceReport.srvc", method = RequestMethod.POST)
	public void generateInsuranceInvoiceReport(HttpServletResponse response,
			@RequestBody Map<String, Object> visitInformation) {
		ReportUtil.createMultipleJasperViews(response, "insurance_invoice",
				reportService.generateInsuranceInvoiceReport(visitInformation));
	}

	@RequestMapping(value = "/generateVisitResults.srvc", method = RequestMethod.POST)
	public void generateVisitResults(HttpServletResponse response, @RequestBody ResultsDataWrapper visitInformation) {
		ReportUtil.createMultipleJasperViews(response,
				reportService.getAllResultsReports(visitInformation.getVisitRid(), visitInformation.getTestsMap()),
				comTenantLanguageService.getTenantNamePrimary());
	}

	@RequestMapping(value = "/generateVisitResultsEmail.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> generateVisitResultsEmail(@RequestBody ResultsDataWrapper visitInformation) throws JRException {

		Map<String, String> map = visitInformation.getEmailMap();
		String target = map.get("target");//patient or doctor

		EmrVisit visit = emrVisitService.findOne(
				Arrays.asList(new SearchCriterion("rid", visitInformation.getVisitRid(), FilterOperator.eq)),
				EmrVisit.class, "emrPatientInfo", "doctor");
		String email = null;
		String name = null;
		if (target.equals("CUSTOM")) {
			email = map.get("customEmail");
			name = map.get("customEmail");
		} else {
			EmrPatientInfo patient = visit.getEmrPatientInfo();
			if (target.equals("PATIENT") && !visit.getIsEmailNotification() && !patient.getIsEmailNotification()) {
				return null;
			}
			Doctor doctor = visit.getDoctor();
			if (target.equals("PATIENT")) {
				email = patient.getEmail();
				name = patient.getFullName().entrySet().iterator().next().getValue();
			} else if (doctor != null && target.equals("DOCTOR")) {
				email = doctor.getEmail();
				name = doctor.getName().entrySet().iterator().next().getValue();
			}
			if (StringUtil.isEmpty(email)) {
				if (StringUtil.isEmpty(map.get("errorSeverity"))) {
					throw new BusinessException("Patient/Doctor does not have an email", "noEmail", ErrorSeverity.ERROR);
				} else {
					throw new BusinessException("Patient/Doctor does not have an email", "noEmail",
							ErrorSeverity.valueOf(map.get("errorSeverity")));
				}
			}
		}
		List<byte[]> reportBytesList = new ArrayList<>();
		List<VisitResultsWrapper> visitResults = new ArrayList<>();
		visitResults = reportService.getAllResultsReports(visitInformation.getVisitRid(), visitInformation.getTestsMap());
		String namePrimary = comTenantLanguageService.getTenantNamePrimary();
		for (VisitResultsWrapper visitResult : visitResults) {
			Map<String, Object> params = new HashMap<>();
			params.put("wrapper", visitResult);
			params.put("namePrimary", namePrimary);

			JRBeanCollectionDataSource currentJDBean = new JRBeanCollectionDataSource(Arrays.asList(visitResult));
			reportBytesList.add(ReportUtil.createJasperBytesPDF(visitResult.getReportName(), currentJDBean, params));
		}
		emrVisitService.sendVisitResults(name, email, reportBytesList);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/generateVisitResultsSMS.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> generateVisitResultsSMS(@RequestBody ResultsDataWrapper visitInformation) {

		Map<String, String> map = visitInformation.getEmailMap();
		Long visitRid = Long.valueOf(map.get("visitRid"));
		String target = map.get("target");//patient or doctor
		EmrVisit visit = emrVisitService.findOne(Arrays.asList(new SearchCriterion("rid", visitRid, FilterOperator.eq)),
				EmrVisit.class, "emrPatientInfo", "doctor");
		EmrPatientInfo patient = visit.getEmrPatientInfo();
		Doctor doctor = visit.getDoctor();
		Map<String, Object> serviceData = new HashMap<>();
		serviceData.put("serviceUrl", "generateVisitResults");
		serviceData.put("parameters", visitInformation);
		Map<String, Object> tokenData = new HashMap<>();
		tokenData.put("data", serviceData);//this data object will be used to get the custom data from the token
		tokenData.putAll(tokenUtil.generateTokenData(SecurityUtil.getCurrentUser(), new ArrayList<>()));

		String token = tokenUtil.generateToken(tokenData, tokenUtil.DEFAULT_TOKEN_EXPIRATION);
		String url = serverUrl + "/sms?t=" + token;
		url = HttpUtil.shortenUrl(url);
		String mobileNo = null;
		if (target.equals("PATIENT")) {
			mobileNo = patient.getMobileNo();
		} else if (doctor != null && target.equals("DOCTOR")) {
			mobileNo = doctor.getMobileNo();
		}

		if (StringUtil.isEmpty(mobileNo)) {
			throw new BusinessException("Patient/Doctor does not have a mobile", "noMobile", ErrorSeverity.ERROR);
		}
		Map<SMSKey, String> result = new HashMap<>();
		result.put(SMSKey.MOBILE, mobileNo);
		result.put(SMSKey.MESSAGE, url);
		smsUtil.send(result);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/generateDailyCashPaymentsReport.srvc", method = RequestMethod.POST)
	public void generateDailyCashPaymentsReport(HttpServletResponse response, @RequestBody Map<String, Object> cashPaymentsInformation) {
		ReportUtil.createMultipleJasperViews(response, "daily_cash", reportService.getDailyCashPayments(cashPaymentsInformation));
	}

	@RequestMapping(value = "/generateDailyIncomeReport.srvc", method = RequestMethod.POST)
	public void generateDailyIncomeReport(HttpServletResponse response, @RequestBody Map<String, Object> incomeInformation) {
		ReportUtil.createMultipleJasperViews(response, "daily_income", reportService.generateDailyIncomeReport(incomeInformation));
	}

	@RequestMapping(value = "/generateAppointmentCard.srvc", method = RequestMethod.POST)
	public void generateAppointmentCard(HttpServletResponse response, @RequestBody Map<String, Object> patientCardInfo) {
		ReportUtil.createMultipleJasperViews(response, "appointment_card", reportService.generateAppointmentCard(patientCardInfo));
	}

	@RequestMapping(value = "/generateClaimReport.srvc", method = RequestMethod.POST)
	public void generateClaimReport(HttpServletResponse response, @RequestBody Map<String, Object> claimInformation) {
		if (claimInformation.get("isReportSummarized").toString().equals("true")) {
			ReportUtil.createMultipleJasperViews(response, "claim_summary",
					reportService.generateClaimSummarizedReport(claimInformation));
		} else {
			ReportUtil.createMultipleJasperViews(response, "claim_detailed",
					reportService.generateClaimDetailedReport(claimInformation));
		}

	}

	@RequestMapping(value = "/generateDailyCreditPaymentReport.srvc", method = RequestMethod.POST)
	public void generateDailyCreditPaymentReport(HttpServletResponse response, @RequestBody Map<String, Object> dailyCreditInformation) {
		ReportUtil.createMultipleJasperViews(response, "daily_credit",
				reportService.generateDailyCreditPaymentReport(dailyCreditInformation));
	}

	@RequestMapping(value = "/generateReferralOutReport.srvc", method = RequestMethod.POST)
	public void generateReferralOutReport(HttpServletResponse response, @RequestBody Map<String, Object> referralFilters) {
		ReportUtil.createMultipleJasperViews(response, "referral_out", reportService.generateReferralOutReport(referralFilters));
	}
}
