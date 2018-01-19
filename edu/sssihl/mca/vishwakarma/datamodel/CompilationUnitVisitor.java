package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

public class CompilationUnitVisitor extends ASTVisitor {
	
	List<TypeDeclaration> types= new ArrayList<TypeDeclaration>();
	List<EnumDeclaration> enums= new ArrayList<EnumDeclaration>();
	
	@Override
	public boolean visit(TypeDeclaration node) {
		types.add(node);
		return false;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		enums.add(node);
		return false;
	}
	
	public List<TypeDeclaration> getTypes() {
		return types;
	}
	
	public List<EnumDeclaration> getEnums() {
		return enums;
	}

}
