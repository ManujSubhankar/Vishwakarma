package edu.sssihl.mca.vishwakarma.views;

import java.awt.image.ImageProducer;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import edu.sssihl.mca.vishwakarma.datamodel.Configuration;
import edu.sssihl.mca.vishwakarma.datamodel.IACompilationUnit;
import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.Threshold;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaConfiguration;
import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

public class MetricTable{

	private List<List<IASoftwareMetric>> input;
	private Configuration config= new VishwakarmaConfiguration();
	private Threshold threshold;
	private TableViewer table;
	private Composite parent;
	private String level;
	//private Action doubleClick;

	public MetricTable(Composite parent, int style,List<List<IASoftwareMetric>> input,String level) {
		this.input= input;
		this.parent= parent;
		this.level= level;		

	}

	public void setConfiguration(Configuration config) {
		this.config= config;
	}

	public void setThreshold(Threshold thres) {
		this.threshold= thres;
	}

	public TableViewer getTable() {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table= new TableViewer(sc,SWT.MULTI | SWT.BORDER | SWT.FILL);
		table.setContentProvider(new TableContentProvider());
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		table.setContentProvider(new TableContentProvider());
		table.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				createDoubleClickAction(table.getSelection()).run();
			}
		});

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setAlwaysShowScrollBars(true);
		sc.setContent(table.getTable());
		if(input.size() == 0 || input.get(0).size() == 0) return table;


		if(level.equals("class"))
			classLevel();
		else
			methodLevel();

		table.setContentProvider(new TableContentProvider());
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		table.setContentProvider(new TableContentProvider());

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setAlwaysShowScrollBars(true);
		sc.setContent(table.getTable());


		return table;
	}

	private void setData(int startIndex, TableViewerColumn[] column,int columnCount) {
		for(int i= 0; i < columnCount; i++) {
			column[i+startIndex]= new TableViewerColumn(table, SWT.LEFT);
			column[i+startIndex].getColumn().setText(input.get(0).get(i).getShortName());
			column[i+startIndex].getColumn().setWidth(50);
			column[i+startIndex].setLabelProvider(new TableLabelProvider(i,threshold));
		}

		table.setInput(input);
	}

	private void methodLevel() {
		int columnCount= config.getMethodLevelMetrics().size();
		TableViewerColumn[] column= new TableViewerColumn[columnCount + 2];


		column[0]= new TableViewerColumn(table, SWT.LEFT);
		column[0].getColumn().setText("Parent");
		column[0].getColumn().setWidth(200);
		column[0].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((List<IASoftwareMetric>)element).get(0).getOwnerElement().getParentElement().getElementName();
			}
			@Override
			public Image getImage(Object element) {
				IAType type= (IAType) ((List<IASoftwareMetric>)element).get(0).getOwnerElement().getParentElement();
				Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
				URL url = null;
				if(type.isClass())
					url = FileLocator.find(bundle, new Path("icons/class.png"), null);
				else if(type.isInterface())
					url = FileLocator.find(bundle, new Path("icons/interface.png"), null);
				else
					url = FileLocator.find(bundle, new Path("icons/enum.png"), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				return imageDcr.createImage();
			}

		});


		column[1]= new TableViewerColumn(table, SWT.LEFT);
		column[1].getColumn().setText("NAME");
		column[1].getColumn().setWidth(200);
		column[1].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((List<IASoftwareMetric>)element).get(0).getOwnerElement().getElementName();
			}
			@Override
			public Image getImage(Object element) {
				IAMethod method= (IAMethod) ((List<IASoftwareMetric>)element).get(0).getOwnerElement();
				Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
				URL url = null;
				if(Modifier.isPublic(method.getModifier()))
					url = FileLocator.find(bundle, new Path("icons/public.png"), null);
				else if(Modifier.isPrivate(method.getModifier()))
					url = FileLocator.find(bundle, new Path("icons/private.png"), null);
				else
					url = FileLocator.find(bundle, new Path("icons/protected.png"), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				return imageDcr.createImage();
			}

		});

		setData(2, column, columnCount);
	}

	private void classLevel() {
		int columnCount= config.getClassLevelMetrics1().size() + config.getClassLevelMetrics2().size();;
		List<IASoftwareMetric> met= config.getClassLevelMetrics1();
		met.addAll(config.getClassLevelMetrics2());
		List<IASoftwareMetric> met2= input.get(0);
		TableViewerColumn[] column= new TableViewerColumn[columnCount + 1];

		column[0]= new TableViewerColumn(table, SWT.LEFT);
		column[0].getColumn().setText("NAME");
		column[0].getColumn().setWidth(200);
		column[0].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((List<IASoftwareMetric>)element).get(0).getOwnerElement().getElementName();
			}
			@Override
			public Image getImage(Object element) {
				IAType type= (IAType) ((List<IASoftwareMetric>)element).get(0).getOwnerElement();
				Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
				URL url = null;
				if(type.isClass())
					url = FileLocator.find(bundle, new Path("icons/class.png"), null);
				else if(type.isInterface())
					url = FileLocator.find(bundle, new Path("icons/interface.png"), null);
				else
					url = FileLocator.find(bundle, new Path("icons/enum.png"), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				return imageDcr.createImage();
			}

		});

		setData(1, column, columnCount);
	}

	public static Action createDoubleClickAction(ISelection selection) {
		Action ans= new Action() {

			public void run() {
				IAJavaElement cu;
				Object obj= ((IStructuredSelection)selection).getFirstElement();

				if(obj instanceof IADesignSmell)
					cu= ((IADesignSmell)obj).getOwner();
				else
					cu= ((ArrayList<IASoftwareMetric>)obj).get(0).getOwnerElement();

				System.out.println("came..........: " + obj.getClass());

				IWorkbenchPage page= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				while(!(cu instanceof IACompilationUnit))
					cu= cu.getParentElement();
				IPath location= Path.fromOSString(((IACompilationUnit)cu).getPath());
				//File file= new File(((IACompilationUnit)cu).getPath());
				//IFileStore fileStore= EFS.getLocalFileSystem().getStore(file.toURI());
				IFile ifile= ResourcesPlugin.getWorkspace().getRoot().getFile(location);
				//IEditorInput editorInput = new FileEditorInput(ifile);
				IEditorDescriptor desc = PlatformUI.getWorkbench().
						getEditorRegistry().getDefaultEditor(ifile.getName());

				try {
					//IDE.openEditorOnFileStore(page, fileStore);
					//page.openEditor(editorInput, page.getActiveEditor().getEditorSite().getId());
					page.openEditor(new FileEditorInput(ifile), desc.getId());

				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		return ans;

	}

}
