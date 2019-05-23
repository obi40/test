package com.optimiza.ehope.web.visit.wrapper;

import java.util.Map;

import com.optimiza.core.base.entity.BaseWrapper;

public class ResultsDataWrapper extends BaseWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long visitRid;
	private Map<Long, Boolean> testsMap;
	private Map<String, String> emailMap;
	private String timezoneOffset;
	private String timezoneId;

	public String getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(String timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public String getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	public Map<String, String> getEmailMap() {
		return emailMap;
	}

	public void setEmailMap(Map<String, String> emailMap) {
		this.emailMap = emailMap;
	}

	public Long getVisitRid() {
		return visitRid;
	}

	public void setVisitRid(Long visitRid) {
		this.visitRid = visitRid;
	}

	public Map<Long, Boolean> getTestsMap() {
		return testsMap;
	}

	public void setTestsMap(Map<Long, Boolean> testsMap) {
		this.testsMap = testsMap;
	}

}
