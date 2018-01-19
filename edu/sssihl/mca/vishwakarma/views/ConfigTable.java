package edu.sssihl.mca.vishwakarma.views;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.xml.internal.ws.util.StringUtils;

import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaConfiguration;
import edu.sssihl.mca.vishwakarma.datamodel.Threshold;
import edu.sssihl.mca.vishwakarma.datamodel.VishwakarmaThreshold;

public class ConfigTable {

	private TableViewer tableViewer;
	private String[][] data;

	public ConfigTable(Composite parent, String[][] data) {
		this.tableViewer= new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		this.data= data;
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new ArrayContentProvider());
		createColumns(parent, tableViewer);


		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);

		tableViewer.setInput(data);
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {

		TableViewerColumn col1= new TableViewerColumn(tableViewer, 0);
		col1.getColumn().setText("Metric");
		col1.getColumn().setWidth(200);
		col1.getColumn().setResizable(true);
		col1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[0];
			}
		});

		TableViewerColumn col2= new TableViewerColumn(tableViewer, 0);
		col2.getColumn().setText("Threshold");
		col2.getColumn().setWidth(100);
		col2.getColumn().setResizable(true);
		col2.setEditingSupport(new MetricEditor(tableViewer));
		col2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[1];
			}
		});
		
		TableViewerColumn col3= new TableViewerColumn(tableViewer, 0);
		col3.getColumn().setText("Range");
		col3.getColumn().setWidth(100);
		col3.getColumn().setResizable(true);
		col3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[2];
			}
		});
	}

	public TableViewer getViewer() {
		return this.tableViewer;
	}

	public String[][] getData() {
		return this.data;
	}

	public static void display(Display display, VishwakarmaThreshold threshold) {
		//Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Task List - TableViewer Example");
		shell.setSize(450, 500);

		// Set layout for shell
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		// Create a composite to hold the children
		ConfigTable con= new ConfigTable(shell, threshold.getData());
		Button btn= new Button(shell, 0);
		btn.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				String[][] oldData= con.getData();
				TableItem[] newData= con.getViewer().getTable().getItems();
				for(int i= 0; i < newData.length; i++) {
					//IsDigit Check
					String enterdValue= newData[i].getText(1);
					if(!enterdValue.matches("[0-9]+")) {
						MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
						dialog.setText("ERROR...");
						dialog.setMessage("Invalid data in " + newData[i].getText(0) + " field");
						dialog.open();
						return;
					}
					//Range Check
					String[] range= newData[i].getText(2).split("-");
					if(Integer.parseInt(enterdValue) < Integer.parseInt(range[0]) || Integer.parseInt(enterdValue) > Integer.parseInt(range[1])) {
						MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
						dialog.setText("ERROR...");
						dialog.setMessage("Data in " + newData[i].getText(0) + " field is out of range");
						dialog.open();
						return;
					}
					oldData[i][1]= newData[i].getText(1);
				}
				threshold.setData(oldData);
				shell.dispose();
			}
			@Override
			public void mouseDown(MouseEvent arg0) {}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
		});
		btn.setText("SAVE");


		// Open the shell and run until a close event is detected
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		//display.dispose();
	}

	public static void main(String[] args) throws MalformedURLException  {
		Display display = new Display();
		Shell shell = new Shell(display);
		final Table table = new Table(shell, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		final int rowCount = 64, columnCount = 4;
		for (int i = 0; i < columnCount; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText("Column " + i);
		}
		for (int i = 0; i < rowCount; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int j = 0; j < columnCount; j++) {
				item.setText(j, "Item " + i + "-" + j);
			}
		}
		for (int i = 0; i < columnCount; i++) {
			table.getColumn(i).pack();
		}
		Point size = table.computeSize(SWT.DEFAULT, 200);
		table.setSize(size);
		shell.pack();
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					TableItem item = table.getItem(index);
					for (int i = 0; i < columnCount; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							System.out.println("Item " + index + "-" + i);
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}



class MetricEditor extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public MetricEditor(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((String[])element)[1];
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		((String[])element)[1]= (String.valueOf(userInputValue));
		viewer.update(element, null);
	}
} 