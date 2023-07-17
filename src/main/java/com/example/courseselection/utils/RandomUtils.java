package com.example.courseselection.utils;


public final class RandomUtils {
	
	private static final int DEF_COUNT = 20;
	
	private RandomUtils() {
		
	}

	public static String generatePassword() {
//		return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
		return "dummy";
	}
	
	public static String generateActivationKey() {
		return "dummy";
	}
	
	public static String generateResetKey() {
		return "dummy";
	}

}
