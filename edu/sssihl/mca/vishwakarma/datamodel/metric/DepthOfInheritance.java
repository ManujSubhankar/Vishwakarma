package edu.sssihl.mca.vishwakarma.datamodel.metric;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class DepthOfInheritance implements IASoftwareMetric {
	
	private IAType owner;
	private int value;
	@Override
	public String getName() {
		return "Depth Of Inheritance";
	}

	@Override
	public String getShortName() {
		return "DIT";
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
		return MetricLevel.Class;
	}

	@Override
	public void computeMetric() {
		TypeDeclaration td= owner.getTypeDeclaration();
		value= 0;
		if(td == null) return;
		ITypeBinding binding= td.resolveBinding();

		while(binding != null) {
			if(binding.getName().toString().equals("Object")) value--;
			binding= binding.getSuperclass();
			value++;
		}
		value-= 1;
	}

	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAType) owner;
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getClassThreshold(getShortName());
	}

}
