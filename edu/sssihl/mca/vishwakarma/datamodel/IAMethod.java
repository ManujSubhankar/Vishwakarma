package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;
import edu.sssihl.mca.vishwakarma.datamodel.metric.*;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public interface IAMethod extends IAJavaElement {
	public void addDesignSmell(IADesignSmell smell);

	public void addMetrics(IASoftwareMetric metric);

	public List<IAField> getAccessedFields();

	public List<IAField> getAccessedNonStaticFields();

	public String[] getExceptions();

	public List<String> getFanOutTypes();

	public MethodDeclaration getMethodDeclaration();

	public List<IASoftwareMetric> getMetrics();

	public int getModifier();

	public IAField[] getParameters();

	public String getReturnType();

	public List<IADesignSmell> getSmells();

	public List<IAField> getVariables();

	public boolean isGetter();

	public boolean isOverriding();

	public boolean isSetter();
}
