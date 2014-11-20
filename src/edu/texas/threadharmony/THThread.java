package edu.texas.threadharmony;

import java.lang.reflect.InvocationTargetException;

public class THThread extends Thread {
	
	private Object test;
	private String testMethodName;
	private Class<?> testClass;

	public THThread(Class<?> testClass, Object test, String testMethodName) {
		this.testClass = testClass;
		this.test = test;
		this.testMethodName = testMethodName;
	}
	
	@Override
	public void run() {
		try {
			testClass.getMethod(this.testMethodName).invoke(this.test);
		} catch (InvocationTargetException e) {
			// Do nothing as of now
		} catch (
			IllegalAccessException | 
			IllegalArgumentException | 
			NoSuchMethodException |
			SecurityException e
		) {
			e.printStackTrace();
		}
	}
	
	
}
