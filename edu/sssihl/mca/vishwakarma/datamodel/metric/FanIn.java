package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class FanIn implements IASoftwareMetric {
	int value;
	IAType owner;

	@Override
	public String getName() {
		return "Fan-In";
	}

	@Override
	public String getShortName() {
		return "FI";
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
		value= owner.getFanInTypes().size();
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
