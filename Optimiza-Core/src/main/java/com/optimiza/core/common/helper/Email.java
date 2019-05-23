package com.optimiza.core.common.helper;

import java.util.Map;

/**
 * Email.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/15/2018
 **/
public class Email {

	private String templateUri;
	private String name;
	private String to;
	private Map<String, String> templateValueMap;
	private Map<String, byte[]> attachmentBytes;

	public Email(String templateUri, String name, String to, Map<String, String> templateValueMap) {
		this.templateUri = templateUri + ".ftl";
		this.name = name;
		this.to = to;
		this.templateValueMap = templateValueMap;
	}

	public Map<String, byte[]> getAttachmentBytes() {
		return attachmentBytes;
	}

	public void setAttachmentBytes(Map<String, byte[]> attachmentBytes) {
		this.attachmentBytes = attachmentBytes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplateUri() {
		return templateUri;
	}

	public void setTemplateUri(String templateUri) {
		this.templateUri = templateUri;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Map<String, String> getTemplateValueMap() {
		return templateValueMap;
	}

	public void setTemplateValueMap(Map<String, String> templateValueMap) {
		this.templateValueMap = templateValueMap;
	}

}
