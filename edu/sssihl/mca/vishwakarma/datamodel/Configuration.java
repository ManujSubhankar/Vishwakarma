package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

public interface Configuration {
	public ArrayList<IASoftwareMetric> getClassLevelMetrics1();
	public ArrayList<IASoftwareMetric> getClassLevelMetrics2();
	public List<IADesignSmell> getClassLevelSmells1();
	public List<IADesignSmell> getClassLevelSmells2();
	public ArrayList<IASoftwareMetric> getMethodLevelMetrics();
	public List<IADesignSmell> getMethodLevelSmells1();
	public List<IASoftwareMetric> getProjectLevelMetrics();
	public List<IADesignSmell> getProjectLevelSmell();
}