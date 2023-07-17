package com.example.courseselection.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.example.courseselection.utils.Constants;

@Configuration
@Aspect
public class LoggingAspect {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final Environment env;
	
	public LoggingAspect(Environment env) {
		super();
		this.env = env;
	}

	@Before("com.example.courseselection.aop.PointcutConfig.allPackagesConfig()")
	public void logBeforeExecution(JoinPoint joinPoint) {
		logger.info("Before Aspect - {} is called with args: {}", 
				joinPoint, joinPoint.getArgs());
	}
	
	@After("com.example.courseselection.aop.PointcutConfig.webPackageConfig()")
	public void logAfterExecution(JoinPoint joinPoint) {
		logger.info("After Aspect - {} is called with args: {}", 
				joinPoint, joinPoint.getArgs());
	}
	
	@SuppressWarnings("deprecation")
	@AfterThrowing(pointcut="com.example.courseselection.aop.PointcutConfig.servicePackageConfig()",
			throwing="exception")
	public void logAfterThrowingExecution(JoinPoint joinPoint, Exception exception) {
		if (env.acceptsProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)) {
			logger.error("Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'",
					joinPoint.getSignature().getDeclaringTypeName(),
					joinPoint.getSignature().getName(),
					exception.getCause() != null ? exception.getCause() : "NULL",
					exception.getMessage());
		} else {
			logger.error("After Throwing Aspect - {} is called with throwing: {}", 
				joinPoint, exception);
		}
		
	}
	
	@AfterReturning(pointcut="com.example.courseselection.aop.PointcutConfig.repositoryPackageConfig()", 
			returning="resultValue")
	public void logAfterReturningExecution(JoinPoint joinPoint, Object resultValue) {
		logger.info("After Returning Aspect - {} is called with returning: {}",
				joinPoint, resultValue);
	}
	
	

}
