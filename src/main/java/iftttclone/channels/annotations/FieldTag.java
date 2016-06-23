package iftttclone.channels.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import iftttclone.core.Validator.FieldType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FieldTag {
	String name();

	String description();

	FieldType type();

	boolean publishable();
}
