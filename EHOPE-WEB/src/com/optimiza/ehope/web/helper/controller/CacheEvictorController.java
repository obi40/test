package com.optimiza.ehope.web.helper.controller;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * CacheEvictorController.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/14/2017
 **/
@RestController
@RequestMapping("/services")
public class CacheEvictorController {

	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private EntityManager entityManager;

	@RequestMapping(value = "/deleteCache.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteCache() {

		//Deleting all caches in system, for development purposes only
		cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
		//		Class<?> clazz = ReflectionUtil.getEntityClassByName(entityName, entityManager);
		//		String cacheName = null;
		//		if (clazz.getSimpleName().startsWith("Lkp") && clazz.getSuperclass() == BaseAuditableTenantedEntity.class) {
		//			cacheName = clazz.getSimpleName();
		//		} else {
		//			cacheName = CustomCacheKeyGenerator.generateKey(ReflectionUtil.getEntityClassByName(entityName, entityManager));
		//		}
		//		for (String name : cacheManager.getCacheNames()) {
		//			if (name.equals(cacheName)) {
		//				cacheManager.getCache(cacheName).clear();
		//				break;
		//			}
		//		}
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}
