package com.optimiza.ehope.lis.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.HttpUtil;
import com.optimiza.ehope.lis.helper.SMSKey;
import com.optimiza.ehope.lis.model.ConfTenantSMS;
import com.optimiza.ehope.lis.service.ConfTenantSMSService;

@Component
public class SMSUtil {

	@Autowired
	private ConfTenantSMSService confTenantSMSService;

	/**
	 * Send an SMS message
	 * 
	 * @param smsValues : put the values the represent the SMSKey enum
	 */
	public ResponseEntity<String> send(Map<SMSKey, String> smsValues) {

		List<ConfTenantSMS> conTenantSMSList = confTenantSMSService.getSMSConfig(new ArrayList<>(), null, "smsKey");

		//-1 becuase we dont send url it is in the ConfTenantSMS
		if (CollectionUtil.isCollectionEmpty(conTenantSMSList) || ((SMSKey.values().length - 1) != smsValues.size())) {
			throw new BusinessException("No SMS Configurations", "smsNoConfig", ErrorSeverity.ERROR);
		}
		Map<String, String> uriVariables = new HashMap<>();
		Map<String, Boolean> uriVariablesEncoding = new HashMap<>();
		String url = null;
		for (ConfTenantSMS cts : conTenantSMSList) {
			String key = cts.getKey();
			String value = cts.getValue();
			if (cts.getSmsKey() != null) {//means it is not a static value(except url) i.e. mobile number
				SMSKey smsKey = SMSKey.valueOf(cts.getSmsKey().getCode());
				switch (smsKey) {
					case BASE_URL:
						url = cts.getKey();
						continue;//if url then continue
					case MESSAGE:
					case MOBILE:
						value = smsValues.get(smsKey);
						break;
					default:
						break;
				}
			}
			uriVariables.put(key, value);
			uriVariablesEncoding.put(key, cts.getIsEncodeKey());
			uriVariablesEncoding.put(value, cts.getIsEncodeValue());
		}

		return HttpUtil.queryParamsPost(url, uriVariables, uriVariablesEncoding, String.class);
	}
}
