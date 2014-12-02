package edu.texas.threadharmony;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.bcel.classfile.ClassFormatException;
import org.omg.CORBA.INITIALIZE;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

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

	private static int threadPairCount;
	private static int interleavingCount;
	private static int sharedVariableCount;
	private static int numberOfPassedTests;
	
	public static void executeTest(String testClassName, TestCriteria criteria) 
		throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			IllegalArgumentException, 
			InvocationTargetException, 
			NoSuchMethodException, 
			SecurityException,
			ClassFormatException,
			IOException {
				
        Class<?> testClass = Class.forName(testClassName);
		THTest test = (THTest) testClass.newInstance();
		Set<String> sharedVariables = getSharedVariables(testClass);
		test.initialize(sharedVariables);
		
		test.globalSetUp();
		
		interleavingCount = 0;
		threadPairCount = 0;
		sharedVariableCount = 0;
		numberOfPassedTests = 0;
		
        System.out.println("Executing test '" + testClassName + "' with " + criteria.toString() + " coverage\n\n");
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
		
		System.out.println("\nTotal number of interleavings: " + interleavingCount);
		System.out.println("Total number of thread pairs: " + threadPairCount);
		System.out.println("Total number of shared variables: " + sharedVariableCount);
		
		System.out.println();
		
		if (numberOfPassedTests == interleavingCount) {
			System.out.println("Final Result: All tests passed!");
		} else {
			System.out.println("Final Result: One or more tests failed!");
		}
		
		double coverage = 100 * (double) numberOfPassedTests / interleavingCount;
		System.out.println(String.format("Coverage: %d/%d (%5.2f%%)", numberOfPassedTests, interleavingCount, coverage));
		
		test.globalTearDown();
	}

	private static void executeWithALLCoverage(THTest test) {
		List<THThread> threads = test.getThreads();
		THThread.configureThreadsForInterleaving(threads, true);
		
		for (Interleaving interleaving : new InterleavingGenerator(threads)) {
			interleavingCount++;
			manageInterleaving(test, interleaving);
		}
	}
	
	private static void executeWithTPAIRCoverage(THTest test) {
		List<THThread> threads = test.getThreads();
		Generator<THThread> threadPairGenerator = Factory.createSimpleCombinationGenerator(Factory.createVector(threads), 2);
		
		for (ICombinatoricsVector<THThread> threadPairVector : threadPairGenerator) {
			threadPairCount++;
			List<THThread> threadPair = threadPairVector.getVector();
			THThread.configureThreadsForInterleaving(threads, false); // Turn off interleaving for all threads
			THThread.configureThreadsForInterleaving(threadPair, true); // Then turn on interleaving for only the threads we care about
			
			for (Interleaving interleaving : new InterleavingGenerator(threadPair)) {
                interleavingCount++;
				manageInterleaving(test, interleaving);
			}
		}
		
	}
	
	private static void executeWithSVARCoverage(THTest test) {
		// @TODO: For now we are getting the sharedVariableNames from the class itself. In the future, 
		// we should get it from a custom attribute in the class itself, since shared variables may not
		// necessarily be a field. If we decide that shared variables will always be field, then we can
		// ignore this and simply our code
		
		for (String sharedVariableName : getSharedVariables(test.getClass())) {
			sharedVariableCount++;
			test.setTargetSharedVariableName(sharedVariableName);
			executeWithTPAIRCoverage(test);
		}
	}
	
	private static void manageInterleaving(THTest test, Interleaving interleaving) {
		test.setUp();
		test.startHarmoniously();
	
		int contextSwitchCount = 0;
		for (ContextSwitch contextSwitch : interleaving) {
			contextSwitchCount++;
			long threadId = contextSwitch.getThreadId();
				
			if (test.isThreadRunning(threadId)) {
				test.feedThread(threadId, contextSwitch.getNumberOfInstructions());
				
				while (test.isThreadEating(threadId)) {
					// The thread is blocked on something else
					if (test.isThreadBlocked(threadId))	{
						break;
						
					} else {
						Thread.yield();
					}
				}
			}
		}
			
		test.finishHarmoniously();
		test.tearDown();
	
		try {
			test.checkState();
			numberOfPassedTests++;
			System.out.println("Test Result: PASSED!");
		} catch (Exception e) {
			System.out.println("Test Result: FAILED!");
			System.out.println("Test Output: " + e.getMessage());
		}
		System.out.println("Number of context switches: " + contextSwitchCount);
		System.out.println("Execution Order: ");
		System.out.println(test.getTrace());
	}
	
	private static Set<String> getSharedVariables(Class<?> clazz) {
		Set<String> sharedVariables = new HashSet<String>();
		
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(Interleavable.class) != null) {
				sharedVariables.add(field.getName());
			}
		}
		
		return sharedVariables;
	}
}