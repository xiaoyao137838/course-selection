package com.example.courseselection.utils;

import org.springframework.http.HttpHeaders;

public final class HeaderUtils {
	
	private static final String APPLICATION_NAME = "Course-Selection";
	
	private HeaderUtils() {
		
	}

	public static HttpHeaders createAlert(String message, String param) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-" + APPLICATION_NAME + "-Alert", message);
		headers.add("X-" + APPLICATION_NAME + "-Params", param);
		return headers;
	}

	public static HttpHeaders createEntityDeletionAlert(String entityName, String username) {
		return createAlert("A user is deleted by Admin: ".concat(entityName), username);
	}

	public static HttpHeaders createEntityUpdateAlert(String entityName, String username) {
		return createAlert("A user is upadated by Admin: ".concat(entityName), username);
	}

	public static HttpHeaders createEntityCreationAlert(String entityName, String username) {
		return createAlert("A user is created by Admin: ".concat(entityName), username);
	}

}
