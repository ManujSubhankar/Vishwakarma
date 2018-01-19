package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class WideHierarchy implements IADesignSmell {
	
	IAType owner;
	int width;

	@Override
	public String getName() {
		return "Wide Hierarchy";
	}

	@Override
	public boolean isToBeComputedAfterProjectBuild() {
		return true;
	}

	@Override
	public SmellLevel getLevel() {
		return SmellLevel.Class;
	}

	@Override
	public SmellType getType() {
		return SmellType.Hierarchy;
	}

	@Override
	public void setOwner(IAJavaElement owner) {
		this.owner= (IAType) owner;
	}

	@Override
	public IAJavaElement getOwner() {
		return owner;
	}

	@Override
	public boolean detectSmell() {
		width= owner.getMetrics().get(7).getValue();
		if(width > owner.getMetrics().get(7).getThreshold() && !owner.isInterface())
			return true;

		return false;
	}

	@Override
	public String getMessage() {
		return "This abstraction have too many children i.e. " + width;
	}

}
