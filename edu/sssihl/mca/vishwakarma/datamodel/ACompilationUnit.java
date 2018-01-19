package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ACompilationUnit implements IACompilationUnit {
	private String name;
	private int loc;
	private ArrayList<IAType> types= new ArrayList<>();
	private String path;
	private IAPackage parent;
	private CompilationUnit cu;
	
	public ACompilationUnit(CompilationUnit cu, String name, String path, IAPackage Package) {
		this.cu= cu;
		this.path= path;
		this.name= name;
		this.parent= Package;
		CompilationUnitVisitor cuVisitor= new CompilationUnitVisitor();
		cu.accept(cuVisitor);
		int start= cu.getLineNumber(cu.getStartPosition()) - 1;
		int end= cu.getLineNumber(cu.getStartPosition()+cu.getLength()-1);
		this.loc= end - start;
		cu.getLength();
		for(TypeDeclaration i : cuVisitor.getTypes()) {
			types.add(new AType(i,this,Package));
		}
		
		for(EnumDeclaration i : cuVisitor.getEnums()) {
			types.add(new AType(i,this, Package));
		}
		this.cu= null;
	}
	/**
	 * 
	 * @return the top-level types declared in this compilation unit.
	 */
	public List<IAType> getTypes() {
		return types;
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
		str= path.substring(temp.getElementName().length()+1);
		return temp.getElement(str);
	}
	
	public List<IAType> getAllTypes() {
		List<IAType> temp= new ArrayList<>();
		for(IAType i : types) {
			temp.add(i);
			temp.addAll(i.getAllTypes());
		}
		return temp;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public int getLinesOfCode() {
		return loc;
	}
	
	@Override
	public CompilationUnit getCompilationUnit() {
		return this.cu;
	}
	
	@Override
	public AJavaElementType getElementType() {
		return AJavaElementType.CompilationUnit;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public IAJavaElement getParentElement() {
		return parent;
	}
	
	@Override
	public IAProject getProject() {
		return parent.getProject();
	}

	@Override
	public List<IAJavaElement> getChildren() {
		List<IAJavaElement> ans= new ArrayList<>();
		ans.addAll(types);
		return ans;
	}

	@Override
	public boolean hasChildren() {
		if(types.size() > 0)
			return true;
		return false;
	}
}
