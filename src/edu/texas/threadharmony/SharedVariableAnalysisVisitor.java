package edu.texas.threadharmony;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;


public class SharedVariableAnalysisVisitor extends EmptyVisitor {

	private Map<String, Integer> sharedVariableReferenceMap;
	private ConstantPoolGen constantPoolGen;
	private int totalReferences;
	
	public SharedVariableAnalysisVisitor(Set<String> sharedVariables, ConstantPoolGen constantPoolGen) {
		this.sharedVariableReferenceMap = new HashMap<String, Integer>();
		for (String sharedVariableName : sharedVariables) {
			this.sharedVariableReferenceMap.put(sharedVariableName, 0);
		}
		
		this.constantPoolGen = constantPoolGen;
		
		this.totalReferences = 0;
	}

	@Override
	public void visitPUTFIELD(PUTFIELD obj) {
		analyzeSharedVariableName(obj.getFieldName(constantPoolGen));
	}
	
	@Override
	public void visitPUTSTATIC(PUTSTATIC obj) {
		analyzeSharedVariableName(obj.getFieldName(constantPoolGen));
	}
	
	@Override
	public void visitGETFIELD(GETFIELD obj) {
		analyzeSharedVariableName(obj.getFieldName(constantPoolGen));
	}
	
	@Override
	public void visitGETSTATIC(org.apache.bcel.generic.GETSTATIC obj) {
		analyzeSharedVariableName(obj.getFieldName(constantPoolGen));
	};
	
	private void analyzeSharedVariableName(String sharedVariableName) {
		if (sharedVariableReferenceMap.containsKey(sharedVariableName)) {
			sharedVariableReferenceMap.put(sharedVariableName, sharedVariableReferenceMap.get(sharedVariableName) + 1);
			totalReferences++;
		}
		
	}

	public Map<String, Integer> getReferenceMap() {
		return this.sharedVariableReferenceMap;
	}

	public int getTotalReferences() {
		return this.totalReferences;
	}
}
