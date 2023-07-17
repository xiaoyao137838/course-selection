package com.example.courseselection.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;


public final class PaginationUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(PaginationUtils.class);
	
	private PaginationUtils() {
		
	}

	public static <T> HttpHeaders generatePaginationHttpHeaders(Page<T> page, String baseUrl) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
		logger.info("The current page is {}", page.getNumber());
		logger.info("The total pages are {}", page.getTotalPages());
		
		String link = "";
		if (page.getNumber() + 1 < page.getTotalPages()) {
			link = "<" + generateUri(baseUrl, page.getNumber() + 1, page.getSize()) +
					">, rel=\"next\"; ";
		}
		
		if (page.getNumber() > 0) {
			link += "<" + generateUri(baseUrl, page.getNumber() - 1, page.getSize()) +
					">, rel=\"prev\"; ";
		}
		
		int lastPage = 0;
		if (page.getTotalPages() > 0) {
			lastPage = page.getTotalPages() - 1;
		}
		
		link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">, rel=\"last\"; ";
		link += "<" + generateUri(baseUrl, 0, page.getSize()) + ">, rel=\"first\";";
		headers.add(HttpHeaders.LINK, link);
		
		return headers;
		
	}

	private static String generateUri(String baseUrl, int currentPage, int size) {
		
		return UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("page", currentPage)
				.queryParam("size", size)
				.toUriString();
	}

}
