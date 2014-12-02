package edu.texas.threadharmony;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.bcel.classfile.ClassFormatException;

public class THTest {

	public static final String CONTEXTSWITCHMETHODNAME = "interleave";

	// For debugging purposes
	private Object executionOrderMutex = new Object();
	private String executionOrder = "";

	private Map<Long, Semaphore> semaphoreMap;
	private Map<Long, THThread> threadMap;
	
	private String targetSharedVariableName;
	

	public THTest() {
		semaphoreMap = new HashMap<Long, Semaphore>();
		threadMap = new HashMap<Long, THThread>();
	}

	public void initialize(Set<String> sharedVariables) throws NoSuchMethodException, SecurityException, ClassNotFoundException, ClassFormatException, IOException {
		
		for (Method method : this.getClass().getMethods()) {
			Interleavable interleavableAnnotation = method.getAnnotation(Interleavable.class);

			if (interleavableAnnotation != null) {
				for (int i = 0; i < interleavableAnnotation.numberOfThreads(); i++) {
					THThread testThread = new THThread(this, method.getName(), sharedVariables);

					long threadId = testThread.getId();
					semaphoreMap.put(threadId, new Semaphore(0));
					threadMap.put(threadId, testThread);
				}
			}
		}
	}

	public void globalSetUp() {

	}

	public void setUp() {

	}

	public void startHarmoniously() {
		executionOrder = "";

		for (THThread thread : threadMap.values()) {
			thread.start();
			Semaphore semaphore = semaphoreMap.get(thread.getId());
			while (!isThreadBlocked(thread.getId()) && !semaphore.hasQueuedThreads() && thread.isAlive()) {
				Thread.yield();
			}
		}
	}

	public void finishHarmoniously() {
		for (THThread thread : threadMap.values()) {
			while (thread.isAlive()) {
				Thread.yield();
			}
		}
	}

	public void tearDown() {

	}

	public void globalTearDown() {

	}
	
	public void checkState() throws Exception {
		
	}

	public List<THThread> getThreads() {
		return new ArrayList<THThread>(threadMap.values());
	}

	public String getTrace() {
		return executionOrder;
	}

	// If this method name ever changes, we need to update
	// the contextSwitchMethodName variable above.
	protected static void interleave(String sharedVariableName, String operation) {
		THThread thread = THThread.currentThread();

		if (thread != null && !thread.getIgnoreInterleaveInstruction()) {
			THTest test = thread.getTest();
			String targetSharedVariableName = test.getTargetSharedVariableName();
			
			if (targetSharedVariableName == null || sharedVariableName.equals(targetSharedVariableName)) {
				Long threadId = thread.getId();
				
				Semaphore semaphore = test.semaphoreMap.get(threadId);
				semaphore.acquireUninterruptibly();
	
				synchronized(test.executionOrderMutex) {
					test.executionOrder += String.format("\t%s - %s - %s - %s\n", thread.getName(), thread.getTestMethodName(), operation, sharedVariableName);
				}
			}
		}
	}

	public void feedThread(long threadId, int numberOfInstructions) {
		semaphoreMap.get(threadId).release(numberOfInstructions);
	}

	public boolean isThreadEating(long threadId) {
		Semaphore semaphore = semaphoreMap.get(threadId);
		THThread thread = threadMap.get(threadId);
		
		return semaphore.availablePermits() != 0 && thread.isAlive();
	}
	
	public boolean isThreadBlocked(long threadId) {
		Semaphore semaphore = semaphoreMap.get(threadId);
		THThread thread = threadMap.get(threadId);
		
		return !semaphore.hasQueuedThreads() &&  thread.isAlive() &&
				(thread.getState() == Thread.State.WAITING ||
				thread.getState() == Thread.State.BLOCKED ||
				thread.getState() == Thread.State.TIMED_WAITING);
	}
	
	public boolean isThreadRunning(long threadId) {
		return threadMap.get(threadId).isAlive();
	}
	
	public String getTargetSharedVariableName() {
		return targetSharedVariableName;
	}

	public void setTargetSharedVariableName(String targetSharedVariableName) {
		this.targetSharedVariableName = targetSharedVariableName;
	}

	public static void main(String[] args) {
		
	}
}
