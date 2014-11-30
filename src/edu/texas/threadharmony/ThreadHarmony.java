package edu.texas.threadharmony;

import java.lang.reflect.InvocationTargetException;

public class ThreadHarmony {

	public static void main(String[] args) {
		try {
			THTestManager.executeTest(args[0], THTestManager.TestCriteria.ALL);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
}
