package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;

public class ParameterCount implements IASoftwareMetric {
	IAMethod owner;
	int value;
	
	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAMethod)owner;
	}
	
	@Override
	public String getName() {
		return "Parameter Count";
	}

	@Override
	public String getShortName() {
		return "PC";
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
		return MetricLevel.Method;
	}

	@Override
	public void computeMetric() {
		value= owner.getParameters().length;
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getMethodThreshold(getShortName());
	}
}
