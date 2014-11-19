package tests;

import edu.texas.threadharmony.Interleavable;
import edu.texas.threadharmony.THTestManager;

public class MyTest extends THTestManager.THTest {
	
	static double d = 0;
	
	@Interleavable(numberOfThreads=4)
	public void testMethod() {
		double a = 2.0;
		double b = 5.0;
		double c = Math.sqrt(a * a + b * b) + d;
		System.out.println("The hypotenuse is " + c);
	}
	
	@Interleavable(numberOfThreads=10)
	public void testMethod2() {
		
		double a = 7.0;
		double b = 9.0;
		double c = Math.sqrt(a * a + b * b) + d;
		System.out.println("The hypotenuse is " + c);
	}
}
