package com.school.bank_java.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public class ServiceAop {
	public void beforeMonitor(JoinPoint jp){
		System.out.println("beforeMonitor : " + jp.getClass());
	}
	public void afterMonitor(JoinPoint jp){
		System.out.println("afterMonitor : " +jp.getClass());
	}
	public Object aroundMonitor(ProceedingJoinPoint  pjp) throws Throwable{
		System.out.println("-> arround 시작");
		Object retVal = pjp.proceed();
		System.out.println("-> arround 종료");
		return retVal;
	}
	public void afterReturningMonitor(JoinPoint jp, Object retVal){
		System.out.println("afterRetruningMonitor");
	}
	public void afterThrowingMonitor(JoinPoint jp, Exception exception){
		System.out.println("afterThrowingMonitor : " + exception.getMessage() );
	}

}
