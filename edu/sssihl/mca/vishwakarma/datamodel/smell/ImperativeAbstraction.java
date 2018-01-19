package edu.sssihl.mca.vishwakarma.datamodel.smell;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Modifier;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class ImperativeAbstraction implements IADesignSmell {

	private IAType owner;
	@Override
	public String getName() {
		return "Imperative Abstraction";
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
	public IAJavaElement getOwner(){
		return owner;
	}

	@Override
	public boolean detectSmell() {
		if(owner.getMethods().size() == 1 && Modifier.isPublic(owner.getMethods().get(0).getModifier())) {
			if(owner.getMethods().get(0).getMetrics().get(2).getValue() > 100)
				return true;
		}
		return false;
	}

	@Override
	public String getMessage() {
		return owner.getQualifiedName() + " has only one method with access modifier as public.";
	}

}
