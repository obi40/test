package com.optimiza.ehope.lis.lkp.helper;

public enum SerialType {

	SAMPLE_NO("SAMPLE_NO"),
	SAMPLE_BARCODE("SAMPLE_BARCODE"),
	PATIENT_FILE_NO("PATIENT_FILE_NO"),
	VISIT_ADMISSION_NO("VISIT_ADMISSION_NO"),
	CHARGE_SLIP_NO("CHARGE_SLIP_NO"),
	PAYMENT_NO("PAYMENT_NO"),
	CANCEL_NO("CANCEL_NO"),
	RECALCULATE_NO("RECALCULATE_NO"),
	REFUND_NO("REFUND_NO"),
	INVOICE_NO("INVOICE_NO");

	private String value;

	private SerialType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
