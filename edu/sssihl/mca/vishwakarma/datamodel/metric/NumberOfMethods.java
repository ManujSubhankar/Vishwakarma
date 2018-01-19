package edu.sssihl.mca.vishwakarma.datamodel.metric;

import org.eclipse.jdt.core.IMethod;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class NumberOfMethods implements IASoftwareMetric {

	private int gsCount= 0;
	private int value;
	private IAType owner;
	
	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAType)owner;
	}
	
	@Override
	public String getName() {
		return "Number Of Methods";
	}

	@Override
	public String getShortName() {
		return "NOM";
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
		value= owner.getMethods().size();
		for(IAMethod m : owner.getMethods()) {
			if(m.isGetter() || m.isSetter())
				gsCount++;
		}
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getClassThreshold(getShortName()) + gsCount;
	}

}
