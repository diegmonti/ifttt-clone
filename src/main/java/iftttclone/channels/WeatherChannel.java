package iftttclone.channels;

import java.util.Date;
import java.util.Map;

import iftttclone.channels.annotation.ChannelTag;
import iftttclone.channels.annotation.IngredientTag;
import iftttclone.channels.annotation.TriggerFieldTag;
import iftttclone.channels.annotation.TriggerTag;

@ChannelTag(name="WeatherChannel", description="The channel that handles wheather")
public class WeatherChannel {

	@TriggerTag(name="tomorrowReport", description = "it is activated in order to give the user the forecast of the next day")
	public Map<String, String> tomorrowWeatherReport(
			@TriggerFieldTag(name="forecastTime", description="The hour the user is intrested in") Date reportTime){
		
		return null;
	}
	
	@TriggerTag(name="currentWeather", description = "this trigger is activated if current weather is above or below a certain threshold")
	@IngredientTag(name= "Current Temperature", description = "The current temperature registered", example = "25 degrees")
	public Map<String, String> currentWeather(@TriggerFieldTag(name="threshold", description="The threshold requested by the user") int threshold){
		
		return null;
	}
	
	@TriggerTag(name="sunriseEvent", description = "this trigger activates itself at sunrise")
	public Map<String, String> sunrise(){
		
		return null;
	}	
	
			
}
