package edu.sssihl.mca.vishwakarma.views;


import java.net.URL;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import edu.sssihl.mca.vishwakarma.datamodel.IAProject;


/**
 * Implements {@link ILabelProvider} and provides Label 
 * of the tree nodes of {@link AnalyserView}'s tree
 * 
 * @author Manuj Subhankar Sahoo
 *
 */

public class ViewLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
		URL url = null;
		if(element instanceof IAProject)
			url = FileLocator.find(bundle, new Path("icons/project.png"), null);
		else
			url = FileLocator.find(bundle, new Path("icons/metrics1.png"), null);
	    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
		return imageDcr.createImage();
		//return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof IAProject)
			return ((IAProject)element).getElementName();
		return element.toString().split("===")[0];
	}



}