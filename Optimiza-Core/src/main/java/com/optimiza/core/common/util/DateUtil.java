package com.optimiza.core.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.Interval;
import org.joda.time.Period;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.optimiza.core.common.helper.AgeWrapper;
import com.optimiza.core.common.helper.ElapsedPeriod;

/**
 * DateUtil.java, Used as a utility to handle Date calculations
 *
 * @author Wa'el Abu Rahmeh <waburahemh@optimizasolutions.com>
 * @since 21/05/2017
 **/

public class DateUtil {

	private static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {

		private static final long serialVersionUID = 1L;

		{
			add(new SimpleDateFormat("M/dd/yyyy"));
			add(new SimpleDateFormat("dd.M.yyyy"));
			add(new SimpleDateFormat("dd.MMM.yyyy"));
			add(new SimpleDateFormat("dd-MMM-yyyy"));
			add(new SimpleDateFormat("dd-mm-yyyy"));
			add(new SimpleDateFormat("yyyy-mm-dd"));
			add(new SimpleDateFormat("yyyy-MMM-dd"));
			add(new SimpleDateFormat("dd-MM-yy"));
			add(new SimpleDateFormat("dd-MM-yyyy"));
			add(new SimpleDateFormat("MM-dd-yyyy"));
			add(new SimpleDateFormat("yyyy-MM-dd"));
		}
	};

	public static String formatDBDate(Date date) {

		// FIXME, mmust get the format from the property file
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(date);
	}

	public static String formatDBDatetime(Date date) {

		// FIXME, mmust get the format from the property file
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(date);
	}

	public static String formatJavaDate(Date date) {

		// FIXME, mmust get the format from the property file
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		return sdf.format(date);
	}

	/**
	 * Compares if the date specified is equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isEqual(Date date, Date compareDate) {
		return date.equals(compareDate);
	}

	/**
	 * Compares if the date specified is equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isEqualTrimTime(Date date, Date compareDate) {
		Date tempDate = (Date) date.clone();
		Date tempCompareDate = (Date) compareDate.clone();
		return trimTime(tempDate).equals(trimTime(tempCompareDate));
	}

	/**
	 * Compares if the date specified is after or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isAfterOrEqual(Date date, Date compareDate) {
		return (date.equals(compareDate) || date.after(compareDate));
	}

	/**
	 * Compares if the date specified is before or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBeforeOrEqual(Date date, Date compareDate) {
		return (date.equals(compareDate) || date.before(compareDate));
	}

	/**
	 * Compares if the date specified is after compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isAfter(Date date, Date compareDate) {
		return (date.after(compareDate));
	}

	/**
	 * Compares if the date specified is after or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isAfterWithTrimTime(Date date, Date compareDate) {
		return (trimTime(date).after(trimTime(compareDate)));
	}

	/**
	 * Compares if the date specified is before compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBefore(Date date, Date compareDate) {
		return (date.before(compareDate));
	}

	/**
	 * Compares if the date specified is before or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBeforeWithTrimTime(Date date, Date compareDate) {
		Date tempDate = (Date) date.clone();
		Date tempCompareDate = (Date) compareDate.clone();
		return (trimTime(tempDate).before(trimTime(tempCompareDate)));
	}

	public static boolean isBeforeOrEqualWithTrimTime(Date date, Date compareDate) {
		return isBeforeOrEqual(trimTime(date), trimTime(compareDate));
	}

	/**
	 * Compares if the date specified is between startDate, and endDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBetween(Date date, Date startDate, Date endDate) {
		return isAfterOrEqual(date, startDate) && isBeforeOrEqual(date, endDate);
	}

	/**
	 * Compares if the date specified is between startDate, and endDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isIntersected(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
		if (startDate1 == null || startDate2 == null || (endDate1 == null && endDate2 == null)) {
			return true;
		}

		if ((endDate2 != null && isBetween(startDate1, startDate2, endDate2))
				|| (endDate1 != null && isBetween(startDate2, startDate1, endDate1))
				|| (endDate1 == null && isAfterOrEqual(startDate2, startDate1))
				|| (endDate2 == null && isAfterOrEqual(startDate1, startDate2))) {
			return true;
		}
		return false;
		//		// using joda
		//		DateTime start1 = new DateTime(startDate1);
		//		DateTime end1 = new DateTime(endDate1);
		//		DateTime start2 = new DateTime(startDate2);
		//		DateTime end2 = new DateTime(endDate2);
		//
		//		Interval interval1 = new Interval(start1, end1);
		//		Interval interval2 = new Interval(start2, end2);
		//		return interval1.overlaps(interval2);
	}

	// public static void main(String[] args) {
	// Calendar cal = Calendar.getInstance();
	// cal.add(Calendar.DATE, -10);
	// Date startDate1 = (cal.getTime());
	//
	// Calendar end = Calendar.getInstance();
	// end.add(Calendar.DATE, -5);
	// Date endDate1 = (end.getTime());
	//
	// Calendar cal2 = Calendar.getInstance();
	// cal2.add(Calendar.DATE, -4);
	// Date startDate2 = (cal2.getTime());
	//
	// Calendar end2 = Calendar.getInstance();
	// end2.add(Calendar.DATE, 1);
	// Date endDate2 = (end2.getTime());
	//
	// boolean intersected = DateUtil.isIntersected(startDate1, endDate1, startDate2, endDate2);
	// System.out.println(intersected);
	// }

	public static Date getCurrentDateWithoutTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * set time to zeros in specific Date object
	 *
	 * @param date
	 * @return
	 */
	public static Date trimTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date.setTime(cal.getTimeInMillis());

		return date;
	}

	/**
	 * set time to zeros in specific Calendar object
	 *
	 * @param calendar
	 * @return
	 */
	public static Calendar trimTime(Calendar calendar) {

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	/**
	 * set date always to default date 1/1/2000
	 *
	 * @param date
	 * @return
	 */
	public static Date trimDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return trimDate(cal);
	}

	public static Date trimDate(Calendar cal) {
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Compares if the time-only specified is after or equal to time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeAfterOrEqual(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.equals(compareDate) || date.after(compareDate));
	}

	/**
	 * Compares if the time-only specified is before or equal to time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeBeforeOrEqual(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.equals(compareDate) || date.before(compareDate));
	}

	/**
	 * Compares if the time-only specified is after time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeAfter(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.after(compareDate));
	}

	/**
	 * Compares if the time-only specified is before time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeBefore(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.before(compareDate));
	}

	/**
	 * Compares if the time-only in date specified is between time in startDate, and time in endDate
	 *
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTimeBetween(Date date, Date startDate, Date endDate) {
		return isTimeAfterOrEqual(date, startDate) && isTimeBeforeOrEqual(date, endDate);
	}

	/**
	 * Compares if the time-only in date specified is between time in startDate, and time in endDate (but not equal end)
	 *
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTimeBetweenWithOpenEnd(Date date, Date startDate, Date endDate) {
		return isTimeAfterOrEqual(date, startDate) && isTimeBefore(date, endDate);
	}

	/**
	 * Compares if the time-only in date specified is between time in startDate, and time in endDate
	 *
	 * @param date
	 * @param date2
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTimeIntersected(Date date, Date date2, Date startDate, Date endDate) {
		return isTimeBetween(date, startDate, endDate) || isTimeBetween(date2, startDate, endDate)
				|| (isTimeBefore(date, startDate) && isTimeAfter(date2, endDate));
	}

	/**
	 * get Week Count between specified startDate endDate
	 *
	 * @param startDate
	 * @param endDate
	 * @return long
	 */
	public static long getPeriodWeeksCount(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();

		return (diff / (1000 * 60 * 60 * 24 * 7));
	}

	/**
	 * add days to specified date
	 *
	 * @param date
	 * @param days
	 * @return long
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	/**
	 * add hours to specified date
	 * accept minus value to subtract
	 *
	 * @param date
	 * @param hours
	 * @return Date
	 */
	public static Date addHours(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours); // minus number would decrement the hours
		return cal.getTime();
	}

	/**
	 * add minutes to specified date
	 * accept minus value to subtract
	 * 
	 * @param date
	 * @param minutes
	 * @return Date
	 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes); // minus number would decrement the minutes
		return cal.getTime();
	}

	/**
	 * add seconds to specified date
	 * accept minus value to subtract
	 * 
	 * @param date
	 * @param seconds
	 * @return Date
	 */
	public static Date addSeconds(Date date, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, seconds); // minus number would decrement the seconds
		return cal.getTime();
	}

	/**
	 * set the date to a specified hour
	 * 24 hour clock
	 * 
	 * @param date
	 * @param hour
	 * @return Date
	 */
	public static Date setHour(Date date, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * set the date to a specified minute
	 * 
	 * @param date
	 * @param minute
	 * @return Date
	 */
	public static Date setMinute(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	/**
	 * set the date to a specified second
	 * 
	 * @param date
	 * @param second
	 * @return Date
	 */
	public static Date setSecond(Date date, int second) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}

	public static Date validateDate(String input) throws ParseException {
		if (null == input) {
			throw new ParseException("Entered Date is null", 0);
		}
		boolean valid = false;
		for (SimpleDateFormat format : dateFormats) {
			try {
				format.setLenient(false);
				Date date = format.parse(input);
				valid = true;
				return date;
			} catch (ParseException e) {
				valid = false;
			}

		}
		if (!valid) {
			throw new ParseException("Not a valid date", 0);
		}
		return null;
	}

	public static int convertToSecondfromMidnight(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		hour = hour * 3600;
		int minute = calendar.get(Calendar.MINUTE);
		minute = minute * 60;
		int second = calendar.get(Calendar.SECOND);

		int timeInSeconds = hour + minute + second;
		return timeInSeconds;
	}

	public static Date convertFromSecondfromMidnight(int seconds) {
		int hour = seconds / 3600;
		int reminder = seconds % 3600;

		int minute = reminder / 60;
		reminder = seconds % 60;

		int second = reminder;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return calendar.getTime();
	}

	/**
	 * Get elapsed time from now
	 *
	 * @param date must be less than current date
	 * @return
	 */
	public static ElapsedPeriod elapsedTime(Date date) {
		Date now = new Date();

		Interval interval = new Interval(date.getTime(), now.getTime());
		Period period = interval.toPeriod();

		ElapsedPeriod elapsedPeriod = new ElapsedPeriod();
		elapsedPeriod.setYears(period.getYears());
		elapsedPeriod.setMonths(period.getMonths());
		elapsedPeriod.setDays(period.getDays());
		elapsedPeriod.setHours(period.getHours());
		elapsedPeriod.setMinutes(period.getMinutes());
		elapsedPeriod.setSeconds(period.getSeconds());

		return elapsedPeriod;
	}

	/**
	 * get Age in years by birth date
	 * 
	 * @param birthDate
	 * @return age in years
	 */
	public static long getAge(Date birthDate) {

		Calendar birthDateCalendar = Calendar.getInstance();
		birthDateCalendar.setTimeInMillis(birthDate.getTime());

		LocalDate start = LocalDate.of(birthDateCalendar.get(Calendar.YEAR), birthDateCalendar.get(Calendar.MONTH) + 1,
				birthDateCalendar.get(Calendar.DAY_OF_MONTH));

		LocalDate end = LocalDate.now();
		long years = ChronoUnit.YEARS.between(start, end);
		return years;
	}

	/**
	 * get AgeWrapper of person
	 * 
	 * @param birthDate
	 * @return AgeWrapper { age [Long] , unit [ChronoUnit] }
	 */
	public static AgeWrapper getAgeWithUnit(Date birthDate) {
		return getAgeWithUnit(birthDate, null);
	}

	/**
	 * get AgeWrapper of person
	 * 
	 * @param birthDate
	 * @param dayDate The date of the day to calculate age against, used like this: dayDate - birthDate = age_at_that_day
	 * @return AgeWrapper { age [Long] , unit [ChronoUnit] }
	 */
	public static AgeWrapper getAgeWithUnit(Date birthDate, Date dayDate) {

		Calendar birthDateCalendar = Calendar.getInstance();
		birthDateCalendar.setTimeInMillis(birthDate.getTime());

		LocalDate start = LocalDate.of(birthDateCalendar.get(Calendar.YEAR), birthDateCalendar.get(Calendar.MONTH) + 1,
				birthDateCalendar.get(Calendar.DAY_OF_MONTH));

		LocalDate end = LocalDate.now();
		if (dayDate != null) {
			Calendar dayDateCalendar = Calendar.getInstance();
			dayDateCalendar.setTimeInMillis(dayDate.getTime());

			end = LocalDate.of(dayDateCalendar.get(Calendar.YEAR), dayDateCalendar.get(Calendar.MONTH) + 1,
					dayDateCalendar.get(Calendar.DAY_OF_MONTH));
		}

		AgeWrapper ageWrapper = new AgeWrapper();
		long years = ChronoUnit.YEARS.between(start, end);
		long months = ChronoUnit.MONTHS.between(start, end);
		long weeks = ChronoUnit.WEEKS.between(start, end);
		long days = ChronoUnit.DAYS.between(start, end);
		if (years >= 1) {
			ageWrapper.setAge(years);
			ageWrapper.setUnit(ChronoUnit.YEARS);
		} else if (months >= 1) {
			ageWrapper.setAge(months);
			ageWrapper.setUnit(ChronoUnit.MONTHS);
		} else if (weeks >= 1) {
			ageWrapper.setAge(weeks);
			ageWrapper.setUnit(ChronoUnit.WEEKS);
		} else {
			ageWrapper.setAge(days);
			ageWrapper.setUnit(ChronoUnit.DAYS);
		}

		return ageWrapper;
	}

	/**
	 * get Age in years from given year
	 * 
	 * @param year
	 * @return age in years
	 */
	public static long getAge(String year) {
		LocalDate start = LocalDate.of(Integer.parseInt(year), 1, 1);

		LocalDate end = LocalDate.now();
		long years = ChronoUnit.YEARS.between(start, end);
		return years;
	}

	public static Date parseUTCDate(String dateString) {
		try {
			if (StringUtil.isEmpty(dateString)) {
				return null;
			}
			ISO8601DateFormat jacksonDateFormat = new ISO8601DateFormat();
			return jacksonDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse a string into a Date object with the provided format
	 * 
	 * @param dateString The string to read a date from
	 * @param dateFormat The format of the date string
	 * 
	 * @return Date object
	 */
	public static Date parseDate(String dateString, String dateFormat) {
		try {
			if (StringUtil.isEmpty(dateString)) {
				return null;
			}
			SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
			return dateParser.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get client offset and timezone, and sends it to JasperReports for printing
	 * 
	 * @param dateString The string to read a date from
	 * @param dateFormat The format of the date string
	 * 
	 * @return Date object
	 */
	public static TimeZone returnClientTimeZone(String timezoneId, Integer timezoneOffset) {
		try {
			if (!timezoneId.isEmpty() && timezoneOffset != null) {
				//Get the user's timezone from variable timezoneId 
				//and create a timezone variable to send it to Jasper
				Calendar cal = Calendar.getInstance();
				TimeZone timeZone = cal.getTimeZone();
				timeZone.setID(timezoneId);
				timeZone.setRawOffset(-(timezoneOffset * 60000));
				return timeZone;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
