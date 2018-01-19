package edu.sssihl.mca.vishwakarma.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.sssihl.mca.vishwakarma.datamodel.IAProject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.dom.BodyDeclaration;

/**
 * Implements {@link ITreeContentProvider} and provides contents 
 * or nodes of the {@link AnalyserView}'s tree.
 * 
 * @author Manuj Subhankar Sahoo
 *
 */
public class ViewContentProvider implements ITreeContentProvider {
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<IAProject>)inputElement).toArray();
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		try {
			if(parentElement == null)
				return null;

			if(parentElement instanceof IAProject) {
				String pro= ((IAProject)parentElement).getElementName();
				return new String[] {"Project-level Metrics==="+pro,"Class-level Metrics==="+pro,
						"Method-level Metrics==="+pro, "Design Smells==="+pro};
			
			}
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof IResource)
			return ((IResource)element).getParent();
		
		if(element instanceof BodyDeclaration)
			return ((BodyDeclaration)element).getParent();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return true;
	}

	

}