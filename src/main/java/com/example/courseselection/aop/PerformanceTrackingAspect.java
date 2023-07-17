package com.example.courseselection.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class PerformanceTrackingAspect {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Around("com.example.courseselection.aop.PointcutConfig.trackTimeAnnotation()")
	public Object logAroundToFindExecutionTime(ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("Enter: {}.{}() with args = {}",
					proceedingJoinPoint.getSignature().getDeclaringTypeName(),
					proceedingJoinPoint.getSignature().getName(),
					Arrays.toString(proceedingJoinPoint.getArgs()));
		}
		long startTimeMillis = System.currentTimeMillis();
		Object result;
		try {
			result = proceedingJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("Illegal argument: {} in {}.{}()", 
					Arrays.toString(proceedingJoinPoint.getArgs()),
					proceedingJoinPoint.getSignature().getDeclaringTypeName(),
					proceedingJoinPoint.getSignature().getName());
			throw e;
		}
		long endTimeMillis = System.currentTimeMillis();
		
		long executionTimeDuration = endTimeMillis - startTimeMillis;
		if (logger.isDebugEnabled()) {
			logger.debug("Exit {}.{}() with result = {}",
					proceedingJoinPoint.getSignature().getDeclaringTypeName(),
					proceedingJoinPoint.getSignature().getName(),
					result);
		}
		logger.info("Around Aspect - {} with execution time: {}ms",
				proceedingJoinPoint, executionTimeDuration);
		
		return result;
		
	}
}
