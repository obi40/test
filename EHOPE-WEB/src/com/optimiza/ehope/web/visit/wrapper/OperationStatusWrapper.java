package com.optimiza.ehope.web.visit.wrapper;

import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;

public class OperationStatusWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private Long rid;
	private OperationStatus operationStatus;
	private String type;
	private String comment;
	private FilterablePageRequest filterablePageRequest;
	private List<Long> propagateRids;//samples rids or tests rids
	private Long visitRid;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operationStatus == null) ? 0 : operationStatus.hashCode());
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperationStatusWrapper other = (OperationStatusWrapper) obj;
		if (operationStatus != other.operationStatus)
			return false;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public Long getVisitRid() {
		return visitRid;
	}

	public void setVisitRid(Long visitRid) {
		this.visitRid = visitRid;
	}

	@Override
	public String toString() {
		return "OperationStatusWrapper [rid=" + rid + ", operationStatus=" + operationStatus + ", type=" + type + ", comment=" + comment
				+ ", filterablePageRequest=" + filterablePageRequest + ", propagateRids=" + propagateRids + "]";
	}

	public List<Long> getPropagateRids() {
		return propagateRids;
	}

	public void setPropagateRids(List<Long> propagateRids) {
		this.propagateRids = propagateRids;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public FilterablePageRequest getFilterablePageRequest() {
		return filterablePageRequest;
	}

	public void setFilterablePageRequest(FilterablePageRequest filterablePageRequest) {
		this.filterablePageRequest = filterablePageRequest;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public OperationStatus getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

}
