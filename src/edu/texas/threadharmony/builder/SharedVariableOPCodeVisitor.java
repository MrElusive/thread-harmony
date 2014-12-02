package edu.texas.threadharmony.builder;
import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;


public class SharedVariableOPCodeVisitor extends EmptyVisitor {

	private Set<String> sharedVariablesNames;
	private ConstantPoolGen constantPoolGen;
	
	private boolean operatesOnSharedVariable = false;
	private String sharedVariableName = "";
	
	public SharedVariableOPCodeVisitor(String sharedVariableName, ConstantPoolGen constantPoolGen) {
		this.sharedVariablesNames = new HashSet<String>();
		this.sharedVariablesNames.add(sharedVariableName);
		this.constantPoolGen = constantPoolGen;
	}
	
	public SharedVariableOPCodeVisitor(Set<String> sharedVariables,	ConstantPoolGen constantPoolGen) {
		this.sharedVariablesNames = sharedVariables;
		this.constantPoolGen = constantPoolGen;
	}

	@Override
	public void visitPUTFIELD(PUTFIELD obj) {
		checkIfReferencedNameIsSharedVariable(obj.getFieldName(constantPoolGen));
	}
	
	@Override
	public void visitPUTSTATIC(PUTSTATIC obj) {
		checkIfReferencedNameIsSharedVariable(obj.getFieldName(constantPoolGen));
	}
	
	@Override
	public void visitGETFIELD(GETFIELD obj) {
		checkIfReferencedNameIsSharedVariable(obj.getFieldName(constantPoolGen));
	}
	
	@Override
	public void visitGETSTATIC(org.apache.bcel.generic.GETSTATIC obj) {
		checkIfReferencedNameIsSharedVariable(obj.getFieldName(constantPoolGen));
	};
	
	public boolean operatesOnSharedVariable() {
		return operatesOnSharedVariable;
	}
	
	public void clear() {
		operatesOnSharedVariable = false;
	}
	
	private void checkIfReferencedNameIsSharedVariable(String referencedName) {
		operatesOnSharedVariable = this.sharedVariablesNames.contains(referencedName);
		if (operatesOnSharedVariable) {
			this.sharedVariableName = referencedName;
		}
	}

	public String getSharedVariableName() {
		return sharedVariableName;
	}
}
