package iftttclone.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import iftttclone.exceptions.InvalidRequestException;

public class TimezoneManager {

	private static final List<TimezoneValue> TIMEZONES = new ArrayList<TimezoneValue>();

	static {
		TIMEZONES.add(new TimezoneValue("Asia/Kabul", "(GMT +04:30) Kabul"));
		TIMEZONES.add(new TimezoneValue("America/Anchorage", "(GMT -09:00) Alaska"));
		TIMEZONES.add(new TimezoneValue("Asia/Riyadh", "(GMT +03:00) Kuwait, Riyadh"));
		TIMEZONES.add(new TimezoneValue("Asia/Dubai", "(GMT +04:00) Abu Dhabi, Muscat"));
		TIMEZONES.add(new TimezoneValue("Asia/Baghdad", "(GMT +03:00) Baghdad"));
		TIMEZONES.add(new TimezoneValue("America/Buenos_Aires", "(GMT -03:00) Buenos Aires"));
		TIMEZONES.add(new TimezoneValue("America/Halifax", "(GMT -04:00) Atlantic Time (Canada)"));
		TIMEZONES.add(new TimezoneValue("Australia/Darwin", "(GMT +09:30) Darwin"));
		TIMEZONES.add(new TimezoneValue("Australia/Sydney", "(GMT +10:00) Canberra, Melbourne, Sydney"));
		TIMEZONES.add(new TimezoneValue("Asia/Baku", "(GMT +04:00) Baku"));
		TIMEZONES.add(new TimezoneValue("Atlantic/Azores", "(GMT -01:00) Azores"));
		TIMEZONES.add(new TimezoneValue("Asia/Dhaka", "(GMT +06:00) Dhaka"));
		TIMEZONES.add(new TimezoneValue("America/Regina", "(GMT -06:00) Saskatchewan"));
		TIMEZONES.add(new TimezoneValue("Atlantic/Cape_Verde", "(GMT -01:00) Cape Verde Is."));
		TIMEZONES.add(new TimezoneValue("Asia/Yerevan", "(GMT +04:00) Yerevan"));
		TIMEZONES.add(new TimezoneValue("Australia/Adelaide", "(GMT +09:30) Adelaide"));
		TIMEZONES.add(new TimezoneValue("America/Guatemala", "(GMT -06:00) Central America"));
		TIMEZONES.add(new TimezoneValue("Asia/Almaty", "(GMT +06:00) Astana"));
		TIMEZONES.add(new TimezoneValue("America/Cuiaba", "(GMT -04:00) Cuiaba"));
		TIMEZONES.add(new TimezoneValue("Europe/Budapest", "(GMT +01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague"));
		TIMEZONES.add(new TimezoneValue("Europe/Warsaw", "(GMT +01:00) Sarajevo, Skopje, Warsaw, Zagreb"));
		TIMEZONES.add(new TimezoneValue("Pacific/Guadalcanal", "(GMT +11:00) Solomon Is., New Caledonia"));
		TIMEZONES.add(new TimezoneValue("America/Mexico_City", "(GMT -06:00) Guadalajara, Mexico City, Monterrey"));
		TIMEZONES.add(new TimezoneValue("America/Chicago", "(GMT -06:00) Central Time (US & Canada)"));
		TIMEZONES.add(new TimezoneValue("Asia/Shanghai", "(GMT +08:00) Beijing, Chongqing, Hong Kong, Urumqi"));
		TIMEZONES.add(new TimezoneValue("Etc/GMT+12", "(GMT -12:00) International Date Line West"));
		TIMEZONES.add(new TimezoneValue("Africa/Nairobi", "(GMT +03:00) Nairobi"));
		TIMEZONES.add(new TimezoneValue("Australia/Brisbane", "(GMT +10:00) Brisbane"));
		TIMEZONES.add(new TimezoneValue("Europe/Minsk", "(GMT +02:00) Minsk"));
		TIMEZONES.add(new TimezoneValue("America/Sao_Paulo", "(GMT -03:00) Brasilia"));
		TIMEZONES.add(new TimezoneValue("America/New_York", "(GMT -05:00) Eastern Time (US & Canada)"));
		TIMEZONES.add(new TimezoneValue("Africa/Cairo", "(GMT +02:00) Cairo"));
		TIMEZONES.add(new TimezoneValue("Asia/Yekaterinburg", "(GMT +05:00) Ekaterinburg"));
		TIMEZONES.add(new TimezoneValue("Pacific/Fiji", "(GMT +12:00) Fiji, Marshall Is."));
		TIMEZONES.add(new TimezoneValue("Europe/Kiev", "(GMT +02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius"));
		TIMEZONES.add(new TimezoneValue("Asia/Tbilisi", "(GMT +04:00) Tbilisi"));
		TIMEZONES.add(new TimezoneValue("Europe/London", "(GMT) Dublin, Edinburgh, Lisbon, London"));
		TIMEZONES.add(new TimezoneValue("America/Godthab", "(GMT -03:00) Greenland"));
		TIMEZONES.add(new TimezoneValue("Atlantic/Reykjavik", "(GMT) Monrovia, Reykjavik"));
		TIMEZONES.add(new TimezoneValue("Europe/Istanbul", "(GMT +02:00) Athens, Bucharest, Istanbul"));
		TIMEZONES.add(new TimezoneValue("Pacific/Honolulu", "(GMT -10:00) Hawaii"));
		TIMEZONES.add(new TimezoneValue("Asia/Calcutta", "(GMT +05:30) Chennai, Kolkata, Mumbai, New Delhi"));
		TIMEZONES.add(new TimezoneValue("Asia/Tehran", "(GMT +03:30) Tehran"));
		TIMEZONES.add(new TimezoneValue("Asia/Jerusalem", "(GMT +02:00) Jerusalem"));
		TIMEZONES.add(new TimezoneValue("Asia/Amman", "(GMT +02:00) Amman"));
		TIMEZONES.add(new TimezoneValue("Asia/Kamchatka", "(GMT +12:00) Petropavlovsk-Kamchatsky - Old"));
		TIMEZONES.add(new TimezoneValue("Asia/Seoul", "(GMT +09:00) Seoul"));
		TIMEZONES.add(new TimezoneValue("Asia/Magadan", "(GMT +11:00) Magadan"));
		TIMEZONES.add(new TimezoneValue("Indian/Mauritius", "(GMT +04:00) Port Louis"));
		TIMEZONES.add(new TimezoneValue("Etc/GMT+2", "(GMT -02:00) Mid-Atlantic"));
		TIMEZONES.add(new TimezoneValue("Asia/Beirut", "(GMT +02:00) Beirut"));
		TIMEZONES.add(new TimezoneValue("America/Montevideo", "(GMT -03:00) Montevideo"));
		TIMEZONES.add(new TimezoneValue("Africa/Casablanca", "(GMT) Casablanca"));
		TIMEZONES.add(new TimezoneValue("America/Chihuahua", "(GMT -07:00) Chihuahua, La Paz, Mazatlan"));
		TIMEZONES.add(new TimezoneValue("America/Denver", "(GMT -07:00) Mountain Time (US & Canada)"));
		TIMEZONES.add(new TimezoneValue("Asia/Rangoon", "(GMT +06:30) Yangon (Rangoon)"));
		TIMEZONES.add(new TimezoneValue("Asia/Novosibirsk", "(GMT +06:00) Novosibirsk"));
		TIMEZONES.add(new TimezoneValue("Africa/Windhoek", "(GMT +02:00) Windhoek"));
		TIMEZONES.add(new TimezoneValue("Asia/Katmandu", "(GMT +05:45) Kathmandu"));
		TIMEZONES.add(new TimezoneValue("Pacific/Auckland", "(GMT +12:00) Auckland, Wellington"));
		TIMEZONES.add(new TimezoneValue("America/St_Johns", "(GMT -03:30) Newfoundland"));
		TIMEZONES.add(new TimezoneValue("Asia/Irkutsk", "(GMT +08:00) Irkutsk"));
		TIMEZONES.add(new TimezoneValue("Asia/Krasnoyarsk", "(GMT +07:00) Krasnoyarsk"));
		TIMEZONES.add(new TimezoneValue("America/Santiago", "(GMT -04:00) Santiago"));
		TIMEZONES.add(new TimezoneValue("America/Tijuana", "(GMT -08:00) Baja California"));
		TIMEZONES.add(new TimezoneValue("America/Los_Angeles", "(GMT -08:00) Pacific Time (US & Canada)"));
		TIMEZONES.add(new TimezoneValue("Asia/Karachi", "(GMT +05:00) Islamabad, Karachi"));
		TIMEZONES.add(new TimezoneValue("America/Asuncion", "(GMT -04:00) Asuncion"));
		TIMEZONES.add(new TimezoneValue("Europe/Paris", "(GMT +01:00) Brussels, Copenhagen, Madrid, Paris"));
		TIMEZONES.add(new TimezoneValue("Europe/Moscow", "(GMT +03:00) Moscow, St. Petersburg, Volgograd"));
		TIMEZONES.add(new TimezoneValue("America/Cayenne", "(GMT -03:00) Cayenne, Fortaleza"));
		TIMEZONES.add(new TimezoneValue("America/Bogota", "(GMT -05:00) Bogota, Lima, Quito"));
		TIMEZONES.add(new TimezoneValue("America/La_Paz", "(GMT -04:00) Georgetown, La Paz, Manaus, San Juan"));
		TIMEZONES.add(new TimezoneValue("Pacific/Samoa", "(GMT -11:00) Samoa"));
		TIMEZONES.add(new TimezoneValue("Asia/Bangkok", "(GMT +07:00) Bangkok, Hanoi, Jakarta"));
		TIMEZONES.add(new TimezoneValue("Asia/Singapore", "(GMT +08:00) Kuala Lumpur, Singapore"));
		TIMEZONES.add(new TimezoneValue("Africa/Johannesburg", "(GMT +02:00) Harare, Pretoria"));
		TIMEZONES.add(new TimezoneValue("Asia/Colombo", "(GMT +05:30) Sri Jayawardenepura"));
		TIMEZONES.add(new TimezoneValue("Asia/Damascus", "(GMT +02:00) Damascus"));
		TIMEZONES.add(new TimezoneValue("Asia/Taipei", "(GMT +08:00) Taipei"));
		TIMEZONES.add(new TimezoneValue("Australia/Hobart", "(GMT +10:00) Hobart"));
		TIMEZONES.add(new TimezoneValue("Asia/Tokyo", "(GMT +09:00) Osaka, Sapporo, Tokyo"));
		TIMEZONES.add(new TimezoneValue("Pacific/Tongatapu", "(GMT +13:00) Nuku'alofa"));
		TIMEZONES.add(new TimezoneValue("Asia/Ulaanbaatar", "(GMT +08:00) Ulaanbaatar"));
		TIMEZONES.add(new TimezoneValue("America/Indianapolis", "(GMT -05:00) Indiana (East)"));
		TIMEZONES.add(new TimezoneValue("America/Phoenix", "(GMT -07:00) Arizona"));
		TIMEZONES.add(new TimezoneValue("Etc/GMT", "(GMT) Coordinated Universal Time"));
		TIMEZONES.add(new TimezoneValue("Etc/GMT-12", "(GMT +12:00) Coordinated Universal Time+12"));
		TIMEZONES.add(new TimezoneValue("Etc/GMT+2", "(GMT -02:00) Coordinated Universal Time-02"));
		TIMEZONES.add(new TimezoneValue("Etc/GMT+11", "(GMT -11:00) Coordinated Universal Time-11"));
		TIMEZONES.add(new TimezoneValue("America/Caracas", "(GMT -04:30) Caracas"));
		TIMEZONES.add(new TimezoneValue("Asia/Vladivostok", "(GMT +10:00) Vladivostok"));
		TIMEZONES.add(new TimezoneValue("Australia/Perth", "(GMT +08:00) Perth"));
		TIMEZONES.add(new TimezoneValue("Africa/Lagos", "(GMT +01:00) West Central Africa"));
		TIMEZONES.add(new TimezoneValue("Europe/Berlin", "(GMT +01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"));
		TIMEZONES.add(new TimezoneValue("Asia/Tashkent", "(GMT +05:00) Tashkent"));
		TIMEZONES.add(new TimezoneValue("Pacific/Port_Moresby", "(GMT +10:00) Guam, Port Moresby"));
		TIMEZONES.add(new TimezoneValue("Asia/Yakutsk", "(GMT +09:00) Yakutsk"));
	}

	private static final TimezoneManager INSTANCE = new TimezoneManager();

	public static final TimezoneManager getInstance() {
		return INSTANCE;
	}

	private Map<String, TimezoneValue> idMap = new HashMap<String, TimezoneValue>();
	private Map<String, TimezoneValue> nameMap = new HashMap<String, TimezoneValue>();
	private Set<String> nameSet = new TreeSet<String>();

	private TimezoneManager() {
		Set<String> timezones = new HashSet<String>();

		for (String id : TimeZone.getAvailableIDs()) {
			timezones.add(id);
		}

		for (TimezoneValue value : TIMEZONES) {
			if (!timezones.contains(value.id)) {
				throw new IllegalStateException("Unknown timezone: " + value.id);
			}
			idMap.put(value.id, value);
			nameMap.put(value.name, value);
			nameSet.add(value.name);
		}
	}

	public Set<String> getTimezones() {
		return nameSet;
	}

	public String getNameFromId(String id) {
		if (!idMap.containsKey(id)) {
			throw new InvalidRequestException("The provided timezone is unknown");
		}
		return idMap.get(id).name;
	}

	public String getIdFromName(String name) {
		if (!nameMap.containsKey(name)) {
			throw new InvalidRequestException("The provided timezone is unknown");
		}
		return nameMap.get(name).id;
	}

	private static final class TimezoneValue implements Comparable<TimezoneValue> {
		private final String id;
		private final String name;

		public TimezoneValue(String id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TimezoneValue other = (TimezoneValue) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public int compareTo(TimezoneValue that) {
			return this.name.compareTo(that.name);
		}

	}

}