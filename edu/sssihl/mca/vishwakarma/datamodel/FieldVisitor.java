package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleName;

public class FieldVisitor extends ASTVisitor {
	
	ArrayList<String> fanOuts= new ArrayList<>();
	
	@Override
	public boolean visit(SimpleName node) {
		if(node.resolveTypeBinding() != null && !node.resolveTypeBinding().isPrimitive() && !fanOuts.contains(node.resolveTypeBinding().getBinaryName())) 
			fanOuts.add(node.resolveTypeBinding().getBinaryName());
		return super.visit(node);
	}
	
	public ArrayList<String> getFanOut() {
		return fanOuts;
	}
}
