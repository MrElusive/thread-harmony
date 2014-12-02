package edu.texas.threadharmony.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.util.IClassFileReader;

import edu.texas.threadharmony.plugin.SharedVariableParser;



public class THBuilder extends IncrementalProjectBuilder {
	
	private List<IPath> classFilesToProcess;
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		
		this.classFilesToProcess = new ArrayList<IPath>();
		
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			gatherClassFilesForFullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			
			if (delta == null) {
				gatherClassFilesForFullBuild(monitor);
			} else {
				gatherClassFilesForIncrementalBuild(delta, monitor);
			}
		}
		
		processClassFiles(monitor);
		
		return null;
	}
	
	private void processClassFiles(IProgressMonitor monitor) {
		try {
			for (IPath path : this.classFilesToProcess) {
				String classFilePath = path.toOSString(); 
                ClassParser parser = new ClassParser(classFilePath);
                JavaClass clazz = parser.parse();
                
                SharedVariableParser sharedVariableParser = new SharedVariableParser(clazz.getClassName(), getProject().getName());
				THClassAugmenter classAugmenter = new THClassAugmenter(clazz, sharedVariableParser.parse());
				
                classAugmenter.write(classFilePath);
			}
			
		} catch (ClassFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void gatherClassFilesForIncrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		System.out.println("Incremental Build on " + delta);
		
		try {			
			
			final IPath outputLocation = getProject().getLocation().append(JavaCore.create(getProject()).getOutputLocation().removeFirstSegments(1));
			
			delta.accept(new IResourceDeltaVisitor() {
				
				public boolean visit(IResourceDelta delta) {					
					IResource resource = delta.getResource();
					IJavaElement javaElement = JavaCore.create(resource);
					
					if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
						ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
						
						try {
							for (IType type : compilationUnit.getAllTypes()) {
								String classFileName = type.getFullyQualifiedName('$').replace('.', '/') + ".class";
								classFilesToProcess.add(outputLocation.append(classFileName));
							}
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					return true; // visit children too
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private void gatherClassFilesForFullBuild(IProgressMonitor monitor) {
		try {
			getProject().accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					IJavaElement javaElement = JavaCore.create(resource);
					
					if (javaElement != null && javaElement.getElementType() == IJavaElement.CLASS_FILE) {
						classFilesToProcess.add(resource.getLocation());
					}
					
					return true;
				}
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
