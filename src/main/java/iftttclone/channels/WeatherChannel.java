package iftttclone.channels;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.TriggerFieldTag;
import iftttclone.channels.annotations.TriggerTag;

@ChannelTag(name="WeatherChannel", description="The channel that handles wheather", withConnection = false)
public class WeatherChannel extends AbstractChannel {
	private final String baseUrl = "http://query.yahooapis.com/v1/public/yql?format=json&diagnostics=false&q=";
	private final String charset = StandardCharsets.UTF_8.name();

	@TriggerTag(name = "tomorrowReport", description = "It is activated in order to give the user the forecast of the next day")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius", example = "25")
	@IngredientTag(name = "Condition", description = "The weather condition", example = "Sunny")
	@IngredientTag(name = "HighTempFahrenheit", description = "Today's high temperature registered in degrees Fahrenheit", example = "72")
	@IngredientTag(name = "HighTempCelsius", description = "Today's high temperature registered in degrees Celsius", example = "32")
	@IngredientTag(name = "LowTempFahrenheit", description = "Today's low temperature registered in degrees Fahrenheit", example = "18")
	@IngredientTag(name = "LowTempCelsius", description = "Today's low temperature registered in degrees Celsius", example = "23")
	@IngredientTag(name = "SunriseTime", description = "When the sunrise will take place", example = "23/05/2016 05:15")
	@IngredientTag(name = "CheckTime", description = "When these measurements were taken", example = "23/05/2016 13:09")
	public Map<String, String> tomorrowWeatherReport(
			@TriggerFieldTag(name = "location", description="The location the user is intrested in", isPublishable = false) String location,
			@TriggerFieldTag(name = "checkHour", description="The hour the trigger will fire", isPublishable = true) String hour, 
			@TriggerFieldTag(name = "checkMinutes", description="The minutes the trigger will fire", isPublishable = true) String minutes){
		
		Calendar triggerTime = Calendar.getInstance(TimeZone.getTimeZone(this.getUser().getTimezone()));	// now for the user
		Date now = triggerTime.getTime();
		Calendar tomorrow = (Calendar) triggerTime.clone();
		triggerTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		triggerTime.set(Calendar.MINUTE, Integer.parseInt(minutes));
		Date ttDate = triggerTime.getTime();
		/*System.err.println("nowDate: " + now);
		System.err.println("TTDate: " + ttDate);
		System.err.println("LastRunDate: " + this.getLastRun());*/
		// problem with for example 23:59 since most likely this would run later (next day) and so will never fire
			// ifttt has intervals of 15 minutes so the last time is 23:45, something like this can be done
		if(ttDate.after(now) || this.getLastRun().after(ttDate)){	// now is after tt and lastRun was before tt
			return null;
		}
		
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		DateFormat yqlFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		String yql = "select astronomy.sunrise, units.temperature, item.condition.temp, item.condition.text, item.pubDate, item.forecast.high, item.forecast.low "
				+ "from weather.forecast where woeid = " + location + " "
						+ "and item.forecast.date=\"" + yqlFormat.format(tomorrow.getTime()) + "\"";
		String query;
		try {
			query = String.format("%s", URLEncoder.encode(yql,charset));
		} catch (UnsupportedEncodingException e) {	// should not happen (500)
			return null;
		}
		
		JsonNode rootNode = doQuery(query);
		if(rootNode == null){	// should not happen (404 or 400)
			return null;
		}
		
		Map<String, String> result = new HashMap<String, String>();
		this.addCommonIngredients(rootNode, result);
		this.addExtraIngredients(rootNode, result);
		result.put("SunriseTime", rootNode.findPath("astronomy").findPath("sunrise").asText());
		
		return result;
	}
	
	@TriggerTag(name ="currentTemperature", description = "This trigger is activated if current temperature is above or below a certain threshold, if one of the thresholds is not needed leave it blank")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius", example = "25")
	@IngredientTag(name = "Condition", description = "The weather condition", example = "Sunny")
	@IngredientTag(name = "CheckTime", description = "When these measurements were taken", example = "23/05/2016 13:09")
	public Map<String, String> currentTemperature(
			@TriggerFieldTag(name = "location", description="The location the user is intrested in", isPublishable = false) String location,
			@TriggerFieldTag(name = "lowerTemp", description="Fires if more than or equal to this value", isPublishable = true) String lower, 
			@TriggerFieldTag(name = "upperTemp", description="Fires if less than or equal to this value", isPublishable = true) String upper, 
			@TriggerFieldTag(name = "degrees", description="The unit of measure", isPublishable = true) String unit){
		
		String yql = "select units.temperature, item.condition.temp, item.condition.text, item.pubDate "
				+ "from weather.forecast where woeid = " + location;
		String query;
		try {
			query = String.format("%s", URLEncoder.encode(yql,charset));
		} catch (UnsupportedEncodingException e) {	// should not happen (500)
			return null;
		}
		
		JsonNode rootNode = doQuery(query);
		if(rootNode == null){	// should not happen (404 or 400)
			return null;
		}
		
		String tempUnit = rootNode.findPath("units").findPath("temperature").asText();	// "" if not found
		int currTemp = rootNode.findPath("item").findPath("condition").findPath("temp").asInt();	// 0 if not found
		currTemp = getTemp(currTemp, tempUnit, unit);
		/*System.err.println("currTemp: " + currTemp);
		System.err.println("upperTemp: " + upper);
		System.err.println("lowerTemp: " + lower);*/
		if(!upper.isEmpty() && (currTemp > Integer.parseInt(upper))){
			return null;
		}
		if(!lower.isEmpty() && (currTemp < Integer.parseInt(lower))){
			return null;
		}
		
		Map<String, String> result = new HashMap<String, String>();
		this.addCommonIngredients(rootNode, result);
		
		return result;
	}
	
	@TriggerTag(name ="sunriseEvent", description = "This trigger activates itself at sunrise")
	@IngredientTag(name = "CurrTempFahrenheit", description = "The current temperature registered in degrees Fahrenheit", example = "40")
	@IngredientTag(name = "CurrTempCelsius", description = "The current temperature registered in degrees Celsius", example = "25")
	@IngredientTag(name = "Condition", description = "The weather condition", example = "Sunny")
	@IngredientTag(name = "CheckTime", description = "When these measurements were taken", example = "23/05/2016 13:09")
	@IngredientTag(name = "HighTempFahrenheit", description = "Today's high temperature registered in degrees Fahrenheit", example = "72")
	@IngredientTag(name = "HighTempCelsius", description = "Today's high temperature registered in degrees Celsius", example = "32")
	@IngredientTag(name = "LowTempFahrenheit", description = "Today's low temperature registered in degrees Fahrenheit", example = "18")
	@IngredientTag(name = "LowTempCelsius", description = "Today's low temperature registered in degrees Celsius", example = "23")
	public Map<String, String> sunrise(
			@TriggerFieldTag(name = "location", description="The location the user is intrested in", isPublishable = false) String location){
		
		Calendar triggerTime = Calendar.getInstance(TimeZone.getTimeZone(this.getUser().getTimezone()));	// now for the user
		Date now = triggerTime.getTime();
		DateFormat yqlFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		String yql = "select astronomy.sunrise, units.temperature, item.condition.temp, item.condition.text, item.pubDate, item.forecast.high, item.forecast.low "
				+ "from weather.forecast where woeid = " + location + " "
						+ "and item.forecast.date=\"" + yqlFormat.format(now) + "\"";
		String query;
		try {
			query = String.format("%s", URLEncoder.encode(yql,charset));
		} catch (UnsupportedEncodingException e) {	// should not happen (500)
			return null;
		}
		
		JsonNode rootNode = doQuery(query);
		if(rootNode == null){	// should not happen (404 or 400)
			return null;
		}
		
		String sunriseText = rootNode.findPath("astronomy").findPath("sunrise").asText();
		if(sunriseText.isEmpty()){	// should not happen (404 or 400)
			return null;
		}
		DateFormat sunriseFormat = new SimpleDateFormat("hh:mm a");	// not sure if giving today so following
		Date sunriseDate;
		try {
			sunriseDate = sunriseFormat.parse(sunriseText);
		} catch (ParseException e) {	// should not happen (500)
			return null;
		}
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(sunriseDate);
		triggerTime.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY));
		triggerTime.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE));
		Date ttDate = triggerTime.getTime();
		/*System.err.println("nowDate: " + now);
		System.err.println("TTDate: " + ttDate);
		System.err.println("LastRunDate: " + this.getLastRun());*/
		if(ttDate.after(now) || this.getLastRun().after(ttDate)){	// now is after tt and lastRun was before tt
			return null;
		}
		
		Map<String, String> result = new HashMap<String, String>();
		this.addCommonIngredients(rootNode, result);
		this.addExtraIngredients(rootNode, result);
		
		return result;
	}
	
	// Utility methods
	private JsonNode doQuery(String query){
		try{
			URLConnection connection = new URL(baseUrl + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			connection.connect();
			
			int status = ((HttpURLConnection) connection).getResponseCode();
			if(status != 200){
				return null;
			}
			
			InputStream response = connection.getInputStream();
			
			byte[] jsonIn = new byte[response.available()];
			response.read(jsonIn);
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonIn).findPath("query").findPath("results").findPath("channel");
			//JsonNode rootNode = objectMapper.readTree(jsonIn);
			
			if(rootNode.isMissingNode()){
				return null;
			}
			
			return rootNode;
		} catch (Exception e){
			return null;
		}
	}
	
	private void addCommonIngredients(JsonNode rootNode, Map<String, String> result){
		String tempUnit = rootNode.findPath("units").findPath("temperature").asText();	// "" if not found
		int currTemp = rootNode.findPath("item").findPath("condition").findPath("temp").asInt();	// 0 if not found
		DateFormat fromFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a z", Locale.ENGLISH);
		DateFormat toFormat = new SimpleDateFormat("dd MMM yyyy HH:mm z", Locale.ENGLISH);
		Date checkT;
		try{
			checkT = fromFormat.parse(rootNode.findPath("item").findPath("pubDate").asText());
		} catch (ParseException e){
			checkT = Calendar.getInstance().getTime();
		}
		if(checkT == null){
			checkT = Calendar.getInstance().getTime();
		}
		
		result.put("CurrTempFahrenheit", Integer.toString(getTemp(currTemp, tempUnit, "f")));
		result.put("CurrTempCelsius", Integer.toString(getTemp(currTemp, tempUnit, "c")));
		result.put("Condition", rootNode.findPath("item").findPath("condition").findPath("text").asText());
		result.put("CheckTime", toFormat.format(checkT));
	}
	
	private void addExtraIngredients(JsonNode rootNode, Map<String, String> result){
		String tempUnit = rootNode.findPath("units").findPath("temperature").asText();	// "" if not found
		int highTemp = rootNode.findPath("item").findPath("forecast").findPath("high").asInt();	// 0 if not found
		int lowTemp = rootNode.findPath("item").findPath("forecast").findPath("low").asInt();	// 0 if not found
		
		result.put("HighTempFahrenheit", Integer.toString(getTemp(highTemp, tempUnit, "f")));
		result.put("HighTempCelsius", Integer.toString(getTemp(highTemp, tempUnit, "c")));
		result.put("LowTempFahrenheit", Integer.toString(getTemp(lowTemp, tempUnit, "f")));
		result.put("LowTempCelsius", Integer.toString(getTemp(lowTemp, tempUnit, "c")));
	}
	
	private int getTemp(int temp, String from, String to){
		if(from.toLowerCase().equals("f") && to.toLowerCase().equals("c")){
			return this.f2c(temp);
		}
		if(from.toLowerCase().equals("c") && to.toLowerCase().equals("f")){
			return this.c2f(temp);
		}
		return temp;
	}
	
	private int c2f(int celsius) {
        return (int) Math.round(celsius*1.8+32);
    }
	
	private int f2c(int fahrenheit) {
        return (int) Math.round((fahrenheit-32)/1.8);
    }
}