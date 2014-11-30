package edu.texas.threadharmony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.omg.CORBA.INITIALIZE;

/*
 * -Deadlock detection (sort of works)
 * -Tracing (sort of works)
 * -Need to figure out how to deal with certain constructs when instrumenting the code: i.e. try/catch blocks
 * 	- Workaround: Use the JVM option "-XX:-UseSplitVerifier"
 * 	- We can still try to fix this later by manually updating the StackMapTable in each code method (complicated!)
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
	
	public enum TestCriteria {
		ALL,
		TPAIR,
		SVAR
	}
	
	public static void executeTest(String testClassName, TestCriteria criteria) 
		throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			IllegalArgumentException, 
			InvocationTargetException, 
			NoSuchMethodException, 
			SecurityException {
				
        Class<?> testClass = Class.forName(testClassName);
		THTest test = (THTest) testClass.newInstance();
		test.initialize();
		
		test.globalSetUp();
		
		switch (criteria) {
			case ALL:
				executeWithALLCoverage(test);
				break;
			case TPAIR:
				executeWithTPAIRCoverage(test);
				break;
			case SVAR:
				executeWithSVARCoverage(test);
				break;
			default:
				break;
		}
		
		test.globalTearDown();
	}

	private static void executeWithALLCoverage(THTest test) {
		for (Interleaving interleaving : new InterleavingGenerator(test.getThreads())) {
			test.setUp();
			test.start();
				
			for (ContextSwitch contextSwitch : interleaving) {
				long threadId = contextSwitch.getThreadId();
					
				if (test.isThreadRunning(threadId)) {
					test.feedThread(threadId, contextSwitch.getNumberOfInstructions());
					
					do {
						// The thread is blocked on something else
						if (test.isThreadBlocked(threadId))	{
							break;
							
						} else {
							Thread.yield();
						}
						
					} while (test.isThreadEating(threadId));								
				}
			}
				
			test.waitForFinish();
			test.tearDown();
				
			System.out.println("Execution Order: ");
			System.out.println(test.getTrace());
		}
	}
	
	private static void executeWithTPAIRCoverage(THTest test) {
		
	}
	
	private static void executeWithSVARCoverage(THTest test) {
		
	}
}