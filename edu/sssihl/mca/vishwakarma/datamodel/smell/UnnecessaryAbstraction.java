package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class UnnecessaryAbstraction implements IADesignSmell {

	IAType owner;
	
	@Override
	public String getName() {
		return "Unnecessary Abstraction";
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
	public void setOwner(IAJavaElement owner) {
		this.owner= (IAType) owner; 
	}

	@Override
	public IAJavaElement getOwner() {
		return owner;
	}

	@Override
	public boolean detectSmell() {
		if(owner.getMethods().size() == 0 && owner.getFields().size() < 5 && !owner.isEnum())
			return true;
		return false;
	}

	@Override
	public String getMessage() {
		return "This abstraction have no methods and very few feilds";
	}

}
