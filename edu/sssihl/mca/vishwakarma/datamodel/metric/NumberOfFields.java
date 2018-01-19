package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class NumberOfFields implements IASoftwareMetric {
	private int value;
	private IAType owner;
	
	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAType)owner;
	}

	@Override
	public String getName() {
		return "Number Of Fields";
	}

	@Override
	public String getShortName() {
		return "NOF";
	}

	@Override
	public int getValue() {
		return value;
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
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
		value= owner.getFields().size();
	}
	
	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getClassThreshold(getShortName());
	}
}
