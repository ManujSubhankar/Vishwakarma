package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import java.util.*;
public class WeightedMethodPerClass implements IASoftwareMetric {

	private int value;
	private IAType owner;
	@Override
	public String getName() {
		return "Weighted Method Per Class";
	}

	@Override
	public String getShortName() {
		return "WMC";
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
		int metricPosition= 0;
		List<IAMethod> methods= owner.getMethods();
		if(methods.size() == 0)
		{
			value= 0;
			return;
		}
		for(IASoftwareMetric i : methods.get(0).getMetrics()) {
			if(i.getShortName().equals("CC")) break;
			metricPosition++;
		}
		if(metricPosition == methods.get(0).getMetrics().size())
			throw new RuntimeException("Cyclomatic complexity not present in method level metric");
		for(IAMethod i : methods) 
			value+= i.getMetrics().get(metricPosition).getValue();
		
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
