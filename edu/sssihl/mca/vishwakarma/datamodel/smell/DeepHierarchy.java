package edu.sssihl.mca.vishwakarma.datamodel.smell;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaConfiguration;

public class DeepHierarchy implements IADesignSmell {
	
	IAType owner;
	int depth;

	@Override
	public String getName() {
		return "Deep Hierarchy";
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
		if(owner.getMetrics().get(6).getValue() > owner.getMetrics().get(6).getThreshold()) {
			this.depth= owner.getMetrics().get(6).getValue();
			return true;
		}
		return false;
	}

	@Override
	public String getMessage() {
		StringBuffer message= new StringBuffer();
		message.append(owner.getQualifiedName());
		message.append(" has a inheritance depth of ");
		message.append(depth);
		return message.toString();
	}

}
