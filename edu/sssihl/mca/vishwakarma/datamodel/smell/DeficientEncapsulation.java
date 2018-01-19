package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class DeficientEncapsulation implements IADesignSmell {

	private IAType owner;
	private int noPublicFields;
	
	public boolean detectSmell() {
		noPublicFields= owner.getMetrics().get(1).getValue();
		if(noPublicFields > owner.getMetrics().get(1).getThreshold()) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getName() {
		return "Deficient Encapsulation";
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
		return SmellType.Encapsulation;
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
		StringBuffer message= new StringBuffer();
		message.append(owner.getQualifiedName());
		message.append(" has ");
		message.append(noPublicFields);
		message.append(" public fields");
		return message.toString();
	}

}
