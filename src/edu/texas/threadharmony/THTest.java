package edu.texas.threadharmony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class THTest {

	public static final String CONTEXTSWITCHMETHODNAME = "interleave";

	// For debugging purposes
	public String executionOrder = "";

	private Map<Long, Semaphore> semaphoreMap;
	private Map<Long, THThread> threadMap;

	public THTest() {
		semaphoreMap = new HashMap<Long, Semaphore>();
		threadMap = new HashMap<Long, THThread>();
	}

	public void initialize() throws NoSuchMethodException, SecurityException {
		for (Method method : this.getClass().getMethods()) {
			Interleavable interleavableAnnotation = method.getAnnotation(Interleavable.class);

			if (interleavableAnnotation != null) {
				THThread testThread = new THThread(this, method.getName());

				long threadId = testThread.getId();
				semaphoreMap.put(threadId, new Semaphore(0));
				threadMap.put(threadId, testThread);
			}
		}
	}

	public void globalSetUp() {

	}

	public void setUp() {

	}

	public void start() {
		executionOrder = "";

		for (THThread thread : threadMap.values()) {
			thread.start();
		}
	}

	public void waitForFinish() {
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

	public List<THThread> getThreads() {
		return new ArrayList<THThread>(threadMap.values());
	}

	public String getTrace() {
		return executionOrder;
	}

	// If this method name ever changes, we need to update
	// the contextSwitchMethodName variable above.
	protected static void interleave() {
		THThread thread = THThread.currentThread();

		if (thread != null) {
			Long threadId = thread.getId();

			THTest test = thread.getTest();
			Semaphore semaphore = test.semaphoreMap.get(threadId);
			semaphore.acquireUninterruptibly();

			test.executionOrder += thread.getName() + "\n";
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
		
		return !semaphore.hasQueuedThreads() && 
				(thread.getState() == Thread.State.WAITING ||
				thread.getState() == Thread.State.BLOCKED ||
				thread.getState() == Thread.State.TIMED_WAITING);
	}
	
	public boolean isThreadRunning(long threadId) {
		return threadMap.get(threadId).isAlive();
	}
	
	public static void main(String[] args) {
		
	}
}
