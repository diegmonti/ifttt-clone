package iftttclone.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import iftttclone.core.Validator.FieldContext;

/**
 * This exception represent a validation error of a field.
 */
@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidFieldException extends RuntimeException {
	private String field;
	private FieldContext context;

	public InvalidFieldException(String field, FieldContext context) {
		this.field = field;
		this.context = context;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public FieldContext getContext() {
		return context;
	}

	public void setContext(FieldContext context) {
		this.context = context;
	}

}
