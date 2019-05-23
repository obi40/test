package com.optimiza.core.common.containers;

import java.io.Serializable;
import java.util.List;

import com.optimiza.core.base.entity.BaseEntity;

public class RequestDataContainer<T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T data;
	private List dataList;
	private String dataItem;

	public RequestDataContainer() {
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public void setDataItem(String dataItem) {
		this.dataItem = dataItem;
	}

	public String getDataItem() {
		return dataItem;
	}

}
