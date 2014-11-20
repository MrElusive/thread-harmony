package tests;

import edu.texas.threadharmony.Interleavable;
import edu.texas.threadharmony.THTestManager;

public class MyTest extends THTestManager.THTest {
	
	static Object mutex = new Object();
	static double d = 0;
	
	private void pause(int testArg) {
				
		synchronized (mutex) {
			
			try {
				mutex.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}
	
	@Interleavable()
	public void testMethod() {
		double a = 2.0;
		double b = 5.0;
		double c = Math.sqrt(a * a + b * b) + d;
		{
			pause(5);
		}
		for (int i = 0; i < 10; i ++) {
			b = c;
			
		}
		System.out.println("The hypotenuse is " + c);
	}
	
	@Interleavable()
	public void testMethod2() {
		
		double a = 7.0;
		double b = 9.0;
		double c = Math.sqrt(a * a + b * b) + d;
		if (a == 5.0) {
			
			b = c;
			
			if (c == b) {
				c = b;
			}
		} else if (a == b) {
			c = a;
		} else {
			a = c;
		}
		System.out.println("The hypotenuse is " + c);
	}
}
