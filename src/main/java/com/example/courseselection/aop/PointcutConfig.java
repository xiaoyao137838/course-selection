package com.example.courseselection.aop;

import org.aspectj.lang.annotation.Pointcut;

public class PointcutConfig {
	
	@Pointcut("execution(* com.example.courseselection.*.*.*(..))")
	public void allPackagesConfig() {}
	
	@Pointcut("execution(* com.example.courseselection.web.*.*(..))")
	public void webPackageConfig() {}
	
	@Pointcut("within(com.example.courseselection.service..*)")
	public void servicePackageConfig() {}
	
	@Pointcut("within(@org.springframework.stereotype.Repository *)")
	public void repositoryPackageConfig() {}
	
	@Pointcut("bean(*Service*)")
	public void packageConfigUsingServiceBean() {}
	
	@Pointcut("@annotation(com.example.courseselection.annotation.TrackTime)") 
	public void trackTimeAnnotation() {}

}
