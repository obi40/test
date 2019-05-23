package com.optimiza.core.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.common.helper.CustomComparable;

/**
 * CollectionUtils.java, Used To handle collection operations
 *
 * @author Wa'el Abu Rahmeh <waburahemh@optimizasolutions.com>
 * @since 21/05/2017
 **/

public class CollectionUtil {

	/**
	 * Check if a given collection is empty
	 * 
	 * @param collection
	 * @return boolean, true if collection is null or empty, false otherwise
	 */
	public static boolean isCollectionEmpty(Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Check if a given map is NOT empty
	 * 
	 * @param map
	 * @return boolean, false if map is null or empty, true otherwise
	 */
	public static boolean isMapEmpty(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}

	public static String convertListToCommaSeparatedStr(List<String> list) {
		StringBuilder str = new StringBuilder();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			str.append(it.next());
			if (it.hasNext()) {
				str.append(",");
			}
		}
		return str.toString();
	}

	public static String convertListOfLongToCommaSeparatedStr(List<Long> list) {
		StringBuilder str = new StringBuilder();
		Iterator<Long> it = list.iterator();
		while (it.hasNext()) {
			str.append(it.next());
			if (it.hasNext()) {
				str.append(",");
			}
		}
		return str.toString();
	}

	/**
	 * Take rid of each entity, return string containing all rid's comma separated
	 * 
	 * @param entities
	 * @return
	 */
	public static String convertListOfEntitiesToCommaSeparatedStr(List<? extends BaseEntity> entities) {
		StringBuilder str = new StringBuilder();
		@SuppressWarnings("unchecked")
		Iterator<BaseEntity> it = (Iterator<BaseEntity>) entities.iterator();

		while (it.hasNext()) {

			BaseEntity entity = it.next();
			str.append(entity.getRid());
			if (it.hasNext()) {
				str.append(",");
			}
		}
		return str.toString();
	}

	public static String convertListToCommaSeparatedStr(String[] list) {
		StringBuilder str = new StringBuilder();
		for (String string : list) {
			str.append(string);
			str.append(",");
		}

		return str.substring(0, str.length() - 1);
	}

	@SafeVarargs
	public static <X> List<X> joinLists(List<X>... lists) {

		List<X> total = new ArrayList<X>();

		for (int i = 0; i < lists.length; i++) {
			if (!isCollectionEmpty(lists[i])) {
				total.addAll(lists[i]);
			}
		}

		return total;
	}

	public static <T> void removeDuplicates(List<T> list) {
		// to remove duplicates, use HashSet
		HashSet<T> temp = new HashSet<>();
		temp.addAll(list);
		list.clear();
		list.addAll(temp);
	}

	// operator AND or OR
	public static String convertMapToSQLWhereStmnt(Map<String, Object> params, String operator) {

		StringBuilder sb = new StringBuilder();
		Set<String> parameterSet = params.keySet();
		Iterator<String> iterator = parameterSet.iterator();
		while (iterator.hasNext()) {
			String parameter = iterator.next();
			sb.append(" ").append(parameter).append(" = ").append(params.get(parameter));
			if (iterator.hasNext()) {
				sb.append(" ").append(operator);
			}
		}

		return sb.toString();

	}

	// operator AND or OR
	public static String convertMapToSQLWhereStmntLIKE(Map<String, Object> params, String operator) {

		StringBuilder sb = new StringBuilder();
		Set<String> parameterSet = params.keySet();
		Iterator<String> iterator = parameterSet.iterator();
		while (iterator.hasNext()) {
			String parameter = iterator.next();
			sb.append(" LOWER(").append(StringUtil.toUpperUnderscore(parameter)).append(")");
			sb.append(" LIKE LOWER(").append("'%").append(params.get(parameter)).append("%')");
			if (iterator.hasNext()) {
				sb.append(" ").append(operator);
			}
		}

		return sb.toString();

	}

	/**
	 * Check of overlap and gaps in any List<T>, where T MUST implement CustomComparable interface
	 * 
	 * @param list, fromValue, toValue
	 * 
	 * @return List<List<T>> List.get(0) is overlap and List.get(1) is gap. If any is empty then there is no overlap or gaps
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends CustomComparable<? super T>> List<List<T>> checkOverlapAndGaps(List<T> list, BigDecimal fromValue,
			BigDecimal toValue) {
		if (!isCollectionEmpty(list)) {
			Class<? extends CustomComparable> cls = list.get(0).getClass();
			CustomComparable newRec = null;
			try {
				newRec = cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
			}
			newRec.setFromValue(fromValue);
			newRec.setToValue(toValue);

			list.add((T) newRec);
			return checkOverlapAndGaps(list);
		}
		return null;
	}

	/**
	 * Check of overlap and gaps in any List<T>, where T MUST implement CustomComparable interface
	 * 
	 * @param list
	 * 
	 * @return List<List<T>> List.get(0) is overlap and List.get(1) is gap. If any is empty then there is no overlap or gaps
	 */
	public static <T extends CustomComparable<? super T>> List<List<T>> checkOverlapAndGaps(List<T> list) {

		// sort the list
		Collections.sort(list);

		T current;
		T previous;
		List<T> overlapList = new ArrayList<T>();
		List<T> gapList = new ArrayList<T>();

		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);

			if (i > 0) {
				double step = current.getStep().doubleValue();
				int scale = 0;
				if (step < 1) {
					scale = (int) Math.log10(1 / step);
				}
				previous = list.get(i - 1);

				BigDecimal currFromValue = current.getFromValue().setScale(scale, RoundingMode.HALF_UP);
				BigDecimal prevToValue = previous.getToValue().setScale(scale, RoundingMode.HALF_UP);

				if (currFromValue.compareTo(prevToValue) < 1) { // current.from <= previous.to
					// overlap
					overlapList.add(previous);
					overlapList.add(current);
					break;
				} else if (currFromValue.subtract(prevToValue).doubleValue() != step) {
					// gap
					gapList.add(previous);
					gapList.add(current);
					break;
				}
			}
		}

		List<List<T>> returnList = new ArrayList<List<T>>();
		returnList.add(overlapList); // 0 index
		returnList.add(gapList); // 1 index

		return returnList;
	}

	/**
	 * Compare Two Lists using their equals method
	 * 
	 * @param list1
	 * @param list2
	 * @return true if they are identical
	 */
	public static <T> boolean areListsSameIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}

	// public static void main(String[] args) {
	// List<Integer> a1 = new ArrayList<Integer>();
	// a1.add(1);
	// a1.add(2);
	// a1.add(3);
	//
	// List<Integer> a2 = new ArrayList<Integer>();
	// a1.add(5);
	// a1.add(6);
	// a1.add(7);
	//
	// List<Integer> a3 = new ArrayList<Integer>();
	// a1.add(9);
	// a1.add(10);
	// a1.add(11);
	//
	// List<Integer> a4 = null;
	// List<Integer> a5 = new ArrayList<Integer>();;
	//
	// List<Integer> total = CollectionUtils.joinLists(a1,a4, a5, a2, a3);
	// for (Integer integer : total) {
	// System.out.println(integer);
	// }
	// }

}
