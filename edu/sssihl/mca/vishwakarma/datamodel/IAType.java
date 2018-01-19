package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;

import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

public interface IAType extends IAJavaElement {
	public List<IAField> getFields();

	public List<IAMethod> getMethods();

	public List<IAType> getTypes();

	public List<IAType> getAllTypes();

	public boolean isClass();

	public boolean isInterface();

	public boolean isEnum();

	public String getSuperClass();

	public List<String> getSuperInterfaces();

	public IAType getType(String name);

	public void addSubClass(IAType child);

	public List<IAType> getSubClasses();

	public IAPackage getPackage();

	public List<String> getFanOutTypes();

	public void addFanInType(IAType type);

	public TypeDeclaration getTypeDeclaration();

	public EnumDeclaration getEnumDeclaration();

	public List<IASoftwareMetric> getMetrics();

	public List<IADesignSmell> getSmells();

	public List<IAType> getFanInTypes();

	public void doAfterBuildTasks();

	public void addDesignSmell(IADesignSmell smell);

	public void addMetrics(IASoftwareMetric metric);

	public String getQualifiedName();
}
