package iftttclone.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import iftttclone.core.Validator.FieldContext;
import iftttclone.exceptions.InvalidFieldException;
import iftttclone.exceptions.InvalidRequestException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ InvalidRequestException.class })
	public ResponseEntity<Object> handleInvalidRequest(InvalidRequestException e, WebRequest request) {
		InvalidRequestResponse response = new InvalidRequestResponse();
		response.setMessage(e.getMessage());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return handleExceptionInternal(e, response, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	public class InvalidRequestResponse {
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	@ExceptionHandler({ InvalidFieldException.class })
	public ResponseEntity<Object> handleInvalidField(InvalidFieldException e, WebRequest request) {
		InvalidFieldResponse response = new InvalidFieldResponse();
		response.setField(e.getField());
		response.setContext(e.getContext());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return handleExceptionInternal(e, response, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	public class InvalidFieldResponse {
		private String field;
		private FieldContext context;

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

}
