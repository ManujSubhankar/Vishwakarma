package edu.sssihl.mca.vishwakarma.views;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.Threshold;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaConfiguration;
import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;

public class TableLabelProvider extends ColumnLabelProvider {
	private int index;
	private Threshold threshold;

	public TableLabelProvider(int index, Threshold threshold) {
		this.index= index;
		this.threshold= threshold;
	}

	@Override
	public String getText(Object element) {
		return ""+((List<IASoftwareMetric>)element).get(index).getValue();
	}

	@Override
	public Color getBackground(Object element) {
		IASoftwareMetric metric= ((List<IASoftwareMetric>)element).get(index);
		if(metric.getOwnerElement() instanceof IAType) {
			if(metric.getValue() > metric.getThreshold())
				return new Color(null, 255,0,0);
		} else {
			if(metric.getValue() > metric.getThreshold())
				return new Color(null, 255,0,0);
		}
		return null;
	}
}
