package com.optimiza.core.common.helper;

import java.math.BigDecimal;

/**
 * 
 * CustomComparable.java
 * 
 * @author Wa'el Abu Rahmeh <waburahemh@optimizasolutions.com>
 * @param <T>
 * @since 21/05/2017
 */

public interface CustomComparable<T> extends Comparable<T> {

	BigDecimal getFromValue();

	void setFromValue(BigDecimal fromValue);

	BigDecimal getToValue();

	void setToValue(BigDecimal toValue);

	BigDecimal getStep();
}
