package edu.texas.threadharmony;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class ClassFileLoader {

	public static JavaClass load(Class<?> clazz) throws ClassFormatException, IOException {
		ClassLoader classLoader = clazz.getClassLoader();
		
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
			while (classLoader != null && classLoader.getParent() != null) {
				classLoader = classLoader.getParent();
			}
		}
		
		if (classLoader != null) {
			String className = clazz.getCanonicalName().replace(".", "/") + ".class";
			URL classURL = classLoader.getResource(className);
			String classFilePath = classURL.getPath();	
			
			ClassParser classParser = new ClassParser(classFilePath);
			return classParser.parse();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(load(ClassFileLoader.class));
		} catch (ClassFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
