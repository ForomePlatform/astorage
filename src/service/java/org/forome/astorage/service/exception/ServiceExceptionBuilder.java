package org.forome.astorage.service.exception;

import org.forome.astorage.core.exception.AStorageException;
import org.forome.astorage.core.exception.ExceptionFactory;

import java.util.Collections;

public class ServiceExceptionBuilder {

	private static final ExceptionFactory EXCEPTION_FACTORY = new ExceptionFactory();


	public static AStorageException buildInvalidOperation(String comment) {
		return EXCEPTION_FACTORY.build("invalid_operation", comment);
	}

	public static AStorageException buildServerTimeoutException() {
		return EXCEPTION_FACTORY.build("server_timeout");
	}

	public static AStorageException buildEmptyValueException(String fieldName) {
		return EXCEPTION_FACTORY.build("empty_value", Collections.singletonMap("fieldName", fieldName));
	}

	public static AStorageException buildInvalidJsonException() {
		return buildInvalidJsonException(null);
	}

	public static AStorageException buildInvalidJsonException(Throwable cause) {
		return EXCEPTION_FACTORY.build("invalid_json", cause);
	}

	public static AStorageException buildGraphQLInvalidSyntaxException() {
		return EXCEPTION_FACTORY.build("graphql_invalid_syntax");
	}

	public static AStorageException buildGraphQLValidationException() {
		return buildGraphQLValidationException(null);
	}

	public static AStorageException buildGraphQLValidationException(String message) {
		return EXCEPTION_FACTORY.build("graphql_validation_error", message);
	}
}
