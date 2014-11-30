package edu.texas.threadharmony.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;
import org.apache.bcel.generic.ElementValuePairGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.SimpleElementValueGen;
import org.apache.bcel.generic.Type;

import edu.texas.threadharmony.Interleavable;
import edu.texas.threadharmony.THTest;


public class THClassAugmenter {

	private JavaClass clazz;
	private List<String> testMethodNames;
	private Set<String> sharedVariables;
	
	public THClassAugmenter(JavaClass clazz, Set<String> sharedVariables) {
        this.clazz = clazz;
        this.sharedVariables = sharedVariables;
        this.testMethodNames = new ArrayList<String>();
	}

	public void write(String classFilePath) throws IOException {
		ClassGen classGen = new ClassGen(clazz);
		JavaClass augmentedClass = augmentClass(classGen);
		augmentedClass.dump(classFilePath);
	}
	
	protected JavaClass augmentClass(ClassGen classGen) {
				
		ConstantPoolGen constantPoolGen = classGen.getConstantPool();
					
		String expectedTypeName = Interleavable.class.getName();
		Method[] methods = classGen.getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			for (AnnotationEntry annotationEntry : methods[i].getAnnotationEntries()) {							
				String actualTypeName = annotationEntry.getAnnotationType();
				// The format of the type name given by the annotation is slightly different from the
				// format given by Interleavable.class.getName().
				actualTypeName = actualTypeName.substring(1, actualTypeName.length() - 1).replace("/", ".");
				
				if (expectedTypeName.equals(actualTypeName)) {
					getTestMethodNames().add(methods[i].getName());
					methods[i] = augmentMethod(methods[i], annotationEntry, classGen.getClassName(), constantPoolGen);
					
					break;
				}			
			}
		}
		
		classGen.setMethods(methods);
		
		//clazz.setConstantPool(constantPoolGen.getFinalConstantPool());

		return classGen.getJavaClass();
	}
	
	private Method augmentMethod(Method method, AnnotationEntry annotationEntry, String className, ConstantPoolGen constantPoolGen) {
		MethodGen methodGen = new MethodGen(method, className, constantPoolGen);
		AnnotationEntryGen annotationEntryGen = new AnnotationEntryGen(annotationEntry, constantPoolGen, false);
		
		InstructionFactory instructionFactory = new InstructionFactory(constantPoolGen);
				
		InvokeInstruction contextSwitchInstruction = 
			instructionFactory.createInvoke(
				THTest.class.getName(), 
				THTest.CONTEXTSWITCHMETHODNAME, 
				Type.VOID, 
				Type.NO_ARGS, 
				Constants.INVOKESTATIC
			);
		
		SharedVariableOPCodeVisitor sharedVariableOPCodeVisitor = new SharedVariableOPCodeVisitor(sharedVariables, constantPoolGen);
	
		int numberOfInterleaves = 0;
		
		InstructionList instructionList = methodGen.getInstructionList();
		InstructionHandle instructionHandle = instructionList.getStart();
		while (instructionHandle != instructionList.getEnd()) {
			instructionHandle.accept(sharedVariableOPCodeVisitor);
			
			if (sharedVariableOPCodeVisitor.operatesOnSharedVariable()) {
				instructionList.insert(instructionHandle, contextSwitchInstruction);
				numberOfInterleaves++;
			}
			
			sharedVariableOPCodeVisitor.clear();
			
			instructionHandle = instructionHandle.getNext();
		}
		
        ElementValueGen elementValueGen = new SimpleElementValueGen(ElementValueGen.PRIMITIVE_INT, constantPoolGen, numberOfInterleaves);
        ElementValuePairGen elementValuePairGen = new ElementValuePairGen("numberOfInterleaves", elementValueGen, constantPoolGen);
        annotationEntryGen.addElementNameValuePair(elementValuePairGen);
        
        // @TODO: We'll need to fix this later, but for now, we only expect there to be one annotation for the method.
        methodGen.removeAnnotationEntries();
        methodGen.addAnnotationEntry(annotationEntryGen);
		
		return methodGen.getMethod();		
	}

	public List<String> getTestMethodNames() {
		return testMethodNames;
	}

}
