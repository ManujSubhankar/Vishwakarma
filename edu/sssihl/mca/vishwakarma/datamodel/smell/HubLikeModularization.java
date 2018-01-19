package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class HubLikeModularization implements IADesignSmell {

	IAType owner;
	@Override
	public String getName() {
		return "HubLikeModularization";
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
		return SmellType.Modularization;
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
		if(owner.getMetrics().get(9).getValue() > owner.getMetrics().get(9).getValue() 
						&& owner.getMetrics().get(10).getValue() > owner.getMetrics().get(10).getValue())
			return true;
		return false;
	}

	@Override
	public String getMessage() {
		return owner.getQualifiedName() + " have high fan-in and fan-out value.";
	}

}
