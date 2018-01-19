package edu.sssihl.mca.vishwakarma.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;

public class TableContentProvider implements IStructuredContentProvider{
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object[] getElements(Object arg0) {
		if(arg0 instanceof Object[])
			return (Object[]) arg0;
		return ((List<List<IASoftwareMetric>>)arg0).toArray();
	}
}
