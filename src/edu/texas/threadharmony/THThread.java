package edu.texas.threadharmony;

import java.lang.Thread.State;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class THThread implements Runnable {
	
	private THTest test;
	private String testMethodName;

	public static Map<Long, THThread> threadMap = new HashMap<Long, THThread>();
	private static long idCounter;
	
	private long id;
	private Thread thread;
	
	private int numberOfInterleavableInstructions;

	public THThread(THTest test, String testMethodName) throws NoSuchMethodException, SecurityException {
		this.test = test;
		this.testMethodName = testMethodName;
		this.numberOfInterleavableInstructions = test.getClass().getMethod(testMethodName).getAnnotation(Interleavable.class).numberOfInterleaves();
		this.id = idCounter++;
	}
	
	public THThread(int numberOfInterleavableInstructions) {
		this.numberOfInterleavableInstructions = numberOfInterleavableInstructions;
	}

	@Override
	public void run() {
		try {
			test.getClass().getMethod(this.testMethodName).invoke(this.test);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (
			IllegalAccessException | 
			IllegalArgumentException | 
			NoSuchMethodException |
			SecurityException e
		) {
			e.printStackTrace();
		}
	}

	public int getNumberOfInterleaves() {
		return numberOfInterleavableInstructions;
	}

	public void start() {
		if (this.thread != null) {
			THThread.threadMap.remove(this.thread.getId());
		}
		this.thread = new Thread(this);
		THThread.threadMap.put(thread.getId(), this);
		this.thread.start();
	}

	public boolean isAlive() {
		return this.thread.isAlive();
	}

	public State getState() {
		return this.thread.getState();
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return "Thread " + this.id;
	}

	public static THThread currentThread() {
		Thread currentThread =  Thread.currentThread();
		return THThread.threadMap.get(currentThread.getId());
	}

	public THTest getTest() {
		return this.test;
	}
	
	
}
