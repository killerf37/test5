package com.ginfon.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author: James
 * @Date: 2018/5/14 10:07
 * @Description:
 */
@Aspect
@Component
public class LogAspect {

	@Pointcut("execution(public * com.goldpeak.mis.web.*.*(..))")
	public void webLog() {
	}

	@Before("webLog()")
	public void before(JoinPoint joinPoint) throws Throwable {

	}

	@After("webLog()")
	public void after(JoinPoint joinPoint) throws Throwable {

	}
}
