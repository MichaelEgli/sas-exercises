package org.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	@Before("within(org.example.*)")
	public void before(JoinPoint joinPoint) {
		System.out.println("--- Invoking method '" + joinPoint.getSignature() + "'");
	}
}
