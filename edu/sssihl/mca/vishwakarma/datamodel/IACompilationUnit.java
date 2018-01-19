package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

public interface IACompilationUnit extends IAJavaElement {
	public List<IAType> getTypes();

	public List<IAType> getAllTypes();

	public String getPath();

	public CompilationUnit getCompilationUnit();

	public int getLinesOfCode();
}
