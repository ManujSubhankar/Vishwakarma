package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaConfiguration;

public class InsufficientModularization implements IADesignSmell {

	private IAType owner;
	private int nom= -1, wmc= -1;
	
	public boolean detectSmell() {
		if(owner.getMetrics().get(2).getValue() > owner.getMetrics().get(2).getThreshold())
			nom= owner.getMetrics().get(2).getValue();
		if(owner.getMetrics().get(4).getValue() > owner.getMetrics().get(4).getThreshold())
			wmc= owner.getMetrics().get(4).getValue();
		if(nom != -1 || wmc != -1)
			return true;
		
		return false;
	}
	
	@Override
	public String getName() {
		return "Insufficient Modularization";
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
		return SmellType.Modularization;
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
		message.append(owner.getQualifiedName() + " has ");
		if(wmc != -1)
			message.append("high value of Weighted method per class counts to " + wmc);
		if(nom != -1)
			message.append(" large number of method counts to " + nom);
		return message.toString();
	}

}
