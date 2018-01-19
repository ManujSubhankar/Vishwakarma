package edu.sssihl.mca.vishwakarma.datamodel.metric;

import edu.sssihl.mca.vishwakarma.datamodel.IAField;
import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

import java.util.*;

import org.eclipse.jdt.core.dom.Modifier;

public class LackOfCohesion implements IASoftwareMetric {
	private int value;
	private IAType owner;

	@Override
	public String getName() {
		return "Lack Of Cohesion";
	}

	@Override
	public String getShortName() {
		return "LCOM";
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean isToBeComputedAfterProjectBuilt() {
		return false;
	}

	@Override
	public MetricLevel getLevel() {
		return MetricLevel.Class;
	}

	@Override
	public void computeMetric() {
		ArrayList<IAMethod> nonStaticMethods= new ArrayList<>();
		ArrayList<IAField> nonStaticFields= new ArrayList<>();
		for(IAField i : owner.getFields())
		{
			if(i.getModifier() != Modifier.STATIC)
				nonStaticFields.add(i);
		}
		for(IAMethod i : owner.getMethods())
		{
			if(i.getModifier() != Modifier.STATIC && !i.isGetter() && !i.isSetter())
				nonStaticMethods.add(i);
		}
		if(nonStaticFields.size() == 0 || nonStaticMethods.size() == 0) {
			value= 0;
			return;
		}
		float ans= 0;
		int fmCount= 0;
		for(IAField i : nonStaticFields) {
			for(IAMethod j : nonStaticMethods) {
				if(j.getAccessedNonStaticFields().contains(i))
					fmCount++;
			}
		}
		ans= 1 - ((float)fmCount / (float)(nonStaticFields.size()*nonStaticMethods.size()));
		ans= (float) (Math.round(ans*100.0) / 100.0);
		value= Math.round(ans * 100);
	}

	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAType) owner;
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}

	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getClassThreshold(getShortName());
	}
	
	
	
}
