package com.optimiza.ehope.web.bill.billPatientTransaction.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.service.BillPatientTransactionService;
import com.optimiza.ehope.lis.wrapper.PaymentInformation;
import com.optimiza.ehope.lis.wrapper.TestPricingWrapper;

@RestController
@RequestMapping("/services")
public class BillPatientTransactionController {

	@Autowired
	private BillPatientTransactionService patientTransactionService;

	@RequestMapping(value = "/getTestsPricing.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getTestsPricing(@RequestBody TestPricingWrapper testPricingWrapper) {
		return new ResponseEntity<Map<String, Object>>(
				patientTransactionService.getTestsPricing(testPricingWrapper.getPatientVisit().getRid(), testPricingWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestsCoverageDetail.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<PaymentInformation>> getTestsCoverageDetail(@RequestBody Long visitRid) {
		return new ResponseEntity<List<PaymentInformation>>(patientTransactionService.getTestsCoverageDetail(visitRid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/payment.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> payment(@RequestBody TestPricingWrapper testPricingWrapper) {
		patientTransactionService.payment(testPricingWrapper);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/partialPayment.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> partialPayment(@RequestBody TestPricingWrapper testPricingWrapper) {
		patientTransactionService.partialPayment(testPricingWrapper);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestsPricingNoVisit.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getTestsPricingNoVisit(@RequestBody TestPricingWrapper testPricingWrapper) {
		return new ResponseEntity<Map<String, Object>>(patientTransactionService.getTestsPricingNoVisit(testPricingWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/skipPayment.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> skipPayment(@RequestBody TestPricingWrapper testPricingWrapper) {
		patientTransactionService.skipPayment(testPricingWrapper);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/refundPayment.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> refundPayment(@RequestBody TestPricingWrapper testPricingWrapper) {
		patientTransactionService.refundPayment(testPricingWrapper.getPatientVisit().getRid(), testPricingWrapper.getTestPaymentList());
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getRefundInfo.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getRefundInfo(@RequestBody Long visitRid) {
		return new ResponseEntity<Map<String, Object>>(patientTransactionService.getRefundInfo(visitRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/getPreviousPaymentDialogData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getPreviousPaymentDialogData(@RequestBody Long visitRid) {
		return new ResponseEntity<Map<String, Object>>(patientTransactionService.getPreviousPaymentDialogData(visitRid), HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/areTestsWithoutInsPricing.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> areTestsWithoutInsPricing(@RequestBody Map<String, Object> map) {
		patientTransactionService.areTestsWithoutInsPricing((List<Long>) map.get("testsRid"),
				Long.valueOf((String) map.get("providerPlanRid")));
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}
