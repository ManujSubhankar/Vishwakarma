package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;


public class MethodVisitor extends ASTVisitor {
	private ArrayList<IAField> accessedField= new ArrayList<>();
	private ArrayList<VariableDeclarationFragment> variables= new ArrayList<>();
	private List<IAField> classVariables;
	private ArrayList<String> fanOuts= new ArrayList<>();
	
	public MethodVisitor(List<IAField> list) {
		this.classVariables= list;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		for(IAField i : classVariables) {
			if(i.getBinding() != null && node.resolveBinding() != null) {
				if( i.getBinding().equals(node.resolveBinding()) )
					accessedField.add(i);
			}
		}
		if(node.resolveTypeBinding() != null && !node.resolveTypeBinding().isPrimitive() && !fanOuts.contains(node.resolveTypeBinding().getBinaryName())) 
			fanOuts.add(node.resolveTypeBinding().getBinaryName());
		
		return super.visit(node);
	}
	
	@Override 
	public boolean visit(VariableDeclarationFragment node) {
		variables.add(node);
		return super.visit(node);
	}
	
	public ArrayList<VariableDeclarationFragment> getVariables() {
		return variables;
	}
	
	public ArrayList<IAField> getAccessedFields() {
		return accessedField;
	}
	
	public ArrayList<String> getFanOut() {
		return fanOuts;
	}
}
