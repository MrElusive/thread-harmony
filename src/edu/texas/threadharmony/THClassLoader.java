package edu.texas.threadharmony;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassLoader;

import edu.texas.threadharmony.builder.SharedVariableOPCodeVisitor;

public class THClassLoader extends ClassLoader {
	
	private List<String> testMethodNames;
	private ConstantPoolGen constantPoolGen;
	
	
	public THClassLoader() {
		super(new String[] { "java.", "sun.", "javax.", "edu.texas.threadharmony" });
		testMethodNames = new ArrayList<String>();
	}

	@Override
	protected JavaClass modifyClass(JavaClass clazz) {
				
		this.constantPoolGen = new ConstantPoolGen(clazz.getConstantPool());
					
		String expectedTypeName = Interleavable.class.getName();
		Method[] methods = clazz.getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			for (AnnotationEntry annotationEntry : methods[i].getAnnotationEntries()) {							
				String actualTypeName = annotationEntry.getAnnotationType();
				// The format of the type name given by the annotation is slightly different from the
				// format given by Interleavable.class.getName().
				actualTypeName = actualTypeName.substring(1, actualTypeName.length() - 1).replace("/", ".");
				
				if (expectedTypeName.equals(actualTypeName)) {
					getTestMethodNames().add(methods[i].getName());
					methods[i] = createInterleavableMethod(methods[i], clazz.getClassName(), constantPoolGen);
					break;
				}			
			}
		}
		
		clazz.setConstantPool(constantPoolGen.getFinalConstantPool());

		return clazz;
	}
	
	private Method createInterleavableMethod(Method method, String className, ConstantPoolGen constantPoolGen) {
		MethodGen methodGen = new MethodGen(method, className, constantPoolGen);
		InstructionFactory instructionFactory = new InstructionFactory(constantPoolGen);
				
		InvokeInstruction contextSwitchInstruction = 
			instructionFactory.createInvoke(
				THTest.class.getName(), 
				THTest.CONTEXTSWITCHMETHODNAME, 
				Type.VOID, 
				Type.NO_ARGS, 
				Constants.INVOKESTATIC
			);
			
		SharedVariableOPCodeVisitor fieldOrStaticOPCodeVisitor = new SharedVariableOPCodeVisitor("myVariable", constantPoolGen);
		
		InstructionList instructionList = methodGen.getInstructionList();
		InstructionHandle instructionHandle = instructionList.getStart();
		while (instructionHandle != instructionList.getEnd()) {
			instructionHandle.accept(fieldOrStaticOPCodeVisitor);
			
			if (fieldOrStaticOPCodeVisitor.operatesOnSharedVariable()) {
				instructionList.insert(instructionHandle, contextSwitchInstruction);
			}
			
			fieldOrStaticOPCodeVisitor.clear();
			
			instructionHandle = instructionHandle.getNext();
		}
		
		return methodGen.getMethod();		
	}

	public List<String> getTestMethodNames() {
		return testMethodNames;
	}
}
