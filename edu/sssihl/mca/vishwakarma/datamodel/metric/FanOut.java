package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class FanOut implements IASoftwareMetric {

	IAType owner;
	int value;
	@Override
	public String getName() {
		return "Fan Out";
	}

	@Override
	public String getShortName() {
		return "FO";
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
		value= owner.getFanOutTypes().size();
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
