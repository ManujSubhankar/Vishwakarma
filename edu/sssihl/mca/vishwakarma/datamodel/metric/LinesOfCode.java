package edu.sssihl.mca.vishwakarma.datamodel.metric;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.sssihl.mca.vishwakarma.datamodel.IACompilationUnit;
import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class LinesOfCode implements IASoftwareMetric {
	
	private int value;
	private IAJavaElement owner;
	
	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= owner;
	}

	@Override
	public String getName() {
		return "Lines Of Code";
	}

	@Override
	public String getShortName() {
		return "LOC";
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean isToBeComputedAfterProjectBuilt() {
		return false;
	}

	@Override
	public MetricLevel getLevel() {
		if(owner instanceof IAType)
			return MetricLevel.Class;
		else
			return MetricLevel.Method;
	}

	@Override
	public void computeMetric() {
		IAJavaElement ele= owner.getParentElement();
		ASTNode node;
		while(!(ele instanceof IACompilationUnit)) 
			ele= ele.getParentElement();
		CompilationUnit cu= ((IACompilationUnit)ele).getCompilationUnit();
		if(owner instanceof IAType) {		
			if(((IAType)owner).isEnum())
				node= ((IAType)owner).getEnumDeclaration();
			else
				node= ((IAType)owner).getTypeDeclaration();		
		}
		else 
			node= ((IAMethod)owner).getMethodDeclaration();
		
		int start= cu.getLineNumber(node.getStartPosition()) - 1;
		int end= cu.getLineNumber(node.getStartPosition()+node.getLength()-1);
		this.value= end - start;
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		if(owner instanceof IAType)
			return owner.getProject().getThreshold().getClassThreshold(getShortName());
		return owner.getProject().getThreshold().getMethodThreshold(getShortName());
	}

}
