package com.optimiza.ehope.lis.helper;

//This is a reflection of LkpMaster.java
public enum EhopeLkpType {

	ACTION_CODE("ACTION_CODE", "LkpActionCode"),
	AGE_UNIT("AGE_UNIT", "LkpAgeUnit"),
	BLOOD_TYPE("BLOOD_TYPE", "LkpBloodType"),
	BALANCE_TRANSACTION_TYPE("BALANCE_TRANSACTION_TYPE", "LkpBalanceTransactionType"),
	COMPARE_TYPE("COMPARE_TYPE", "LkpCompareType"),
	CONTAINER_TYPE("CONTAINER_TYPE", "LkpContainerType"),
	DEPENDENCY_TYPE("DEPENDENCY_TYPE", "LkpDependencyType"),
	INSURANCE_TYPE("INSURANCE_TYPE", "LkpInsuranceType"),
	OPERATION_STATUS("OPERATION_STATUS", "LkpOperationStatus"),
	TESTING_METHOD("TESTING_METHOD", "LkpTestingMethod"),
	MARITAL_STATUS("MARITAL_STATUS", "LkpMaritalStatus"),
	MESSAGE_TYPE("MESSAGE_TYPE", "LkpMessagesType"),
	PATIENT_STATUS("PATIENT_STATUS", "LkpPatientStatus"),
	PAYMENT_METHOD("PAYMENT_METHOD", "LkpPaymentMethod"),
	PLAN_FIELD_TYPE("PLAN_FIELD_TYPE", "LkpPlanFieldType"),
	QUESTION_TYPE("QUESTION_TYPE", "LkpQuestionType"),
	RACE("RACE", "LkpRace"),
	RANGE_UNIT("RANGE_UNIT", "LkpRangeUnit"),
	RELIGION("RELIGION", "LkpReligion"),
	RESULT_VALUE_TYPE("RESULT_VALUE_TYPE", "LkpResultValueType"),
	SAMPLE_PRIORITY("SAMPLE_PRIORITY", "LkpSamplePriority"),
	SIGNUM("SIGNUM", "LkpSignum"),
	SMS_KEY("SMS_KEY", "LkpSMSKey"),
	SPECIMEN_TEMPERATURE("SPECIMEN_TEMPERATURE", "LkpSpecimenTemperature"),
	SPECIMEN_TYPE("SPECIMEN_TYPE", "LkpSpecimenType"),
	SPECIMEN_STABILITY_UNIT("SPECIMEN_STABILITY_UNIT", "LkpSpecimenStabilityUnit"),
	SERIAL_TYPE("SERIAL_TYPE", "LkpSerialType"),
	SERIAL_FORMAT("SERIAL_FORMAT", "LkpSerialFormat"),
	TEST_ENTRY_TYPE("TEST_ENTRY_TYPE", "LkpTestEntryType"),
	TEST_DESTINATION_TYPE("TEST_DESTINATION_TYPE", "LkpTestDestinationType"),
	UNIT_TYPE("UNIT_TYPE", "LkpUnitType"),
	USER_STATUS("USER_STATUS", "LkpUserStatus"),
	VISIT_STAGE("VISIT_STAGE", "LkpVisitStage"),
	VISIT_STATUS("VISIT_STATUS", "LkpVisitStatus"),
	VISIT_TYPE("VISIT_TYPE", "LkpVisitType"),
	SECTION_TYPE("SECTION_TYPE", "LkpSectionType"),
	ORGANISM_TYPE("ORGANISM_TYPE", "LkpOrganismType"),
	ANTI_MICROBIAL_TYPE("ANTI_MICROBIAL_TYPE", "LkpAntiMicrobialType"),
	ORGANISM_DETECTION("ORGANISM_DETECTION", "LkpOrganismDetection"),
	ORGANISM_SENSITIVITY("ORGANISM_SENSITIVITY", "LkpOrganismSensitivity"),
	REPORT_TYPE("REPORT_TYPE", "LkpReportType");

	private String value;
	private String entity;

	private EhopeLkpType(String value, String entity) {
		this.value = value;
		this.entity = entity;
	}

	public String getEntity() {
		return entity;
	}

	public String getValue() {
		return value;
	}

}
