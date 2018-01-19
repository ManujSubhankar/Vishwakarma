package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class NumberOfChildren implements IASoftwareMetric {

	private int value;
	private IAType owner;
	@Override
	public String getName() {
		return "Number Of Children";
	}

	@Override
	public String getShortName() {
		return "NC";
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean isToBeComputedAfterProjectBuilt() {
		return true;
	}

	@Override
	public MetricLevel getLevel() {
		return MetricLevel.Class;
	}

	@Override
	public void computeMetric() {
		value= owner.getSubClasses().size();
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
