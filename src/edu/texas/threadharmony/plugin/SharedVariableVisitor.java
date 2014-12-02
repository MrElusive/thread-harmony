package edu.texas.threadharmony.plugin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.texas.threadharmony.Interleavable;

public class SharedVariableVisitor extends ASTVisitor {
	private Set<String> sharedVariables;
	
    public SharedVariableVisitor() {
        sharedVariables = new HashSet<String>();        
    }

	public Set<String> getSharedVariables() {
		return sharedVariables;
	}
	
    public boolean visit(VariableDeclarationFragment node) {
    	IVariableBinding variableBinding = node.resolveBinding();
    	if (variableBinding.isField()) {
    		IAnnotationBinding[] annotationBindings = variableBinding.getAnnotations();
    		
    		boolean foundInterleavableAnnotation = false;
    		for (IAnnotationBinding annotationBinding : annotationBindings) {
    			if (annotationBinding.getAnnotationType().getQualifiedName().equals(Interleavable.class.getName())) {
    				foundInterleavableAnnotation = true;
    			}
    		}
    		
    		if (foundInterleavableAnnotation) {
    			sharedVariables.add(node.getName().getIdentifier());
    		}
    	}
        
        return false;
    }
}
