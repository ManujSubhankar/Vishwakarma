package edu.sssihl.mca.vishwakarma.datamodel.smell;

import java.util.ArrayList;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class RebelliousHierarchy implements IADesignSmell {

	IAType owner;
	ArrayList<IAMethod> rebelliousMethods= new ArrayList<>();
	@Override
	public String getName() {
		return "Rebellious Hierarchy";
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
		for(IAMethod method : owner.getMethods()) {
			for(IADesignSmell smell : method.getSmells()) {
				if(smell instanceof RebelliousMethod) {
					rebelliousMethods.add(method);
					break;
				}
			}
		}
		if(rebelliousMethods.size() == 0) {
			rebelliousMethods= null;
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		StringBuffer message= new StringBuffer();
		message.append("This abstraction has not actually implemented ");
		for(IAMethod i : rebelliousMethods)
			message.append(i.getElementName() + ", ");
		message.append("methods which caused Smell of Rebellious hierarchy.");
		return message.toString();
	}

}
