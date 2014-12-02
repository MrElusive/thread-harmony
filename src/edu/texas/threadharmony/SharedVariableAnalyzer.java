package edu.texas.threadharmony;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;

public class SharedVariableAnalyzer {
	
	private Map<String, Integer> referenceMap;
	private int totalRefererences;
	
	public SharedVariableAnalyzer(String className, String methodName, Set<String> sharedVariables) throws ClassNotFoundException, ClassFormatException, IOException, NoSuchMethodException, SecurityException {
		Class<?> clazz = Class.forName(className);
		JavaClass javaClass = ClassFileLoader.load(clazz);
		ConstantPoolGen constantPoolGen = new ConstantPoolGen(javaClass.getConstantPool());
		
		SharedVariableAnalysisVisitor sharedVariableAnalysisVisitor = new SharedVariableAnalysisVisitor(sharedVariables, constantPoolGen);
	
		Method method = javaClass.getMethod(clazz.getMethod(methodName));
		MethodGen methodGen = new MethodGen(method, className, constantPoolGen);
		for (InstructionHandle instructionHandle : methodGen.getInstructionList().getInstructionHandles()) {
			instructionHandle.accept(sharedVariableAnalysisVisitor);
		}
		
		this.referenceMap = sharedVariableAnalysisVisitor.getReferenceMap();
		this.totalRefererences = sharedVariableAnalysisVisitor.getTotalReferences();
	}
	
	public int getNumberOfReferences(String targetSharedVariableName) {
		if (targetSharedVariableName == null) {
			return this.totalRefererences;
		} else {
			return this.referenceMap.get(targetSharedVariableName);
		}
	}

}
