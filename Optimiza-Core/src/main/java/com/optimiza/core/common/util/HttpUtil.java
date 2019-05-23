package com.optimiza.core.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;

public class HttpUtil {

	//TODO: error handling?

	/**
	 * Send a POST request with query parameters.
	 * 
	 * @param url
	 * @param uriVariables : the query string parameters
	 * @param uriVariablesEncoding : keys and values of the query string parameters, to know what to encode.
	 * @param clazz : the response type from the client
	 *
	 * @return ResponseEntity
	 */
	public static <T> ResponseEntity<T> queryParamsPost(String url, Map<String, String> uriVariables,
			Map<String, Boolean> uriVariablesEncoding, Class<T> clazz) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			StringBuilder urlParameters = new StringBuilder();
			for (Map.Entry<String, String> entry : uriVariables.entrySet()) {
				if (urlParameters.length() != 0) {//don't add & if it is the first parameter
					urlParameters.append("&");
				}
				String key = uriVariablesEncoding.get(entry.getKey()) != null
						&& uriVariablesEncoding.get(entry.getKey()) == Boolean.TRUE ? URLEncoder.encode(entry.getKey(), "UTF-8")
								: entry.getKey();
				urlParameters.append(key);
				urlParameters.append("=");
				urlParameters.append("{" + key + "}");
				String value = uriVariablesEncoding.get(entry.getValue()) != null
						&& uriVariablesEncoding.get(entry.getValue()) == Boolean.TRUE ? URLEncoder.encode(entry.getValue(), "UTF-8")
								: entry.getValue();
				entry.setValue(value);
			}
			url += "?" + urlParameters.toString();//http://example.org + '?'+ foo={foo}&bar={bar}
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.ALL));
			HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
			ResponseEntity<T> re = restTemplate.exchange(url, HttpMethod.POST, requestEntity, clazz, uriVariables);
			return re;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage(), e.getLocalizedMessage(), ErrorSeverity.ERROR);
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage(), e.getLocalizedMessage(), ErrorSeverity.ERROR);
		}
	}

	public static <T> ResponseEntity<T> jsonPost(String url, Object objectToSend, Class<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> request = new HttpEntity<Object>(objectToSend, headers);
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(url, HttpMethod.POST, request, responseType);
	}

	/**
	 * Using Firebase Dynamic-Link
	 * https://firebase.google.com/docs/dynamic-links/rest
	 * 
	 * @param url
	 * @return short url
	 */
	public static String shortenUrl(String url) {
		Map<String, Map<String, String>> requestBody = new HashMap<>();
		Map<String, String> details = new HashMap<>();
		details.put("domainUriPrefix", "https://acculab.page.link");
		details.put("link", url);
		requestBody.put("dynamicLinkInfo", details);
		ResponseEntity<String> responseEntity = HttpUtil.jsonPost(
				"https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=AIzaSyAWYkwd316NJljeXhdlmrVfamdevMctTAg", requestBody,
				String.class);
		Map<String, Object> map4 = JSONUtil.convertJSONToMap(responseEntity.getBody(), String.class, Object.class);
		return (String) map4.get("shortLink");
	}

	/**
	 * Simple helper method to help you extract the headers from HttpServletRequest object.
	 * 
	 * @param request
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Simple helper method to fetch request data as a string from HttpServletRequest object.
	 * 
	 * @param request
	 * @return Map<String, Object>
	 */
	public static String getBody(HttpServletRequest request) {
		String body = null;
		try {
			body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}

}
