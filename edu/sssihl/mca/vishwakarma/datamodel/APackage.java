package edu.sssihl.mca.vishwakarma.datamodel;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class APackage implements IAPackage, Comparable<APackage> {
	

	private List<IACompilationUnit> compilationUnits= new ArrayList<>();
	private List<IAPackage> packages= new ArrayList<>();
	private List<IAType> types= new ArrayList<>();
	private String name;
	private IAProject parent;

	
	public APackage(IPackageFragment packageFragment, IAProject parent) throws JavaModelException, InterruptedException {
		this.name= packageFragment.getElementName();
		this.parent= parent;
		IAPackage me= this;
		ExecutorService exec= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(ICompilationUnit i : packageFragment.getCompilationUnits()) {
			exec.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ASTParser parser= ASTParser.newParser(AST.JLS8);
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
					parser.setSource(i);
					parser.setResolveBindings(true);
					CompilationUnit cu1= (CompilationUnit) parser.createAST(null);
					i.getPath();
					IACompilationUnit cu2= new ACompilationUnit(cu1,(i).getElementName(),i.getPath().toOSString(),me);
					synchronized (compilationUnits) {
						compilationUnits.add(cu2);
					}
				}
			});
			
		}
		exec.shutdown();
		if(!exec.awaitTermination(100000, TimeUnit.DAYS))
			System.out.println("Time Out.....");
		
		for(IACompilationUnit i : compilationUnits) 
			types.addAll(i.getTypes());
	}
	
	public IAJavaElement getElement(String path) {
		IAType temp= null;
		String str;
		for(int i= types.size() - 1; i >= 0; i--) {
			if( path.startsWith(types.get(i).getElementName()) )
				temp= types.get(i);
		}
		if(temp == null)
			return null;
		if(temp.getElementName().equals(path))
			return temp;
		if(path.length() >= temp.getElementName().length()+1) {
			str= path.substring(temp.getElementName().length()+1);
			return temp.getElement(str);
		}
		return null;
	}
	
	public List<IAType> getAllTypes() {
		List<IAType> temp= new ArrayList<>();
		for(IACompilationUnit i : compilationUnits)
			temp.addAll(i.getAllTypes());
		return temp;
	}
	
	public List<IAMethod> getAllMethods() {
		List<IAMethod> ans= new ArrayList<>();
		for(IAType t: getAllTypes())
			ans.addAll(t.getMethods());
		return ans;
	}
	
	public void setParent(IAProject pro) {
		this.parent= pro;
	}
	
	/**
	 * @return All the Compilation Units under a particular package
	 */
	public List<IACompilationUnit> getCompilationUnits() {
		return compilationUnits;
	}
	
	@Override
	public IAProject getProject() {
		return parent;
	}
	
	/**
	 * @param p, A Package below whom the other packages need to be looked for.
	 * @return The Packages under a package
	 */
	public List<APackage> getSubPackages(APackage p){
		return null;
	}

	@Override
	public AJavaElementType getElementType() {
		return AJavaElementType.Package;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public IAJavaElement getParentElement() {
		return null;
	}

	@Override
	public List<IAJavaElement> getChildren() {
		List<IAJavaElement> ans= new ArrayList<>();
		ans.addAll(packages);
		ans.addAll(compilationUnits);
		return ans;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println("qwer".substring(4).length());
	}

	@Override
	public int compareTo(APackage o) {
		// TODO Auto-generated method stub
		return name.compareTo(o.getElementName());
	}

}
