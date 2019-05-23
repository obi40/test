package com.optimiza.core.common.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.optimiza.core.base.entity.BaseEntity;

public class ResponseDataContainer<T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T dataBean;
	private List<T> dataList;
	private String createdRecordId;
	private String dataItem;

	private List infoMessagesList;
	private List errorMessagesList;
	private List warningMessagesList;

	public void setDataBean(T dataBean) {
		this.dataBean = dataBean;
	}

	public T getDataBean() {
		return dataBean;
	}

	public void addWarningMessage(String messageKey, String... messageArguments) {
		if (warningMessagesList == null) {
			warningMessagesList = new ArrayList();
		}
		warningMessagesList.add(messageKey);
	}

	public void addInfoMessage(String messageKey, String... messageArguments) {

		if (infoMessagesList == null) {
			infoMessagesList = new ArrayList();
		}
		infoMessagesList.add(messageKey);
	}

	public void addErrorMessage(String messageKey, String... messageArguments) {

		if (errorMessagesList == null) {
			errorMessagesList = new ArrayList();
		}
		errorMessagesList.add(messageKey);
	}

	public boolean containsErrorMessages() {
		if (errorMessagesList == null || errorMessagesList.isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean containsWarningMessages() {
		if (warningMessagesList == null || warningMessagesList.isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean containsInfoMessages() {
		if (infoMessagesList == null || infoMessagesList.isEmpty()) {
			return false;
		}
		return true;
	}

	public void setInfoMessagesList(List infoMessagesList) {
		this.infoMessagesList = infoMessagesList;
	}

	public List getInfoMessagesList() {
		return infoMessagesList;
	}

	public void setErrorMessagesList(List errorMessagesList) {
		this.errorMessagesList = errorMessagesList;
	}

	public List getErrorMessagesList() {
		return errorMessagesList;
	}

	public void setCreatedRecordId(String createdRecordId) {
		this.createdRecordId = createdRecordId;
	}

	public String getCreatedRecordId() {
		return createdRecordId;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public List getWarningMessagesList() {
		return warningMessagesList;
	}

	public String getDataItem() {
		return dataItem;
	}

	public void setDataItem(String dataItem) {
		this.dataItem = dataItem;
	}

}
