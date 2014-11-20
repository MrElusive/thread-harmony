package edu.texas.threadharmony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/*
 * -Deadlock detection (sort of works)
 * -Tracing (sort of works)
 * Need to figure out how to deal with certain constructs when instrumenting the code: i.e. try/catch blocks
 * Renaming
 * Cleanup
 * Criteria
 * Focusing on single variables
 * Test examples
 * Following methods to interleave
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
		
		// @TODO: Implement other coverage criteria here, 
		// probably as a list of pairs, where the first element is the thread ID
		// and the second element is the number of instructions to run for that thread
		
		while (!semaphoreMap.isEmpty()) {
			List<Thread> threadsToRemove = new ArrayList<Thread>();
			boolean deadlockExists = true;
			
			for (Thread thread : threads) {
				
				long threadId = thread.getId();
				Semaphore semaphore = semaphoreMap.get(threadId);
				
				if (thread.isAlive()) {					
					semaphore.release(1);
					do
					{
						// The thread is blocked on something else
						if (!semaphore.hasQueuedThreads() && 
							(thread.getState() == Thread.State.WAITING || 
							thread.getState() == Thread.State.BLOCKED || 
							thread.getState() == Thread.State.TIMED_WAITING))
						{
							break;
						}
						Thread.yield();
					} while (!semaphore.hasQueuedThreads() && thread.isAlive());
					
					if (semaphore.availablePermits() == 0) {
						deadlockExists = false;
					}
				} else {
					semaphoreMap.remove(threadId);
					threadsToRemove.add(thread);
					deadlockExists = false;
				}
			}
			
			if (deadlockExists) {
				System.out.println("Deadlock exists or some threads are stuck!");
				try {
				for (Thread thread : threads) {
					thread.stop();
				}
				} catch (ThreadDeath e)
				{
					
				}
				threads.clear();
				semaphoreMap.clear();
			}
			
			threads.removeAll(threadsToRemove);
		}
		
		System.out.println("Execution Order: ");
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