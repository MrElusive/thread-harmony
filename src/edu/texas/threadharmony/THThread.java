package edu.texas.threadharmony;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.ClassFormatException;

public class THThread implements Runnable {
	
	private THTest test;
	private String testMethodName;

	public static Map<Long, THThread> threadMap = new HashMap<Long, THThread>();
	private static long idCounter;
	
	private long id;
	private Thread thread;

	private SharedVariableAnalyzer sharedVariableAnalyzer;
	private boolean ignoreInterleaveInstruction;

	public THThread(THTest test, String testMethodName, Set<String> sharedVariables) throws NoSuchMethodException, SecurityException, ClassNotFoundException, ClassFormatException, IOException {
		this.test = test;
		this.testMethodName = testMethodName;
		
		this.id = idCounter++;
		this.thread = null;
		
		this.sharedVariableAnalyzer = new SharedVariableAnalyzer(test.getClass().getName(), testMethodName, sharedVariables);
		this.ignoreInterleaveInstruction = true;
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
		return this.sharedVariableAnalyzer.getNumberOfReferences(test.getTargetSharedVariableName());
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

	public String getTestMethodName() {
		return testMethodName;
	}

	public static THThread currentThread() {
		Thread currentThread =  Thread.currentThread();
		return THThread.threadMap.get(currentThread.getId());
	}

	public THTest getTest() {
		return this.test;
	}

	public boolean getIgnoreInterleaveInstruction() {
		return this.ignoreInterleaveInstruction;
	}
	
	public void setIgnoreInterleaveInstruction(boolean ignoreInterleaveInstruction) {
		this.ignoreInterleaveInstruction = ignoreInterleaveInstruction;
	}
	
	public static void configureThreadsForInterleaving(List<THThread> threads, boolean interleave) {
		for (THThread thread : threads) {			
			thread.setIgnoreInterleaveInstruction(!interleave);
		}
	}
}
