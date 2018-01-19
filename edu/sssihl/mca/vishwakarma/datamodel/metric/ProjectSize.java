package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IACompilationUnit;
import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAPackage;
import edu.sssihl.mca.vishwakarma.datamodel.IAProject;

public class ProjectSize implements IASoftwareMetric {
	
	IAJavaElement owner;
	int value= 0;
	@Override
	public String getName() {
		return "ProjectSize";
	}

	@Override
	public String getShortName() {
		return "PS";
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
		return MetricLevel.Project;
	}

	@Override
	public void computeMetric() {
		for(IAPackage pack : ((IAProject)owner).getAllPackages()) {
			for(IACompilationUnit comp : pack.getCompilationUnits()) 
				value+= comp.getLinesOfCode();
		}
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
		return owner.getProject().getThreshold().getClassThreshold(getShortName());
	}

}
