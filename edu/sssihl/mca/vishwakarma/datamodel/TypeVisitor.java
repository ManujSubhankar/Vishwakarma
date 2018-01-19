package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Extends {@link ASTVisitor} and keep 
 * track of fields and methods in the 
 * Abstract Syntax Tree
 * 
 * @author Manuj Subhankar Sahoo
 *
 */
public class TypeVisitor extends ASTVisitor {
	private boolean firstTypeDeclaration= false, firstSimpleName= false;
	private List<MethodDeclaration> methods= new ArrayList<MethodDeclaration>();
	private List<FieldDeclaration> fields= new ArrayList<FieldDeclaration>();
	private List<TypeDeclaration> types= new ArrayList<TypeDeclaration>();
	private List<EnumDeclaration> enums= new ArrayList<EnumDeclaration>();
	private ArrayList<String> fanOuts= new ArrayList<>();
	
	@Override
	public boolean visit(SimpleName node) {
		if(firstSimpleName && node.resolveTypeBinding() != null && !node.resolveTypeBinding().isPrimitive() && !fanOuts.contains(node.resolveTypeBinding().getBinaryName())) {
			fanOuts.add(node.resolveTypeBinding().getBinaryName());
		} else {
			firstSimpleName= true;
		}
		return false;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		if(!firstTypeDeclaration) {
			firstTypeDeclaration= true;
			return super.visit(node);
		}
		enums.add(node);
		return false;
	}

	
	@Override
	public boolean visit(FieldDeclaration node) {
		fields.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		methods.add(node);
		return false;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		if(!firstTypeDeclaration) {
			firstTypeDeclaration= true;
			return super.visit(node);
		}
		types.add(node);
		return false;
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	
	public List<FieldDeclaration> getFieldDeclarations() {
		return fields;
	}
	
	public List<EnumDeclaration> getEnums() {
		return enums;
	}
	
	public List<TypeDeclaration> getTypes() {
		return types;
	}
	
	public ArrayList<String> getFanOut() {
		return fanOuts;
	}
	
}