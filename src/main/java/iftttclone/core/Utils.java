package iftttclone.core;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class Utils {

	public static String getURL(HttpServletRequest req) {
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();

		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);

		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		return url.toString();
	}

	public static boolean isValidEmail(String email) {
		String pattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean isValidTimezone(String timezone) {
		List<String> allTimezones = Arrays.asList(TimeZone.getAvailableIDs());

		if (allTimezones.contains(timezone.replace(' ', '_'))) {
			return true;
		}

		return false;
	}

}
