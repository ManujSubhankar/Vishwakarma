package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.List;

public interface IAJavaElement {
	/**
	 * @return this element's kind.
	 */
	public AJavaElementType getElementType();

	/**
	 * @return String representing the Element name
	 */
	public String getElementName();

	/**
	 * @return parent of this Element or null in the case of a project
	 */
	public IAJavaElement getParentElement();

	/**
	 * @return Returns the immediate children of this element.
	 */
	public List<IAJavaElement> getChildren();

	/**
	 * @return Returns whether this element has one or more immediate children.
	 */
	public boolean hasChildren();

	public IAJavaElement getElement(String path);

	public IAProject getProject();
}
