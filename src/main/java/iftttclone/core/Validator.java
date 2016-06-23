package iftttclone.core;

import org.springframework.stereotype.Component;

@Component
public class Validator {
	
	public static enum FieldType {
		TEXT, NULLABLETEXT, LONGTEXT, EMAIL, NULLABLEINTEGER, TIMESTAMP, TIME, TEMPERATURE, LOCATION
	}

}
