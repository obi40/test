package com.optimiza.ehope.web.security.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.optimiza.core.common.util.JSONUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.common.util.TokenUtil;

import io.jsonwebtoken.JwtException;

public class TokenFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private TokenUtil tokenUtils;

	@Value("${system.website.base}")
	private String SERVER_BASE;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			HttpServletRequest httpRequest = this.getAsHttpRequest(request);

			if (httpRequest.getServletPath().equals("/index.html")) {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
				httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
				httpResponse.setHeader("Expires", "0"); // Proxies.
			}

			String token = httpRequest.getHeader("Authorization");
			if (auth == null && !StringUtil.isEmpty(token)) {
				UsernamePasswordAuthenticationToken authentication = this.tokenUtils.getAuthTokenNoCatch(token);
				if (authentication != null) {
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			if (e instanceof JwtException) {
				handleTokenExceptions(response, request, e);
			}
		}
	}

	private void handleTokenExceptions(ServletResponse resp, ServletRequest req, Exception exception) {
		try {
			final String WWW_SERVER_BASE = SERVER_BASE.replace("https://", "https://www.");
			HttpServletResponse response = (HttpServletResponse) resp;
			HttpServletRequest request = (HttpServletRequest) req;
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			String requestOrigin = request.getHeader(HttpHeaders.ORIGIN);
			if (!StringUtil.isEmpty(requestOrigin)) {
				if (requestOrigin.equals(SERVER_BASE)) {
					response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, SERVER_BASE);
				} else if (requestOrigin.equals(WWW_SERVER_BASE)) {
					response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, WWW_SERVER_BASE);
				}
			}
			Map<String, String> map = new HashMap<>();
			map.put("code", "invalidToken");
			response.getWriter().print(JSONUtil.convertMapToJSON(map));
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private HttpServletRequest getAsHttpRequest(ServletRequest request) {
		if (!(request instanceof HttpServletRequest)) {
			throw new RuntimeException("Expecting an HTTP request");
		}
		return (HttpServletRequest) request;
	}

}
