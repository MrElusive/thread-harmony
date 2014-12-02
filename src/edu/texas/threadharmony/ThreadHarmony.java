package edu.texas.threadharmony;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.bcel.classfile.ClassFormatException;

import edu.texas.threadharmony.THTestManager.TestCriteria;

public class ThreadHarmony {

	public static void main(String[] args) {
		String testName = "";
		TestCriteria testCriteria;
		
		if (args.length > 0) {
			testName = args[0];
		} else {
			System.out.println("USAGE: java ThreadHarmony TEST_NAME [TEST_CRITERIA]");
			System.exit(1);
		}
		
        if (args.length > 1) {
        	testCriteria = THTestManager.TestCriteria.valueOf(args[1]);
        } else {
        	testCriteria = TestCriteria.ALL;
        }
        
		try {
			THTestManager.executeTest(testName, testCriteria);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}
