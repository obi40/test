package com.optimiza.core.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.optimiza.core.admin.model.SecUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class TokenUtil {

	@Value("${system.version}")
	private String SYSTEM_VERSION;

	@Value("${system.token.secretKey}")
	private String SECRET_KEY;

	@Value("${system.token.defaultExpiration}")
	public String DEFAULT_TOKEN_EXPIRATION;

	@Value("${system.token.forgotExpiration}")
	public String FORGOT_TOKEN_EXPIRATION;

	@Value("${system.token.onboardingExpiration}")
	public String ONBOARDING_TOKEN_EXPIRATION;

	@Value("${system.token.integrationExpiration}")
	public String INTEGRATION_TOKEN_EXPIRATION;

	@Value("${system.token.dummyExpiration}")
	public String DUMMY_TOKEN_EXPIRATION;

	private final Long INTEGRATION_USER_ID = -1L;//TODO:application.properties
	private final Long DUMMY_USER_ID = -2L;//TODO:application.properties

	/**
	 * The acutal process of getting claims from token and create an authentication.
	 * 
	 * @param token
	 * @return UsernamePasswordAuthenticationToken
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	private UsernamePasswordAuthenticationToken processAuthToken(String token)
			throws JsonParseException, JsonMappingException, IOException {
		Claims claims = getClaimsFromToken(token);
		String userString = claims.get("user").toString();
		ObjectMapper mapper = new ObjectMapper();
		SecUser user = mapper.readValue(userString, SecUser.class);
		Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
		List<String> tempAuthorities = (List<String>) claims.get("authorities");
		if (tempAuthorities != null) {
			for (int i = 0; i < tempAuthorities.size(); i++) {
				authorities.add(new SimpleGrantedAuthority(tempAuthorities.get(i)));
			}
		}
		UsernamePasswordAuthenticationToken authentication = null;
		authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
		return authentication;
	}

	/**
	 * Get authentication token from JWT token (without catch)
	 * 
	 * @param token The JWT token received with the XHR
	 * @return UsernamePasswordAuthenticationToken
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public UsernamePasswordAuthenticationToken getAuthTokenNoCatch(String token)
			throws JsonParseException, JsonMappingException, IOException {
		return processAuthToken(token);
	}

	/**
	 * Get authentication token from JWT token (with catch)
	 * 
	 * @param token The JWT token received with the XHR
	 * @return UsernamePasswordAuthenticationToken
	 */
	public UsernamePasswordAuthenticationToken getAuthToken(String token) {
		try {
			return processAuthToken(token);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get data from token.
	 * 
	 * @param token
	 * @return Claims
	 */
	public Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts	.parser()
							.setSigningKey(SECRET_KEY)
							.parseClaimsJws(token)
							.getBody();
			String tokenVersion = claims.get("version") != null ? (String) claims.get("version") : null;
			if (StringUtil.isEmpty(tokenVersion) || !tokenVersion.equals(SYSTEM_VERSION)) {
				throw new ExpiredJwtException(null, claims, token);
			}
		} catch (ExpiredJwtException e) {

			//indicating that a JWT was accepted after it expired and must be rejected
			throw e;

		} catch (UnsupportedJwtException e) {

			//thrown when receiving a JWT in a particular format/configuration that does not 
			//match the format expected by the application. For example, this exception would 
			//be thrown if parsing an unsigned plaintext JWT when the application requires a 
			//cryptographically signed Claims JWS instead
			throw e;

		} catch (MalformedJwtException e) {

			//thrown when a JWT was not correctly constructed and should be rejected
			throw e;

		} catch (SignatureException e) {

			//indicates that either calculating a signature or verifying an existing signature of a JWT failed
			throw e;

		} catch (IllegalArgumentException e) {

			throw e;

		} catch (Exception e) {

			throw e;

		}
		return claims;
	}

	/**
	 * Validate token.
	 * 
	 * @param token
	 * @return true if token is valid otherwise false
	 */
	public Boolean validateToken(String token) {
		boolean valid;
		try {
			getClaimsFromToken(token);
			valid = true;
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}
		return valid;
	}

	/**
	 * Generate a token.
	 * 
	 * @param data data to insert inside the token
	 * @param expiration token-life-time
	 * @return token
	 */
	public String generateToken(Map<String, Object> data, String expiration) {
		Map<String, Object> claims = new HashMap<String, Object>();

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			claims.put(entry.getKey(), entry.getValue());
		}

		Date creationDate = new Date(System.currentTimeMillis());
		claims.put("created", creationDate);
		claims.put("version", SYSTEM_VERSION);
		Long expirationTime = Long.parseLong(expiration);
		return Jwts	.builder()
					.setClaims(claims)
					.setExpiration(new Date(creationDate.getTime() + expirationTime * 1000))
					.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
					.compact();
	}

	/**
	 * Token data
	 * 
	 * @param user
	 * @param authorities
	 * @return Map
	 */
	public Map<String, Object> generateTokenData(SecUser user, Collection<? extends GrantedAuthority> authorities) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map<String, Object> data = new HashMap<>();
		try {
			data.put("user", mapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		data.put("authorities", authorities);
		return data;
	}

	/**
	 * Generate a token for the <b>onboarding</b> process so it can be used to make API calls to the server.
	 * 
	 * @return token
	 */
	public String generateDummyToken() {
		SecUser secUser = new SecUser();
		secUser.setRid(DUMMY_USER_ID);
		return generateToken(generateTokenData(secUser, new ArrayList<>()), DUMMY_TOKEN_EXPIRATION);
	}

	/**
	 * Generate a token for the <b>onboarding</b> process so it can be used to make API calls to the server.
	 * 
	 * @param tenantId the id to set inside the token
	 * @return token
	 */
	public String generateTenantOnboardingToken(Long tenantId, List<GrantedAuthority> authorities) {
		SecUser secUser = new SecUser();
		secUser.setRid(DUMMY_USER_ID);
		secUser.setTenantId(tenantId);
		return generateToken(generateTokenData(secUser, authorities), ONBOARDING_TOKEN_EXPIRATION);
	}

	/**
	 * Generate a token for the <b>integration</b> process so it can be used to make API calls to the server.
	 * 
	 * @param tenantId the tenant id to set inside the token
	 * @param branchId the branch id to set inside the token
	 * @param authorities The authorities to put inside the token
	 * @return token
	 */
	public String generateIntegrationToken(Long tenantId, Long branchId, Collection<? extends GrantedAuthority> authorities) {
		SecUser secUser = new SecUser();
		secUser.setRid(INTEGRATION_USER_ID);
		secUser.setBranchId(branchId);
		secUser.setTenantId(tenantId);
		return generateToken(generateTokenData(secUser, authorities), INTEGRATION_TOKEN_EXPIRATION);
	}

	/**
	 * Login Data that is returned when user log in.
	 * 
	 * @param user
	 * @param authorities
	 * @return Map
	 */
	public Map<String, Object> loginData(SecUser user, Collection<? extends GrantedAuthority> authorities) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			//remove keys from the generated string to shorten the token
			JsonNode jsonNode = mapper.readTree(mapper.writeValueAsString(user));
			ObjectNode jsonNodeObj = (ObjectNode) jsonNode;
			jsonNodeObj.remove("password");
			jsonNodeObj.remove("updateDate");
			jsonNodeObj.remove("updatedBy");
			jsonNodeObj.remove("markedForDeletion");
			jsonNodeObj.remove("hibernateLazyInitializer");
			jsonNodeObj.remove("handler");
			jsonNodeObj.remove("country");
			jsonNodeObj.remove("tenantLanguages");
			jsonNodeObj.remove("userGroups");
			jsonNodeObj.remove("userRoles");
			jsonNodeObj.remove("lkpUserStatus");
			jsonNodeObj.remove("lkpGender");
			jsonNodeObj.remove("comLanguage");
			jsonNodeObj.remove("branch");
			jsonNodeObj.remove("tenant");
			data.put("user", jsonNode.toString());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> authorityList = new ArrayList<String>();
		for (GrantedAuthority ga : authorities) {
			authorityList.add(ga.getAuthority());
		}

		data.put("authorities", authorityList);
		String token = generateToken(data, DEFAULT_TOKEN_EXPIRATION);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("token", token);
		map.put("authorities", authorities);
		map.put("user", user);
		return map;
	}

	public String getBranchedToken(Long branchRid, HttpServletRequest request) {
		String currentToken = request.getHeader("Authorization");
		String newToken = null;
		try {
			UsernamePasswordAuthenticationToken authentication = getAuthTokenNoCatch(currentToken);
			SecUser currentUser = SecurityUtil.getCurrentUser();
			currentUser.setBranchId(branchRid);
			newToken = (String) loginData(currentUser, authentication.getAuthorities()).get("token");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newToken;
	}

}