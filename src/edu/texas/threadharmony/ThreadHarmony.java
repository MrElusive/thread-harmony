package edu.texas.threadharmony;

import java.lang.reflect.InvocationTargetException;

import org.apache.bcel.classfile.ClassFormatException;

public class ThreadHarmony {

	public static void main(String[] args) {
		try {
			//THTestManager.executeTest("tests.MyTest");
			THTestManager.executeTest(args[0]);
			/*
			String classFilePath = "/home/clemons/workspace/ThreadHarmony/bin/edu/texas/threadharmony/TestClass.class";		
//			ClassParser classParser = new ClassParser(classFilePath);
//			JavaClass myClass = classParser.parse();
//			Method[] methods = myClass.getMethods();
			
			ClassParser parser = new ClassParser(classFilePath);
			//System.out.println(parser.parse().toString());
			

			
			final Semaphore semaphore = new Semaphore(0);
			
			Thread thread = new Thread() {
				@Override
				public void run() {
					ClassLoader classLoader = new THClassLoader();

					try {
						Class<?> testClass;
						testClass = classLoader.loadClass("edu.texas.threadharmony.TestClass");
						Object test = testClass.getConstructor(Semaphore.class).newInstance(semaphore);
						testClass.getMethod("testMethod").invoke(test);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			thread.start();

			System.in.read();
			semaphore.release(5);
			System.in.read();
//			while(!semaphore.hasQueuedThreads());
			semaphore.release(7);
			System.in.read();
//			while(!semaphore.hasQueuedThreads());
			semaphore.release(9);
			System.in.read();
			semaphore.release(2);
			
//			for (Method method : methods) {
//				System.out.println(method.getName());
//				Code code = method.getCode();
//				System.out.println(code.toString());
//				
//			}
 * */
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static void run() {
		
		
	}

}
