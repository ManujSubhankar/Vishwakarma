package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;

public interface IAProject extends IAJavaElement {

	public List<IAType> getAllTypes();

	public List<IAMethod> getAllMethods();

	public List<IAPackage> getAllPackages();

	public IAJavaElement getElement(String path);

	public Configuration getMetricConfiguration();

	public Threshold getThreshold();

	public List<IASoftwareMetric> getMetrics();
}
