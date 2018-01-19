package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class MultifacetedAbstraction implements IADesignSmell {
	
	IAType owner;
	int Lcom;
	
	@Override
	public void setOwner(IAJavaElement owner) {
		this.owner= (IAType) owner;
	}
	
	@Override
	public IAJavaElement getOwner(){
		return owner;
	}
	
	@Override
	public boolean detectSmell() {
		if(owner.getMetrics().get(5).getValue() > owner.getMetrics().get(5).getThreshold()) {
			Lcom= owner.getMetrics().get(5).getValue();
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "Multifaceted Abstraction";
	}

	@Override
	public boolean isToBeComputedAfterProjectBuild() {
		return false;
	}

	@Override
	public SmellLevel getLevel() {
		return SmellLevel.Class;
	}

	@Override
	public SmellType getType() {
		return SmellType.Abstraction;
	}

	@Override
	public String getMessage() {
		return "This class is less cohesion with LCOM value " + Lcom;
	}

}
