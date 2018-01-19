package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;

public interface IADesignSmell {
	public boolean detectSmell();
	public SmellLevel getLevel();
	public String getMessage();
	public String getName();
	public IAJavaElement getOwner();
	public SmellType getType();
	public boolean isToBeComputedAfterProjectBuild();
	public void setOwner(IAJavaElement owner);
}
