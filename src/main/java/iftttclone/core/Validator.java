package iftttclone.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import iftttclone.channels.WeatherChannel;
import iftttclone.exceptions.InvalidRequestException;

@Component
public class Validator {
	/**
	 * The maximum length of a field that can be stored in the database.
	 */
	public static final int MAX_VARCHAR = 512;
	/**
	 * The default format for the TIMESTAMP data type.
	 */
	public static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm";
	/**
	 * The default format for the TIME data type.
	 */
	public static final String TIME_FORMAT = "HH:mm";

	/**
	 * This enumeration contains the possible data types for a trigger or an
	 * action field. Their value is serialized in the database as a string.
	 */
	public static enum FieldType {
		TEXT, NULLABLETEXT, LONGTEXT, EMAIL, INTEGER, TIMESTAMP, TIME, TEMPERATURE, LOCATION
	}

	/**
	 * This method is in charge of validating the fields of a recipe, according
	 * to the specified FieldType. If the validation is not successful an
	 * exception is thrown.
	 */
	public static void validate(String value, FieldType type, String field) {
		if (value == null) {
			throw new InvalidRequestException("The field " + field + " cannot be null.");
		}

		if (value.length() > MAX_VARCHAR) {
			throw new InvalidRequestException(
					"The field " + field + " cannot be longer than " + MAX_VARCHAR + " characters.");
		}

		switch (type) {
		case TEXT:
			validateText(value, field);
			break;
		case NULLABLETEXT:
			// This is always correct
			break;
		case LONGTEXT:
			validateText(value, field);
			break;
		case EMAIL:
			if (!isValidEmail(value)) {
				throw new InvalidRequestException("The field " + field + " must contain a valid email.");
			}
			break;
		case INTEGER:
			validateInteger(value, field);
			break;
		case TIMESTAMP:
			validateTimestamp(value, field);
			break;
		case TIME:
			validateTime(value, field);
			break;
		case TEMPERATURE:
			if (!value.equals("C") && !value.equals("F")) {
				throw new InvalidRequestException("The field " + field + " must contain a temperature unit.");
			}
			break;
		case LOCATION:
			if (!WeatherChannel.isValidLocation(value)) {
				throw new InvalidRequestException("The field " + field + " must contain a valid location.");
			}
			break;
		}
	}

	private static void validateText(String value, String field) {
		if (value.equals("")) {
			throw new InvalidRequestException("The field " + field + " cannot be empty.");
		}
	}

	private static void validateInteger(String value, String field) {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new InvalidRequestException("The field " + field + " must contain an integer.");
		}
	}

	private static void validateTimestamp(String value, String field) {
		DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
		try {
			format.parse(value);
		} catch (ParseException e) {
			throw new InvalidRequestException(
					"The field " + field + " must contain a timestamp with format " + TIMESTAMP_FORMAT + ".");
		}
	}

	private static void validateTime(String value, String field) {
		DateFormat format = new SimpleDateFormat(TIME_FORMAT);
		try {
			format.parse(value);
		} catch (ParseException e) {
			throw new InvalidRequestException(
					"The field " + field + " must contain a time with format " + TIME_FORMAT + ".");
		}
	}

	/**
	 * This method checks if a string is a formally correct email address.
	 */
	public static boolean isValidEmail(String email) {
		String pattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}

}
