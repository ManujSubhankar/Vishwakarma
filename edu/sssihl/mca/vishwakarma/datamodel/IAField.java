package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;

import org.eclipse.jdt.core.dom.IBinding;

public interface IAField extends IAJavaElement {
	public void clearBinding();

	public IBinding getBinding();

	public List<String> getFanOutTypes();

	public int getModifier();

	public String getTypeName();
}
