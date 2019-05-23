package com.optimiza.ehope.lis.util;

import java.math.BigDecimal;

import com.optimiza.ehope.lis.helper.Comparator;

/**
 * NumberUtil.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/04/2018
 **/
public class NumberUtil {

	public static final BigDecimal MAX_PERCENTAGE = new BigDecimal("100");

	/**
	 * Get the smallest decimal number.
	 * 
	 * EX: Decimals: 2 -> number: 0.01
	 * Decimals: 4 -> number: 0.0001
	 * 
	 * @param decimals : amount of decimals
	 * @return BigDecimal
	 */
	public static BigDecimal getSmallestDecimal(Integer decimals) {
		if (decimals == null || decimals <= 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal divisor = BigDecimal.ONE;
		for (int i = 0; i < decimals; i++) {
			divisor = divisor.divide(BigDecimal.TEN);
		}
		return divisor;
	}

	public static BigDecimal getValueRespectingComparator(String comparator, BigDecimal value, Integer decimals) {
		if (comparator == null) {
			return value;
		}
		BigDecimal smallestDecimalValue = NumberUtil.getSmallestDecimal(decimals);
		switch (Comparator.fromString(comparator)) {
			case gt:
				return value.add(smallestDecimalValue);
			case lt:
				return value.subtract(smallestDecimalValue);
			default:
			case gte:
			case lte:
				return value;
		}
	}

	/**
	 * Validate if the incoming percentage is not more than 100% or less than 0%, if so then correct the percentage.
	 * 
	 * @param percentage : i.e. 91.52%
	 * @return a validated percentage
	 */
	public static BigDecimal validatePercentage(BigDecimal percentage) {

		if (percentage == null) {
			return BigDecimal.ZERO;
		}
		if (percentage.compareTo(MAX_PERCENTAGE) == 1) {
			percentage = MAX_PERCENTAGE;
		} else if (percentage.compareTo(BigDecimal.ZERO) == -1) {
			percentage = BigDecimal.ZERO;
		}
		return percentage;
	}

	/**
	 * Check if strNum is in numeric format
	 * 
	 * @param strNum
	 * @return true of numeric format
	 */
	public static boolean isNumeric(String strNum) {
		return strNum.matches("-?\\d+(\\.\\d+)?");
	}

}
