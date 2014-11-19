package edu.texas.threadharmony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/*
 * Deadlock detection
 * Tracing
 * Renaming
 * Cleanup
 * Criteria
 * Focusing on single variables
 * Test examples
 * 	Deadlock
 * 	Read/Write
 */

public class THTestManager {
	
	private static Map<Long, Semaphore> semaphoreMap = new HashMap<Long, Semaphore>();
	
	public static void executeTest(String testClassName) 
		throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			IllegalArgumentException, 
			InvocationTargetException, 
			NoSuchMethodException, 
			SecurityException {		
		THClassLoader classLoader = new THClassLoader();
		Class<?> testClass = classLoader.loadClass(testClassName);
		Object test = testClass.getConstructor().newInstance();
		List<String> testMethodNames = classLoader.getTestMethodNames();
		
		List<Thread> threads = new ArrayList<Thread>(testMethodNames.size());
		
		for (String testMethodName : testMethodNames) {
			Method method = testClass.getMethod(testMethodName);
			Interleavable interleavableAnnotation = method.getAnnotation(Interleavable.class);
		
			for (int i = 0; i < interleavableAnnotation.numberOfThreads(); i++) {
				Semaphore semaphore = new Semaphore(0);
				
				THThread testThread = new THThread(testClass, test, testMethodName);
				threads.add(testThread);
				
				long threadId = testThread.getId();
				semaphoreMap.put(threadId, semaphore);
				testThread.start();
			}
		}
		
		for (Thread thread : threads){
			while (thread.getState() == Thread.State.NEW);
		}
		
		// @TODO: Implement other coverage criteria here, 
		// possibly as a map of lists of integers that are simply fed in to the semaphores
		
		while (!semaphoreMap.isEmpty()) {
			List<Thread> threadsToRemove = new ArrayList<Thread>();
			
			for (Thread thread : threads) {
				
				long threadId = thread.getId();
				if (thread.isAlive()) {
					semaphoreMap.get(thread.getId()).release(10);
					while (semaphoreMap.get(threadId).availablePermits() != 0 && thread.isAlive())
					{
						Thread.yield();
					}
					
				} else {
					semaphoreMap.remove(threadId);
					threadsToRemove.add(thread);
				}
			}
			
			threads.removeAll(threadsToRemove);
		}
		
		System.out.println(THTest.executionOrder);
	}
	
	public static class THTest {
		// For debugging purposes
		public static String executionOrder = "";	
		public static final String contextSwitchMethodName = "interleave";
		
		// If this method name ever changes, we need to update 
		// the contextSwitchMethodName variable above.
		protected static void interleave() {
			Thread currentThread =  Thread.currentThread();
		
			// To help mitigate potential deadlocks, only acquire the
			// semaphore if this is a THThread.
			if (currentThread instanceof THThread) {
				Long threadId = currentThread.getId();
				Semaphore semaphore = semaphoreMap.get(threadId);
				semaphore.acquireUninterruptibly();
			
				executionOrder += currentThread.getName() + "\n";
			}
		}
	}

}