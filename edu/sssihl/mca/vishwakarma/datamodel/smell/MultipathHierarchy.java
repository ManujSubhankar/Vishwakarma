package edu.sssihl.mca.vishwakarma.datamodel.smell;

import java.util.ArrayList;

import org.eclipse.jdt.internal.core.NameLookup.Answer;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class MultipathHierarchy implements IADesignSmell {

	private IAType owner;
	private ArrayList<IAType> implementers= new ArrayList<>();
	
	@Override
	public String getName() {
		return "Multipath Hierarchy";
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
	public IAJavaElement getOwner(){
		return owner;
	}

	@Override
	public boolean detectSmell() {
		String superClass;
		IAType parent= owner;
		ArrayList<IAType> ancestors= new ArrayList<>();
		while(true) {
			superClass= parent.getSuperClass();
			parent= (IAType) owner.getProject().getElement(superClass);
			if(parent == null)
				break;
			ancestors.add(parent);
		}
		
		for(int i= 0; i < ancestors.size();i++ ) {
			for(String Interface : ancestors.get(i).getSuperInterfaces()) {
				if(owner.getSuperInterfaces().contains(Interface)) {
					this.implementers.add(ancestors.remove(i));
					break;
				}
			}
		}
		
		if(implementers.size() == 0) {
			implementers= null;
			return false;
		}
		return true;
	}
	@Override
	public String getMessage() {
		StringBuffer message= new StringBuffer();
		message.append("Same interface implemented by ");
		message.append(owner.getQualifiedName());
		message.append(" and its ancestors ");
		for(IAType i : implementers)
			message.append(i.getQualifiedName() + ", ");
		
		return message.toString();
	}

}
