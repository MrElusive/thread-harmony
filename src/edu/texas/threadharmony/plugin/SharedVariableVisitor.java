package edu.texas.threadharmony.plugin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class SharedVariableVisitor extends ASTVisitor {
	private Set<String> sharedVariables;
	
    public SharedVariableVisitor() {
        sharedVariables = new HashSet<String>();        
    }

	public Set<String> getSharedVariables() {
		return sharedVariables;
	}
	
    public boolean visit(VariableDeclarationFragment node) {
    	if (node.resolveBinding().isField()) {
    		sharedVariables.add(node.getName().getIdentifier());
    	}
        
        return false;
    }
}
