package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;

public interface IASoftwareMetric{	
	public void computeMetric();
	public MetricLevel getLevel();
	public String getName();
	public IAJavaElement getOwnerElement();
	public String getShortName();
	public int getValue();
	public boolean isToBeComputedAfterProjectBuilt();	
	public void setOwnerElement(IAJavaElement owner);
	public int getThreshold();
}