package org.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProfilingAspect {

	@Around("execution(* org.example.*Service.*(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		try {
			return joinPoint.proceed();
		} finally {
			long duration = System.currentTimeMillis() - startTime;
			System.out.println("--- Execution of method '" + joinPoint.getSignature() + "' took " + duration + "ms");
		}
	}
}
