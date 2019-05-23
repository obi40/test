package javaapitest;

import java.text.DateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JavaApiTest {

    public static void main(String[] args) throws Exception {
        TimeZone zone = TimeZone.getTimeZone("Asia/Amman");
        DateFormat format = DateFormat.getDateTimeInstance();
        format.setTimeZone(zone);

        System.out.println(format.format(new Date()));
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/New_York");

        
        String[] ids = TimeZone.getAvailableIDs();
		for (String id : ids) {
			System.out.println(displayTimeZone(TimeZone.getTimeZone(id)));
		}

		System.out.println("\nTotal TimeZone ID " + ids.length);
    }
private static String displayTimeZone(TimeZone tz) {

		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                                  - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		String result = "";
		if (hours > 0) {
			result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
		} else {
			result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
		}

		return result;

	}
}
