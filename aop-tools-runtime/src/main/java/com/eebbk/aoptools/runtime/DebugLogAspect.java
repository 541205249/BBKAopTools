package com.eebbk.aoptools.runtime;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * DebugLogAspect
 */

@Aspect
public class DebugLogAspect {
	private Context context;
	private Intent intent;

	private static final String POINTCUT_METHOD =
			"execution(@com.eebbk.aoptools.annotations.DebugLog * *(..))";

	private static final String POINTCUT_CONSTRUCTOR =
			"execution(@com.eebbk.aoptools.annotations.DebugLog *.new(..))";

	@Pointcut(POINTCUT_METHOD)
	public void methodAnnotatedWithDebugLog() {}

	@Pointcut(POINTCUT_CONSTRUCTOR)
	public void constructorAnnotatedDebugLog() {}

	@Around("methodAnnotatedWithDebugLog() || constructorAnnotatedDebugLog()")
	public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		if(context == null){
			Class clz = Class.forName("android.app.ActivityThread");
			Method method = clz.getMethod("currentApplication");
			context = (Context) method.invoke(clz);
		}
		enterMethod(joinPoint);
		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long end = System.currentTimeMillis();
		endMethod(joinPoint,end-start);
		if(intent == null){
			intent = new Intent("com.eebbk.test.aoptest");
		}
		intent.putExtra("test","method spend time"+ (end-start));
		context.sendBroadcast(intent);
		return result;
	}

	private static void enterMethod(JoinPoint joinPoint){
		CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
		Class<?> cls = codeSignature.getDeclaringType();
		String methodName = codeSignature.getName();
		String[] parameterNames = codeSignature.getParameterNames();
		Object[] parameterValues = joinPoint.getArgs();
		StringBuilder builder = new StringBuilder("-->  ");
		builder.append(methodName).append("(");
		for(int i = 0;i < parameterValues.length;i++){
			if(i > 0){
				builder.append(", ");
			}
			builder.append(parameterNames[i]).append("=").append(parameterValues[i]);
		}
		builder.append(")");
		Log.e(cls.getSimpleName(),builder.toString());
	}

	private static void endMethod(JoinPoint joinPoint,long spentTime){
		Signature signature = joinPoint.getSignature();

		Class<?> cls = signature.getDeclaringType();
		String methodName = signature.getName();
		StringBuilder builder = new StringBuilder("<--  ")
				.append(methodName)
				.append(" [")
				.append(spentTime)
				.append("ms]");
		Log.e(cls.getSimpleName(),builder.toString());
	}

}
