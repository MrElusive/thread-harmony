package edu.texas.threadharmony.plugin;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class SharedVariableParser {
	
	private String typeName;
	private String projectName;
	
	public SharedVariableParser(String typeName, String projectName) {
		this.typeName = typeName;
		this.projectName = projectName;
	}

	public Set<String> parse() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		try {
			parser.setSource(JavaCore.create(project).findType(typeName).getCompilationUnit());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);		
		
		SharedVariableVisitor sharedVariableVisitor = new SharedVariableVisitor();
		compilationUnit.accept(sharedVariableVisitor);
		
		return sharedVariableVisitor.getSharedVariables();
	}
}
