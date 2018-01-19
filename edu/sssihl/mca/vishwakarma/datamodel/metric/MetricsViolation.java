package edu.sssihl.mca.vishwakarma.datamodel.metric;

import java.util.ArrayList;
import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAProject;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.Configuration;
import edu.sssihl.mca.vishwakarma.datamodel.Threshold;

public class MetricsViolation implements IASoftwareMetric {
	
	private IAJavaElement owner;
	private int value;
	private String[] metrics;
	private int[] violations;
	
	@Override
	public String getName() {
		return "MetricsViolation";
	}

	@Override
	public String getShortName() {
		return "MV";
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
		return MetricLevel.Project;
	}

	@Override
	public void computeMetric() {
		Threshold threshold= ((IAProject)owner).getThreshold();
		Configuration config= ((IAProject)owner).getMetricConfiguration();
		ArrayList<IASoftwareMetric> met= config.getClassLevelMetrics1();
		met.addAll(config.getClassLevelMetrics2());
		metrics= new String[met.size()];
		for(int i= 0;i < met.size(); i++)
			metrics[i]= met.get(i).getShortName();
		
		violations= new int[met.size()];
		for(IAType i : ((IAProject)owner).getAllTypes() ) {
			List<IASoftwareMetric> metrics= i.getMetrics();
			for(int j= 0; j < metrics.size(); j++) {
				if(metrics.get(j).getValue() > threshold.getClassThreshold(metrics.get(j).getShortName()))
					violations[j]++;
			}
		}
	}
	
	public String[] getMetrics() {
		return metrics;
	}
	
	public int[] getViolations() {
		return violations;
	}

	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= owner;
	}

	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		return 0;
	}

}
