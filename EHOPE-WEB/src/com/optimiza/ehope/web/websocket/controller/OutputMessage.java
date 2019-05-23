package com.optimiza.ehope.web.websocket.controller;

public class OutputMessage {

	private String messageCode;
	private String sampleNo;

	public OutputMessage(String messageCode, String sampleNo) {
		this.messageCode = messageCode;
		this.sampleNo = sampleNo;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getSampleNo() {
		return sampleNo;
	}

	public void setSampleNo(String sampleNo) {
		this.sampleNo = sampleNo;
	}

}
