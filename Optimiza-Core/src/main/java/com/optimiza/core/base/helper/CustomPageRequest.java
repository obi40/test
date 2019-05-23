package com.optimiza.core.base.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CustomPageRequest extends PageRequest {

	private static final long serialVersionUID = 1L;

	public CustomPageRequest(int page, int size) {
		super(page, size);
	}

	@Autowired
	public CustomPageRequest(@Value("${system.pageSize}") int sysPageSize) {
		super(0, sysPageSize);
	}

}
