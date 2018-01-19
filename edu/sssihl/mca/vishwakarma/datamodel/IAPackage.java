package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;

public interface IAPackage extends IAJavaElement {
	public List<IAType> getAllTypes();

	public List<IAMethod> getAllMethods();

	public List<IACompilationUnit> getCompilationUnits();
}
