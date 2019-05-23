package com.optimiza.ehope.web.test.wrapper;

public class TestDefinitionFetch {

	private Long rid;
	private FetchMode mode;
	private Boolean isActive;

	public enum FetchMode {
		viewSingle,
		edit;
	}

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public FetchMode getMode() {
		return mode;
	}

	public void setMode(FetchMode mode) {
		this.mode = mode;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
