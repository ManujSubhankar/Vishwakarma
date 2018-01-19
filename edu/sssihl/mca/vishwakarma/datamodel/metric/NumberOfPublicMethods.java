package edu.sssihl.mca.vishwakarma.datamodel.metric;

import java.lang.reflect.Modifier;
import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class NumberOfPublicMethods implements IASoftwareMetric {
	private int gsCount= 0;
	private int value;
	private IAType owner;
	
	@Override
	public String getName() {
		return "Number Of Public Methods";
	}

	@Override
	public String getShortName() {
		return "NOPM";
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
		value= 0;
		List<IAMethod> methods= owner.getMethods();
		for(IAMethod i : methods) {
			if(Modifier.isPublic(i.getModifier())) {
				value++;
				if(i.isGetter() || i.isSetter())
					gsCount++;
			}
		}
	}

	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAType)owner;
	}
	
	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getClassThreshold(getShortName()) + gsCount;
	}

}
