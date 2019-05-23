package com.optimiza.ehope.web.common.controller;

import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FieldMetaData;
import com.optimiza.core.common.util.ReflectionUtil;

/**
 * CommonMethodsController.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/15/2017
 **/
@RestController
@RequestMapping("/services")
public class CommonMethodsController {

	@Autowired
	private EntityManager entityManager;

	@RequestMapping(value = "/getClassMetaData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, FieldMetaData>> getClassMetaData(@RequestBody String className)
			throws ClassNotFoundException {
		Class<?> clazz = ReflectionUtil.getEntityClassByName(className, entityManager);
		Map<String, FieldMetaData> metaData = ReflectionUtil.getEntityFieldMetaData(clazz);
		return new ResponseEntity<Map<String, FieldMetaData>>(metaData, HttpStatus.OK);
	}

}
