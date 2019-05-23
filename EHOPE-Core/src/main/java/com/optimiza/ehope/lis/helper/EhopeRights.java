package com.optimiza.ehope.lis.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EhopeRights {

	//Views
	public final static String VIEW_TEST_DIRECTORY = "VIEW_TEST_DIRECTORY";
	public final static String VIEW_TEST_SETUP = "VIEW_TEST_SETUP";
	public final static String VIEW_TEST_DEFINITION = "VIEW_TEST_DEFINITION";

	public final static String VIEW_PATIENT_LOOKUP = "VIEW_PATIENT_LOOKUP";
	public final static String VIEW_PATIENT_PROFILE = "VIEW_PATIENT_PROFILE";
	public final static String VIEW_PATIENT_INSURANCE = "VIEW_PATIENT_INSURANCE";
	public final static String VIEW_ORDERS = "VIEW_ORDERS";

	public final static String VIEW_TENANT_MESSAGES = "VIEW_TENANT_MESSAGES";

	public final static String VIEW_INSURANCE_NETWORK = "VIEW_INSURANCE_NETWORK";
	public final static String VIEW_INSURANCE_PROVIDER = "VIEW_INSURANCE_PROVIDER";

	public final static String VIEW_BILL_CLASSIFICATION = "VIEW_BILL_CLASSIFICATION";
	public final static String VIEW_BILL_MASTER_ITEM = "VIEW_BILL_MASTER_ITEM";
	public final static String VIEW_BILL_PRICING = "VIEW_BILL_PRICING";
	public final static String VIEW_BILL_PRICE_LIST = "VIEW_BILL_PRICE_LIST";
	public final static String VIEW_BILL_PRICE_LIST_DETAILS = "VIEW_BILL_PRICE_LIST_DETAILS";

	public final static String VIEW_LAB_FACTORS = "VIEW_LAB_FACTORS";
	public final static String VIEW_REQUEST_FORM = "VIEW_REQUEST_FORM";

	public final static String VIEW_ORDER_MANAGEMENT = "VIEW_ORDER_MANAGEMENT";
	public final static String VIEW_DOCTOR = "VIEW_DOCTOR";
	public final static String VIEW_WORKBENCH = "VIEW_WORKBENCH";

	//Accounting > Outstanding Balances
	public final static String VIEW_OUTSTANDING_BALANCES = "VIEW_OUTSTANDING_BALANCES";
	public final static String VIEW_BRANCH = "VIEW_BRANCH";
	public final static String VIEW_SERIAL = "VIEW_SERIAL";

	public final static String VIEW_DASHBOARD = "VIEW_DASHBOARD";
	public final static String VIEW_DATA_IMPORT = "VIEW_DATA_IMPORT";

	public final static String VIEW_TEST_GROUP = "VIEW_TEST_GROUP";

	//ComTenantMessageService	
	public final static String UPD_TENANT_MESSAGE = "UPD_TENANT_MESSAGE";

	//InsNetworkService
	public final static String ADD_INS_NETWORK = "ADD_INS_NETWORK";
	public final static String DEL_INS_NETWORK = "DEL_INS_NETWORK";
	public final static String UPD_INS_NETWORK = "UPD_INS_NETWORK";

	//InsProviderService
	public final static String ADD_INS_PROVIDER = "ADD_INS_PROVIDER";
	public final static String UPD_INS_PROVIDER = "UPD_INS_PROVIDER";
	public final static String ACTIVATE_INS_PROVIDER = "ACTIVATE_INS_PROVIDER";
	public final static String DEACTIVATE_INS_PROVIDER = "DEACTIVATE_INS_PROVIDER";

	//TestDefinitionService
	public final static String ADD_TEST_DEFINITION = "ADD_TEST_DEFINITION";
	public final static String UPD_TEST_DEFINITION = "UPD_TEST_DEFINITION";
	public final static String ACTIVATE_TEST_DEFINITION = "ACTIVATE_TEST_DEFINITION";
	public final static String DEACTIVATE_TEST_DEFINITION = "DEACTIVATE_TEST_DEFINITION";

	//BillClassificationService
	public final static String ADD_BILL_CLASSIFICATION = "ADD_BILL_CLASSIFICATION";
	public final static String UPD_BILL_CLASSIFICATION = "UPD_BILL_CLASSIFICATION";
	public final static String ACTIVATE_BILL_CLASSIFICATION = "ACTIVATE_BILL_CLASSIFICATION";
	public final static String DEACTIVATE_BILL_CLASSIFICATION = "DEACTIVATE_BILL_CLASSIFICATION";

	//BillMasterItemService
	public final static String ADD_BILL_MASTER_ITEM = "ADD_BILL_MASTER_ITEM";
	public final static String UPD_BILL_MASTER_ITEM = "UPD_BILL_MASTER_ITEM";
	public final static String ACTIVATE_BILL_MASTER_ITEM = "ACTIVATE_BILL_MASTER_ITEM";
	public final static String DEACTIVATE_BILL_MASTER_ITEM = "DEACTIVATE_BILL_MASTER_ITEM";

	//BillPricingService
	public final static String ADD_BILL_PRICING = "ADD_BILL_PRICING";
	public final static String UPD_BILL_PRICING = "UPD_BILL_PRICING";

	//InsProviderService
	public final static String ADD_BILL_PRICE_LIST = "ADD_BILL_PRICE_LIST";
	public final static String UPD_BILL_PRICE_LIST = "UPD_BILL_PRICE_LIST";
	public final static String DEL_BILL_PRICE_LIST = "DEL_BILL_PRICE_LIST";

	//PatientProfile
	public final static String ADD_PATIENT_PROFILE = "ADD_PATIENT_PROFILE";
	public final static String UPD_PATIENT_PROFILE = "UPD_PATIENT_PROFILE";
	public final static String ACTIVATE_PATIENT_PROFILE = "ACTIVATE_PATIENT_PROFILE";
	public final static String DEACTIVATE_PATIENT_PROFILE = "DEACTIVATE_PATIENT_PROFILE";
	public final static String MERGE_PATIENT_PROFILE = "MERGE_PATIENT_PROFILE";

	//PatientInsurance
	public final static String ADD_PATIENT_INSURANCE = "ADD_PATIENT_INSURANCE";
	public final static String UPD_PATIENT_INSURANCE = "UPD_PATIENT_INSURANCE";
	public final static String ACTIVATE_PATIENT_INSURANCE = "ACTIVATE_PATIENT_INSURANCE";
	public final static String DEACTIVATE_PATIENT_INSURANCE = "DEACTIVATE_PATIENT_INSURANCE";

	//Order
	public final static String ADD_ORDER = "ADD_ORDER";
	public final static String UPD_ORDER = "UPD_ORDER";

	//InsProviderService
	public final static String UPD_LAB_FACTORS = "UPD_LAB_FACTORS";
	public final static String DEL_LAB_FACTORS = "DEL_LAB_FACTORS";

	//LabTestActual
	public final static String ADD_TEST_ACTUAL = "ADD_TEST_ACTUAL";

	//TestQuestion
	public final static String VIEW_TEST_QUESTION = "VIEW_TEST_QUESTION";

	//LabSample
	public final static String ADD_SAMPLE = "ADD_SAMPLE";

	//TestActualResult
	public final static String UPD_TEST_ACTUAL_RESULT = "UPD_TEST_ACTUAL_RESULT";

	//TestRequestForm
	public final static String ADD_REQUEST_FORM = "ADD_REQUEST_FORM";
	public final static String UPD_REQUEST_FORM = "UPD_REQUEST_FORM";
	public final static String ACTIVATE_REQUEST_FORM = "ACTIVATE_REQUEST_FORM";
	public final static String DEACTIVATE_REQUEST_FORM = "DEACTIVATE_REQUEST_FORM";

	//OperationStatus
	//This gets concatenated with the enum OperationStatus.java
	//ex: "REQUESTED" + "_OPERATION_STATUS"
	public final static String _OPERATION_STATUS = "_OPERATION_STATUS";
	public final static String CANCEL_ANY_OPERATION_STATUS = "CANCEL_ANY_OPERATION_STATUS";

	//Reports
	public final static String GENERATE_RESULTS_REPORT = "GENERATE_RESULTS_REPORT";

	public final static String ADD_PAYMENT = "ADD_PAYMENT";
	public final static String SKIP_PAYMENT = "SKIP_PAYMENT";
	public final static String REFUND_PAYMENT = "REFUND_PAYMENT";

	//Doctors
	public final static String ADD_DOCTOR = "ADD_DOCTOR";
	public final static String UPD_DOCTOR = "UPD_DOCTOR";
	public final static String DEL_DOCTOR = "DEL_DOCTOR";

	//Workbenches
	public final static String ADD_WORKBENCH = "ADD_WORKBENCH";
	public final static String UPD_WORKBENCH = "UPD_WORKBENCH";
	public final static String DEL_WORKBENCH = "DEL_WORKBENCH";

	//LabBranch
	public final static String ADD_BRANCH = "ADD_BRANCH";
	public final static String UPD_BRANCH = "UPD_BRANCH";
	public final static String DEL_BRANCH = "DEL_BRANCH";
	public final static String ACTIVATE_BRANCH = "ACTIVATE_BRANCH";
	public final static String DEACTIVATE_BRANCH = "DEACTIVATE_BRANCH";

	//Serial
	public final static String ADD_SERIAL = "ADD_SERIAL";
	public final static String UPD_SERIAL = "UPD_SERIAL";

	//Section
	public final static String VIEW_SECTION = "VIEW_SECTION";
	public final static String ADD_SECTION = "ADD_SECTION";
	public final static String UPD_SECTION = "UPD_SECTION";
	public final static String DEL_SECTION = "DEL_SECTION";

	//Lab Unit
	public final static String VIEW_LAB_UNIT = "VIEW_LAB_UNIT";
	public final static String ADD_LAB_UNIT = "ADD_LAB_UNIT";
	public final static String UPD_LAB_UNIT = "UPD_LAB_UNIT";
	public final static String DEL_LAB_UNIT = "DEL_LAB_UNIT";

	//Organism
	public final static String VIEW_ORGANISM = "VIEW_ORGANISM";
	public final static String ADD_ORGANISM = "ADD_ORGANISM";
	public final static String UPD_ORGANISM = "UPD_ORGANISM";
	public final static String DEL_ORGANISM = "DEL_ORGANISM";

	//Anti-Microbial
	public final static String VIEW_ANTI_MICROBIAL = "VIEW_ANTI_MICROBIAL";
	public final static String ADD_ANTI_MICROBIAL = "ADD_ANTI_MICROBIAL";
	public final static String UPD_ANTI_MICROBIAL = "UPD_ANTI_MICROBIAL";
	public final static String DEL_ANTI_MICROBIAL = "DEL_ANTI_MICROBIAL";

	//Reports
	public final static String VIEW_DAILY_REPORTS = "VIEW_DAILY_REPORTS";
	public final static String VIEW_CLAIM_REPORT = "VIEW_CLAIM_REPORT";
	public final static String VIEW_DAILY_CASH_REPORT = "VIEW_DAILY_CASH_REPORT";
	public final static String VIEW_DAILY_INCOME_REPORT = "VIEW_DAILY_INCOME_REPORT";
	public final static String VIEW_DAILY_CREDIT_PAYMENT_REPORT = "VIEW_DAILY_CREDIT_PAYMENT_REPORT";
	public final static String VIEW_REFERRAL_OUT_REPORT = "VIEW_REFERRAL_OUT_REPORT";

	//Unused reports rights, must need them later
	public final static String VIEW_DAILY_PAYMENT_REPORT = "VIEW_DAILY_PAYMENT_REPORT";
	public final static String VIEW_DAILY_CHEQUE_REPORT = "VIEW_DAILY_CHEQUE_REPORT";
	public final static String VIEW_DAILY_VISA_REPORT = "VIEW_DAILY_VISA_REPORT";
	public final static String VIEW_DAILY_CREDIT_REPORT = "VIEW_DAILY_CREDIT_REPORT";

	//TestGroupService
	public final static String ADD_TEST_GROUP = "ADD_TEST_GROUP";
	public final static String DEL_TEST_GROUP = "DEL_TEST_GROUP";
	public final static String UPD_TEST_GROUP = "UPD_TEST_GROUP";

	//TestGroupDetailService
	public final static String ADD_TEST_GROUP_DETAIL = "ADD_TEST_GROUP_DETAIL";
	public final static String DEL_TEST_GROUP_DETAIL = "DEL_TEST_GROUP_DETAIL";
	public final static String UPD_TEST_GROUP_DETAIL = "UPD_TEST_GROUP_DETAIL";

	public final static String VIEW_ARTIFACT = "VIEW_ARTIFACT";
	public final static String VIEW_ARTIFACT_FINALIZED = "VIEW_ARTIFACT_FINALIZED";

	public final static Map<String, String> CODES;
	public final static Map<String, String> REVERSE_CODES;
	static {
		Map<String, String> tempCodes = new HashMap<String, String>();
		tempCodes.put("0", VIEW_TEST_DIRECTORY);
		tempCodes.put("1", VIEW_TEST_SETUP);
		tempCodes.put("2", VIEW_TEST_DEFINITION);
		tempCodes.put("3", VIEW_PATIENT_LOOKUP);
		tempCodes.put("4", VIEW_PATIENT_PROFILE);
		tempCodes.put("5", VIEW_PATIENT_INSURANCE);
		tempCodes.put("6", VIEW_ORDERS);
		tempCodes.put("7", VIEW_TENANT_MESSAGES);
		tempCodes.put("8", VIEW_INSURANCE_NETWORK);
		tempCodes.put("9", VIEW_INSURANCE_PROVIDER);
		tempCodes.put("A", VIEW_BILL_CLASSIFICATION);
		tempCodes.put("B", VIEW_BILL_MASTER_ITEM);
		tempCodes.put("C", VIEW_BILL_PRICING);
		tempCodes.put("D", VIEW_BILL_PRICE_LIST);
		tempCodes.put("E", VIEW_BILL_PRICE_LIST_DETAILS);
		tempCodes.put("F", VIEW_LAB_FACTORS);
		tempCodes.put("10", VIEW_REQUEST_FORM);
		tempCodes.put("11", VIEW_ORDER_MANAGEMENT);
		tempCodes.put("12", VIEW_DOCTOR);
		tempCodes.put("13", VIEW_WORKBENCH);
		tempCodes.put("14", VIEW_OUTSTANDING_BALANCES);
		tempCodes.put("15", VIEW_BRANCH);
		tempCodes.put("16", VIEW_SERIAL);
		tempCodes.put("17", VIEW_DASHBOARD);
		tempCodes.put("18", VIEW_DATA_IMPORT);
		tempCodes.put("19", VIEW_TEST_GROUP);
		tempCodes.put("1A", UPD_TENANT_MESSAGE);
		tempCodes.put("1B", ADD_INS_NETWORK);
		tempCodes.put("1C", DEL_INS_NETWORK);
		tempCodes.put("1D", UPD_INS_NETWORK);
		tempCodes.put("1E", ADD_INS_PROVIDER);

		tempCodes.put("20", UPD_INS_PROVIDER);
		tempCodes.put("21", ACTIVATE_INS_PROVIDER);
		tempCodes.put("22", DEACTIVATE_INS_PROVIDER);
		tempCodes.put("23", ADD_TEST_DEFINITION);
		tempCodes.put("24", UPD_TEST_DEFINITION);
		tempCodes.put("25", ACTIVATE_TEST_DEFINITION);
		tempCodes.put("26", DEACTIVATE_TEST_DEFINITION);
		tempCodes.put("27", ADD_BILL_CLASSIFICATION);
		tempCodes.put("28", UPD_BILL_CLASSIFICATION);
		tempCodes.put("29", ACTIVATE_BILL_CLASSIFICATION);
		tempCodes.put("2A", DEACTIVATE_BILL_CLASSIFICATION);
		tempCodes.put("2B", ADD_BILL_MASTER_ITEM);
		tempCodes.put("2C", UPD_BILL_MASTER_ITEM);
		tempCodes.put("2D", ACTIVATE_BILL_MASTER_ITEM);
		tempCodes.put("2E", DEACTIVATE_BILL_MASTER_ITEM);
		tempCodes.put("2F", ADD_BILL_PRICING);
		tempCodes.put("30", UPD_BILL_PRICING);
		tempCodes.put("31", ADD_BILL_PRICE_LIST);
		tempCodes.put("32", UPD_BILL_PRICE_LIST);
		tempCodes.put("33", DEL_BILL_PRICE_LIST);
		tempCodes.put("34", ADD_PATIENT_PROFILE);
		tempCodes.put("35", UPD_PATIENT_PROFILE);
		tempCodes.put("36", ACTIVATE_PATIENT_PROFILE);
		tempCodes.put("37", DEACTIVATE_PATIENT_PROFILE);
		tempCodes.put("38", ADD_PATIENT_INSURANCE);
		tempCodes.put("39", UPD_PATIENT_INSURANCE);
		tempCodes.put("3A", ACTIVATE_PATIENT_INSURANCE);
		tempCodes.put("3B", DEACTIVATE_PATIENT_INSURANCE);
		tempCodes.put("3C", ADD_ORDER);
		tempCodes.put("3D", UPD_LAB_FACTORS);
		tempCodes.put("3E", DEL_LAB_FACTORS);
		tempCodes.put("3F", ADD_TEST_ACTUAL);
		tempCodes.put("40", VIEW_TEST_QUESTION);
		tempCodes.put("41", ADD_SAMPLE);
		tempCodes.put("42", UPD_TEST_ACTUAL_RESULT);
		tempCodes.put("43", ADD_REQUEST_FORM);
		tempCodes.put("44", UPD_REQUEST_FORM);
		tempCodes.put("45", ACTIVATE_REQUEST_FORM);
		tempCodes.put("46", DEACTIVATE_REQUEST_FORM);
		tempCodes.put("47", _OPERATION_STATUS);
		tempCodes.put("48", GENERATE_RESULTS_REPORT);
		tempCodes.put("49", ADD_PAYMENT);
		tempCodes.put("4A", SKIP_PAYMENT);
		tempCodes.put("4B", REFUND_PAYMENT);
		tempCodes.put("4C", ADD_DOCTOR);
		tempCodes.put("4D", UPD_DOCTOR);
		tempCodes.put("4E", DEL_DOCTOR);
		tempCodes.put("4F", ADD_WORKBENCH);
		tempCodes.put("50", UPD_WORKBENCH);
		tempCodes.put("51", DEL_WORKBENCH);
		tempCodes.put("52", ADD_BRANCH);
		tempCodes.put("53", UPD_BRANCH);
		tempCodes.put("54", DEL_BRANCH);
		tempCodes.put("55", ACTIVATE_BRANCH);
		tempCodes.put("56", DEACTIVATE_BRANCH);
		tempCodes.put("57", ADD_SERIAL);
		tempCodes.put("58", UPD_SERIAL);
		tempCodes.put("59", VIEW_SECTION);
		tempCodes.put("5A", ADD_SECTION);
		tempCodes.put("5B", UPD_SECTION);
		tempCodes.put("5C", DEL_SECTION);
		tempCodes.put("5D", VIEW_LAB_UNIT);
		tempCodes.put("5E", ADD_LAB_UNIT);
		tempCodes.put("5F", UPD_LAB_UNIT);
		tempCodes.put("60", DEL_LAB_UNIT);
		tempCodes.put("61", VIEW_ORGANISM);
		tempCodes.put("62", ADD_ORGANISM);
		tempCodes.put("63", UPD_ORGANISM);
		tempCodes.put("64", DEL_ORGANISM);
		tempCodes.put("65", VIEW_ANTI_MICROBIAL);
		tempCodes.put("66", ADD_ANTI_MICROBIAL);
		tempCodes.put("67", UPD_ANTI_MICROBIAL);
		tempCodes.put("68", DEL_ANTI_MICROBIAL);
		tempCodes.put("69", VIEW_DAILY_REPORTS);
		tempCodes.put("6A", VIEW_CLAIM_REPORT);
		tempCodes.put("6B", VIEW_DAILY_CASH_REPORT);
		tempCodes.put("6C", VIEW_DAILY_INCOME_REPORT);
		tempCodes.put("6D", VIEW_DAILY_CREDIT_PAYMENT_REPORT);
		tempCodes.put("6E", VIEW_DAILY_PAYMENT_REPORT);
		tempCodes.put("6F", VIEW_DAILY_CHEQUE_REPORT);
		tempCodes.put("70", VIEW_DAILY_VISA_REPORT);
		tempCodes.put("71", VIEW_DAILY_CREDIT_REPORT);
		tempCodes.put("72", ADD_TEST_GROUP);
		tempCodes.put("73", DEL_TEST_GROUP);
		tempCodes.put("74", UPD_TEST_GROUP);
		tempCodes.put("75", ADD_TEST_GROUP_DETAIL);
		tempCodes.put("76", DEL_TEST_GROUP_DETAIL);
		tempCodes.put("77", UPD_TEST_GROUP_DETAIL);
		CODES = Collections.unmodifiableMap(tempCodes);
		Map<String, String> tempReverseCodes = new HashMap<String, String>();
		for (Entry<String, String> entry : tempCodes.entrySet()) {
			tempReverseCodes.put(entry.getValue(), entry.getKey());
		}
		REVERSE_CODES = Collections.unmodifiableMap(tempReverseCodes);
	}
}
