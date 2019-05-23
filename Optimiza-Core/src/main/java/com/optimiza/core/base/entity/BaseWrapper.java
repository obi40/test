package com.optimiza.core.base.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * BaseWrapper.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Apr/26/2018
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

}