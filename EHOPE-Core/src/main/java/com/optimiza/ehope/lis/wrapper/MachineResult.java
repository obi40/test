package com.optimiza.ehope.lis.wrapper;

public class MachineResult {

	private String testCode;
	private String barcode;
	private String result;
	private Boolean isAccepted;

	public String getTestCode() {
		return testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcodeNo(String barcode) {
		this.barcode = barcode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Boolean getIsAccepted() {
		return isAccepted;
	}

	public void setIsAccepted(Boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

}
