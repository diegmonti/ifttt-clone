package iftttclone.channels;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator;
import iftttclone.core.Validator.FieldType;
import iftttclone.exceptions.SchedulerException;

@ChannelTag(name = "Weather", description = "Weather is a channel that provides a set of triggers specifically for weather conditions and temperatures. Data provided by Yahoo! Weather.", withConnection = false)
public class WeatherChannel extends AbstractChannel {
	private final static String BASE_URL = "http://query.yahooapis.com/v1/public/yql?format=json&diagnostics=false&q=";
	private final static String CHARSET = StandardCharsets.UTF_8.name();
	private final static String TIMESTAMP_FORMAT_API = "EEE, dd MMM yyyy hh:mm a z";
	private final static String DATE_FORMAT_API = "dd MMM yyyy";

	@TriggerTag(name = "Tomorrow's weather report", description = "This trigger retrieves tomorrow's weather report at the time you specify.")
	@IngredientTag(name = "CurrTempFahrenheit", description = "Today's temperature registered in degrees Fahrenheit.", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "Today's temperature registered in degrees Celsius.", example = "25")
	@IngredientTag(name = "Condition", description = "Tomorrow's weather condition.", example = "Sunny")
	@IngredientTag(name = "HighTempFahrenheit", description = "Tomorrow's high temperature in degrees Fahrenheit.", example = "72")
	@IngredientTag(name = "HighTempCelsius", description = "Tomorrow's high temperature in degrees Celsius.", example = "32")
	@IngredientTag(name = "LowTempFahrenheit", description = "Tomorrow's low temperature in degrees Fahrenheit.", example = "18")
	@IngredientTag(name = "LowTempCelsius", description = "Tomorrow's low temperature in degrees Celsius.", example = "23")
	@IngredientTag(name = "SunriseAt", description = "The date and time of tomorrow's sunrise.", example = "23/05/2016 05:15")
	@IngredientTag(name = "CheckTime", description = "The time when the weather information was updated.", example = "23/05/2016 13:00")
	public List<Map<String, String>> tomorrowWeatherReport(
			@FieldTag(name = "Location", description = "The location that is checked.", type = FieldType.LOCATION, publishable = false) String location,
			@FieldTag(name = "Time of day", description = "The time at which the trigger will fire.", type = FieldType.TIME) String time) {

		Date now = new Date();

		// Get the time of the user
		Calendar triggerTime = Calendar.getInstance(TimeZone.getTimeZone(this.getUser().getTimezone()));
		Calendar tomorrow = (Calendar) triggerTime.clone();
		String[] timeField = time.split(":");
		triggerTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeField[0]));
		triggerTime.set(Calendar.MINUTE, Integer.parseInt(timeField[1]));
		triggerTime.set(Calendar.SECOND, 0);
		Date ttDate = triggerTime.getTime();

		// Now is after trigger time and lastRun was before trigger time
		if (ttDate.after(now) || this.getLastRun().after(ttDate)) {
			return null;
		}

		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		DateFormat yqlFormat = new SimpleDateFormat(DATE_FORMAT_API, Locale.ENGLISH);
		String query = "select astronomy.sunrise, units.temperature, item.condition.temp, item.condition.text, item.pubDate, item.forecast.high, item.forecast.low "
				+ "from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + location
				+ "\") and item.forecast.date=\"" + yqlFormat.format(tomorrow.getTime()) + "\"";

		JsonNode rootNode = doQuery(query);
		if (rootNode == null) {
			throw new SchedulerException();
		}

		Map<String, String> resEntry = new HashMap<String, String>();
		this.addCommonIngredients(rootNode, resEntry);
		this.addExtraIngredients(rootNode, resEntry);
		resEntry.put("SunriseAt", rootNode.findPath("astronomy").findPath("sunrise").asText());

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		result.add(resEntry);

		return result;
	}

	@TriggerTag(name = "Temperature drops below", description = "This trigger is activated if the current temperature drops below a certain value.")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit.", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius.", example = "25")
	@IngredientTag(name = "Condition", description = "The current weather condition.", example = "Sunny")
	@IngredientTag(name = "CheckTime", description = "The time when the weather information was updated.", example = "23/05/2016 13:00")
	public List<Map<String, String>> currentTemperatureLow(
			@FieldTag(name = "Location", description = "The location that is checked.", type = FieldType.LOCATION, publishable = false) String location,
			@FieldTag(name = "Temperature", description = "The temperature that needs to be checked.", type = FieldType.INTEGER) String temperature,
			@FieldTag(name = "Degrees in", description = "The unit of measure.", type = FieldType.TEMPERATURE) String unit) {
		return currentTemperature(location, temperature, "", unit);
	}

	@TriggerTag(name = "Temperature rises above", description = "This trigger is activated if the current temperature rises above a certain value.")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit.", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius.", example = "25")
	@IngredientTag(name = "Condition", description = "The current weather condition.", example = "Sunny")
	@IngredientTag(name = "CheckTime", description = "The time when the weather information was updated.", example = "23/05/2016 13:00")
	public List<Map<String, String>> currentTemperatureHigh(
			@FieldTag(name = "Location", description = "The location that is checked.", type = FieldType.LOCATION, publishable = false) String location,
			@FieldTag(name = "Temperature", description = "The temperature that needs to be checked.", type = FieldType.INTEGER) String temperature,
			@FieldTag(name = "Degrees in", description = "The unit of measure.", type = FieldType.TEMPERATURE) String unit) {
		return currentTemperature(location, "", temperature, unit);
	}

	@TriggerTag(name = "Temperature below or above", description = "This trigger is activated if the current temperature is below or above a certain threshold.")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit.", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius.", example = "25")
	@IngredientTag(name = "Condition", description = "The current weather condition.", example = "Sunny")
	@IngredientTag(name = "CheckTime", description = "The time when the weather information was updated.", example = "23/05/2016 13:00")
	public List<Map<String, String>> currentTemperature(
			@FieldTag(name = "Location", description = "The location that is checked.", type = FieldType.LOCATION, publishable = false) String location,
			@FieldTag(name = "Temperature below", description = "Fires if the temperature is below this value.", type = FieldType.INTEGER) String lower,
			@FieldTag(name = "Temperature above", description = "Fires if the temperature is above this value.", type = FieldType.INTEGER) String upper,
			@FieldTag(name = "Degrees in", description = "The unit of measure.", type = FieldType.TEMPERATURE) String unit) {

		String query = "select units.temperature, item.condition.temp, item.condition.text, item.pubDate "
				+ "from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + location
				+ "\")";

		JsonNode rootNode = doQuery(query);
		if (rootNode == null) {
			throw new SchedulerException();
		}

		String tempUnit = rootNode.findPath("units").findPath("temperature").asText();
		int currTemp = rootNode.findPath("item").findPath("condition").findPath("temp").asInt();
		currTemp = getTemp(currTemp, tempUnit, unit);

		if (!upper.isEmpty() && (currTemp > Integer.parseInt(upper))) {
			return null;
		}

		if (!lower.isEmpty() && (currTemp < Integer.parseInt(lower))) {
			return null;
		}

		Map<String, String> resEntry = new HashMap<String, String>();
		this.addCommonIngredients(rootNode, resEntry);

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		result.add(resEntry);

		return result;
	}

	@TriggerTag(name = "Sunrise", description = "This trigger fires within 15 minutes of the sunrise.")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit.", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius.", example = "25")
	@IngredientTag(name = "Condition", description = "The current weather condition.", example = "Sunny")
	@IngredientTag(name = "HighTempFahrenheit", description = "Today's high temperature registered in degrees Fahrenheit.", example = "72")
	@IngredientTag(name = "HighTempCelsius", description = "Today's high temperature registered in degrees Celsius.", example = "32")
	@IngredientTag(name = "LowTempFahrenheit", description = "Today's low temperature registered in degrees Fahrenheit.", example = "18")
	@IngredientTag(name = "LowTempCelsius", description = "Today's low temperature registered in degrees Celsius.", example = "23")
	@IngredientTag(name = "CheckTime", description = "The time when the weather information was updated.", example = "23/05/2016 13:00")
	public List<Map<String, String>> sunrise(
			@FieldTag(name = "Location", description = "The location that is checked.", type = FieldType.LOCATION, publishable = false) String location) {

		Date now = new Date();

		String query = "select astronomy.sunrise, units.temperature, item.condition.temp, item.condition.text, item.pubDate, item.forecast.high, item.forecast.low "
				+ "from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + location
				+ "\")";

		JsonNode rootNode = doQuery(query);
		if (rootNode == null) {
			throw new SchedulerException();
		}

		// The sunrise happens in the time zone where the location is
		String sunriseText = rootNode.findPath("astronomy").findPath("sunrise").asText();
		if (sunriseText.isEmpty()) {
			throw new SchedulerException();
		}

		String sunriseDateText = rootNode.findPath("item").findPath("pubDate").asText()
				.replaceFirst("[0-9]+\\:[0-9]+ (AM|PM)", sunriseText);
		DateFormat sunriseFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_API, Locale.ENGLISH);

		Date sunriseDate;
		try {
			sunriseDate = sunriseFormat.parse(sunriseDateText);
		} catch (ParseException e) {
			throw new SchedulerException();
		}

		// Now is after the sunrise and lastRun was before the sunrise
		if (sunriseDate.after(now) || this.getLastRun().after(sunriseDate)) {
			return null;
		}

		Map<String, String> resEntry = new HashMap<String, String>();
		this.addCommonIngredients(rootNode, resEntry);
		this.addExtraIngredients(rootNode, resEntry);

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		result.add(resEntry);

		return result;
	}

	/*
	 * This method returns true if the location is recognized by the service.
	 */
	public static boolean isValidLocation(String location) {
		String query = "select item.condition.temp from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""
				+ location + "\")";

		JsonNode rootNode = doQuery(query);

		if (rootNode == null) {
			return false;
		}

		return true;
	}

	/*
	 * This method performs the connection to the service.
	 */
	private static JsonNode doQuery(String query) {
		try {
			URLConnection connection = new URL(BASE_URL + String.format("%s", URLEncoder.encode(query, CHARSET)))
					.openConnection();
			connection.setRequestProperty("Accept-Charset", CHARSET);
			connection.connect();

			int status = ((HttpURLConnection) connection).getResponseCode();

			if (status != 200) {
				return null;
			}

			InputStream response = connection.getInputStream();

			byte[] jsonIn = new byte[response.available()];
			response.read(jsonIn);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonIn).findPath("query").findPath("results").findPath("channel");

			if (rootNode.isMissingNode()) {
				return null;
			}

			return rootNode;
		} catch (Exception e) {
			return null;
		}
	}

	private void addCommonIngredients(JsonNode rootNode, Map<String, String> result) {
		String tempUnit = rootNode.findPath("units").findPath("temperature").asText();
		int currTemp = rootNode.findPath("item").findPath("condition").findPath("temp").asInt();

		DateFormat fromFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_API, Locale.ENGLISH);
		DateFormat toFormat = new SimpleDateFormat(Validator.TIMESTAMP_FORMAT);
		toFormat.setTimeZone(TimeZone.getTimeZone(this.getUser().getTimezone()));
		Date checkT;

		try {
			checkT = fromFormat.parse(rootNode.findPath("item").findPath("pubDate").asText());
		} catch (ParseException e) {
			checkT = new Date();
		}
		if (checkT == null) {
			checkT = new Date();
		}

		result.put("CurrTempFahrenheit", Integer.toString(getTemp(currTemp, tempUnit, "f")));
		result.put("CurrTempCelsius", Integer.toString(getTemp(currTemp, tempUnit, "c")));
		result.put("Condition", rootNode.findPath("item").findPath("condition").findPath("text").asText());
		result.put("CheckTime", toFormat.format(checkT));
	}

	private void addExtraIngredients(JsonNode rootNode, Map<String, String> result) {
		String tempUnit = rootNode.findPath("units").findPath("temperature").asText();
		int highTemp = rootNode.findPath("item").findPath("forecast").findPath("high").asInt();
		int lowTemp = rootNode.findPath("item").findPath("forecast").findPath("low").asInt();

		result.put("HighTempFahrenheit", Integer.toString(getTemp(highTemp, tempUnit, "f")));
		result.put("HighTempCelsius", Integer.toString(getTemp(highTemp, tempUnit, "c")));
		result.put("LowTempFahrenheit", Integer.toString(getTemp(lowTemp, tempUnit, "f")));
		result.put("LowTempCelsius", Integer.toString(getTemp(lowTemp, tempUnit, "c")));
	}

	private int getTemp(int temp, String from, String to) {
		if (from.toLowerCase().equals("f") && to.toLowerCase().equals("c")) {
			return this.f2c(temp);
		}
		if (from.toLowerCase().equals("c") && to.toLowerCase().equals("f")) {
			return this.c2f(temp);
		}
		return temp;
	}

	private int c2f(int celsius) {
		return (int) Math.round(celsius * 1.8 + 32);
	}

	private int f2c(int fahrenheit) {
		return (int) Math.round((fahrenheit - 32) / 1.8);
	}
}