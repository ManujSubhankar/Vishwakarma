package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class UnutilizedAbstraction implements IADesignSmell {
	
	private IAType owner;
	
	@Override
	public boolean detectSmell() {
		if(owner.getFanInTypes().size() == 0)
			return true;
		return false;
	};

	@Override
	public String getName() {
		return "Unutilized Abstraction";
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
		return SmellType.Abstraction;
	}

	@Override
	public void setOwner(IAJavaElement owner) {
		this.owner= (IAType) owner;
	}
	
	@Override
	public IAJavaElement getOwner(){
		return owner;
	}

	@Override
	public String getMessage() {
		return "This abstraction is used no where in the project";
	}

}
