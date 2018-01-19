package edu.sssihl.mca.vishwakarma.datamodel.metric;

import java.lang.reflect.Modifier;
import java.util.List;
import edu.sssihl.mca.vishwakarma.datamodel.IAField;
import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class NumberOfPublicFields implements IASoftwareMetric {
	private int value;
	private IAType owner;
	
	@Override
	public String getName() {
		return "Number Of Public Fields";
	}

	@Override
	public String getShortName() {
		return "NOPF";
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
		List<IAField> fields= owner.getFields();
		for(IAField i : fields) {
			if(Modifier.isPublic(i.getModifier()))
				value++;
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
		return owner.getProject().getThreshold().getClassThreshold(getShortName());
	}

}
