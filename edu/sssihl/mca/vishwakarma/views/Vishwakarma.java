package edu.sssihl.mca.vishwakarma.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.AProject;
import edu.sssihl.mca.vishwakarma.datamodel.IACompilationUnit;
import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;
import edu.sssihl.mca.vishwakarma.datamodel.IAPackage;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaConfiguration;
import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.metric.MetricsViolation;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaThreshold;

public class Vishwakarma extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.sssihl.mca.vishwakarma.views.Vishwakarma";

	private TreeViewer viewer;
	private ScrolledComposite scroll;
	private Action doubleClickAction, dC2;
	private Composite infoArea, infoContent;
	private TableViewer table;
	private Composite group;
	private AProject project;
	private List<AProject> projects= new ArrayList<AProject>();
	private Composite parent;
	private Button configButton;
	private VishwakarmaConfiguration config= new VishwakarmaConfiguration();
	private VishwakarmaThreshold threshold= new VishwakarmaThreshold();
	private Point dimension= new Point(0, 0);
	//private Composite progressComposite;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.parent= parent;	
		threshold.getReady();
		
		scroll= new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
		group= new Composite(scroll, SWT.NONE);
		group.setSize(1000, 400);
		group.setLayout(new GridLayout(5, true));
		GridDataFactory.fillDefaults().grab(true, true).hint(1000, 400).applyTo(group);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		scroll.setContent(group);
		scroll.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		viewer = new TreeViewer(group, SWT.BORDER);
		
		GridData gridData = new GridData();
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.verticalSpan = 3;
		viewer.getTree().setLayoutData(gridData);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		for(IProject i : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				if(i.isOpen() && i.hasNature("org.eclipse.jdt.core.javanature"))
					projects.add(new AProject(JavaCore.create(i), config, threshold));
			} catch (CoreException e) { e.printStackTrace(); }
		}
		viewer.setInput(projects);
		
		hookMenu();
		HookDoubleClickListener();
		
		infoArea= new Composite(group, 0);
		infoArea.setTouchEnabled(true);
		infoArea.pack();
		infoArea.setLayout(new GridLayout());
		GridData gridData1= new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData1.horizontalSpan= 4;
		gridData1.grabExcessHorizontalSpace = true;
		infoArea.setLayoutData(gridData1);
		
		createNewContent();
		addConfigButton();
	}
	
	private void addConfigButton() {
		configButton= new Button(group, 0);
		configButton.setText("Configure");
		configButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				ConfigTable.display(PlatformUI.getWorkbench().getDisplay(), threshold);
			}
			@Override
			public void mouseDown(MouseEvent arg0) {}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
		});
	}

	private void createNewContent() {
		infoContent= new Composite(infoArea, 0);
		infoContent.setTouchEnabled(true);
		infoContent.pack();
		infoContent.setLayout(new GridLayout());
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan= 4;
		gridData.grabExcessHorizontalSpace = true;
		infoContent.setLayoutData(gridData);
	}

	private void hookMouseMoveListener(Table table) {
		table.addListener(SWT.MouseMove, new Listener() {
			TableItem it;
			@Override
			public void handleEvent(Event e) {
				TableItem item= table.getItem(new Point(e.x, e.y));
				if(it != null)
					it.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
				it= item;
			}
		});

	}

	private TableViewer createSmellTable(Composite parent, List<IADesignSmell> input) {
		ScrolledComposite sc = new ScrolledComposite(infoContent, SWT.V_SCROLL);
	    sc.setLayout(new GridLayout(1,false));
	    sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		TableViewerColumn type, Package, smell;
		TableViewer table= new TableViewer(sc, SWT.MULTI | SWT.BORDER);
		GridData data1= new GridData();
		data1.verticalAlignment = GridData.FILL;
		data1.verticalSpan= 4;
		
		table.getTable().setLayoutData(data1);
		table.setContentProvider(new TableContentProvider());
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		table.setContentProvider(new TableContentProvider());
		table.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				MetricTable.createDoubleClickAction(table.getSelection()).run();
			}
		});
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
	    sc.setAlwaysShowScrollBars(true);
	    //sc.setMinSize(table.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setContent(table.getTable());
		
		Text message= new Text(infoContent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		message.setTouchEnabled(true);
		message.pack();
		GridData data2= new GridData();
		data2.verticalAlignment = GridData.FILL;
		data2.horizontalAlignment= GridData.FILL;
		data2.verticalSpan= 2;
		data2.heightHint= 60;
		message.setLayoutData(data2);
		
		message.setEditable(false);
		

		hookMouseMoveListener(table.getTable());
		smell= new TableViewerColumn(table,  SWT.LEFT);
		smell.getColumn().setText("Smell");
		smell.getColumn().setWidth(200);
		smell.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IADesignSmell)element).getName();
			}
		});

		Package= new TableViewerColumn(table,  SWT.LEFT);
		Package.getColumn().setText("Package");
		Package.getColumn().setWidth(200);
		Package.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IAType type= (IAType) ((IADesignSmell)element).getOwner();
				IAJavaElement pack= type.getParentElement();
				while(!(pack instanceof IAPackage))
					pack= pack.getParentElement();
				return pack.getElementName();
			}
		});

		type= new TableViewerColumn(table,  SWT.LEFT);
		type.getColumn().setText("Type");
		type.getColumn().setWidth(200);
		type.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IADesignSmell)element).getOwner().getElementName();
			}

			@Override
			public Image getImage(Object element) {
				IAType type= (IAType) ((IADesignSmell)element).getOwner();
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
		table.setInput(input.toArray());
		table.getTable().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				IADesignSmell smell= (IADesignSmell) table.getTable().getSelection()[0].getData();
				message.setText(smell.getMessage());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		return table;
	}

	public TableViewer createMetricsTable(List<List<IASoftwareMetric>> input,String level) {
		int columnCount;


		if(level.equals("class"))
			columnCount= config.getClassLevelMetrics1().size() + config.getClassLevelMetrics2().size();
		else
			columnCount= config.getMethodLevelMetrics().size();
		TableViewerColumn[] column= new TableViewerColumn[columnCount + 1];
		
		ScrolledComposite sc = new ScrolledComposite(infoContent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		TableViewer table= new TableViewer(sc,SWT.MULTI | SWT.BORDER | SWT.FILL);
		if(input.size() == 0 || input.get(0).size() == 0) return table;
		table.setContentProvider(new TableContentProvider());
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		table.setContentProvider(new TableContentProvider());
		
		sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
	    sc.setAlwaysShowScrollBars(true);
		sc.setContent(table.getTable());
		
		hookMouseMoveListener(table.getTable());

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
				if(level.equals("class")) {
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
				}else if (level.equals("method")) {
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
				return null;
			}

		});
		for(int i= 0; i < columnCount; i++) {
			column[i+1]= new TableViewerColumn(table, SWT.LEFT);
			column[i+1].getColumn().setText(input.get(0).get(i).getShortName());
			column[i+1].getColumn().setWidth(50);
			column[i+1].setLabelProvider(new TableLabelProvider(i,threshold));
		}

		table.setInput(input);
		return table;
	}


	private void projectInfo() {
		Composite comp1 = new Composite(infoContent, SWT.NONE);
		comp1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		comp1.setLayout(new FillLayout());
		comp1.setVisible(true);
		ChartDrawer chart= new ChartDrawer();
		List<IASoftwareMetric> metrics= config.getClassLevelMetrics1();
		metrics.addAll(config.getClassLevelMetrics2());
		String[] xSeries= new String[metrics.size()];
		for(int i= 0; i < xSeries.length; i++)
			xSeries[i]= metrics.get(i).getShortName();
		chart.setXSeries(xSeries);
		chart.setYSeries(((MetricsViolation)project.getMetrics().get(0)).getViolations());
		chart.setTitle("Classes : " + project.getAllTypes().size() + "\nPackages : " + project.getAllPackages().size()
				+ "\nLOC : " + project.getMetrics().get(1).getValue());
		chart.createChart(comp1);
	}

	/**
	 * Creates the action that is to be performed on double click.
	 * Adds a double click listener to the viewer with the above 
	 * created action.
	 */
	private void HookDoubleClickListener() {
		doubleClickAction= new Action() {
			public void run() {
				ISelection sel= viewer.getSelection();
				Object obj= ((IStructuredSelection)sel).getFirstElement();
				if(obj instanceof AProject){
					if (viewer.getExpandedState(obj))
						viewer.collapseToLevel(obj, AbstractTreeViewer.ALL_LEVELS);
					else
						viewer.expandToLevel(obj, 1);
				}
				if(!(obj instanceof String)) {
					return;
				}
				if(((String)obj).startsWith("Project-level Metrics")) {
					String temp= ((String)obj).split("===")[1];
					for(AProject i : projects) {
						if(i.getElementName().equals(temp)) {
							project= i;
							break;
						}
					}
					infoContent.dispose();
					createNewContent();
					projectInfo();
					parent.layout(true, true);
				}
				else if(((String)obj).startsWith("Class-level Metrics")) {
					String temp= ((String)obj).split("===")[1];
					for(AProject i : projects) {
						if(i.getElementName().equals(temp)) {
							project= i;
							break;
						}
					}
					ArrayList<List<IASoftwareMetric>> input= new ArrayList<>();
					for(IAType i : project.getAllTypes())
						input.add(i.getMetrics());
					infoContent.dispose();
					createNewContent();
					MetricTable t= new MetricTable(infoContent, 0, input, "class");
					t.setThreshold(threshold);
					t.getTable();
					//createMetricsTable( input, "class");
					parent.layout(true, true);

				}
				else if(((String)obj).startsWith("Method-level Metrics")) {
					String temp= ((String)obj).split("===")[1];
					for(AProject i : projects) {
						if(i.getElementName().equals(temp)) {
							project= i;
							break;
						}
					}
					ArrayList<List<IASoftwareMetric>> input= new ArrayList<>();
					for(IAMethod i : project.getAllMethods())
						input.add(i.getMetrics());
					infoContent.dispose();
					createNewContent();
					MetricTable t= new MetricTable(infoContent, 0, input, "method");
					t.setThreshold(threshold);
					t.getTable();
					//createMetricsTable(input, "method");
					parent.layout(true, true);
				}
				else if(((String)obj).startsWith("Design Smells")) {
					String temp= ((String)obj).split("===")[1];
					for(AProject i : projects) {
						if(i.getElementName().equals(temp)) {
							project= i;
							break;
						}
					}
					ArrayList<IADesignSmell> input= new ArrayList<>();
					for(IAType i : project.getAllTypes()) {
						input.addAll(i.getSmells());
					}
					infoContent.dispose();
					createNewContent();
					createSmellTable(infoContent, input);
					parent.layout(true, true);
				}

			}
		};
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void hookMenu() {
		final Menu menu= new Menu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		menu.addMenuListener(new MenuAdapter()
		{
			public void menuShown(MenuEvent e)
			{
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++)
					items[i].dispose();
				if(!(viewer.getTree().getSelection()[0].getData() instanceof AProject)) return;
				MenuItem newItem = new MenuItem(menu, SWT.NONE);
				newItem.setText("Refresh");
				newItem.setData(viewer.getTree().getSelection()[0].getData());
				newItem.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						AProject pro= (AProject)((MenuItem)arg0.getSource()).getData();
						for(IProject i : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
							try {
								if(i.isOpen() && i.hasNature("org.eclipse.jdt.core.javanature") && i.getName().equals(pro.getElementName())) {
									pro= new AProject(JavaCore.create(i), config, threshold);
									break;
								}
							} catch (CoreException e) { e.printStackTrace(); }
						}
						for(int i= 0; i < projects.size(); i++) {
							if(projects.get(i).getElementName().equals(pro.getElementName())) {
								projects.set(i, pro);
								break;
							}
						}
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {}
				});


				MenuItem menuItem1= new MenuItem(menu,SWT.None);
				menuItem1.setText("Export");
				menuItem1.setData(viewer.getTree().getSelection()[0].getData());
				menuItem1.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						AProject pro= (AProject)((MenuItem)arg0.getSource()).getData();
						ToSpreadSheet sp= new ToSpreadSheet();
						Composite com= new Composite(parent, 0);
						MetricTable t;
						
						
						//Class level metrics
						ArrayList<List<IASoftwareMetric>> inputClass= new ArrayList<>();
						for(IAType i : pro.getAllTypes())
							inputClass.add(i.getMetrics());
						t= new MetricTable(infoContent, 0, inputClass, "class");
						t.setThreshold(threshold);
						sp.addSheet(t.getTable(), "Class-Level Metric");
						
						//Method level metrics
						ArrayList<List<IASoftwareMetric>> inputMethod= new ArrayList<>();
						for(IAMethod i : pro.getAllMethods())
							inputMethod.add(i.getMetrics());
						t= new MetricTable(infoContent, 0, inputMethod, "method");
						t.setThreshold(threshold);
						sp.addSheet(t.getTable(), "Method-Level Metric");
						
						//Design Smell
						ArrayList<IADesignSmell> inputSmell= new ArrayList<>();
						for(IAType i : pro.getAllTypes()) {
							inputSmell.addAll(i.getSmells());
						}
						sp.addSheet(createSmellTable(com, inputSmell), "Design Smells");
						
						//Save the document
						sp.save(pro.getElementName());
						com.dispose();
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {}
				});

				//				try {
				//					Refactor.main(null);
				//				} catch (JavaModelException | MalformedTreeException | BadLocationException e1) {
				//					// TODO Auto-generated catch block
				//					e1.printStackTrace();
				//				}
			}
		});
	}

	@Override
	public void setFocus() {
		scroll.setFocus();
	}

}