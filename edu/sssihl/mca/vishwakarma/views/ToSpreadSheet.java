package edu.sssihl.mca.vishwakarma.views;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

public class ToSpreadSheet {
	private XSSFWorkbook wb = new XSSFWorkbook();
	
	public XSSFWorkbook addSheet(TableViewer table, String name) {
	
		// add a worksheet
		XSSFSheet sheet = wb.createSheet(name);

		// shade the background of the header row
		XSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setBorderTop(CellStyle.BORDER_THIN);
		headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
		headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
		headerStyle.setBorderRight(CellStyle.BORDER_THIN);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		

		// add header row
		//Table table = table.getTable();
		TableColumn[] columns = table.getTable().getColumns();
		int rowIndex = 0;
		int cellIndex = 0;
		XSSFRow header = sheet.createRow((short) rowIndex++);
		for (TableColumn column : columns) {
			String columnName = column.getText();
			XSSFCell cell = header.createCell(cellIndex++);
			cell.setCellValue(column.getText());
			cell.setCellStyle(headerStyle);
		}

		// add data rows
		TableItem[] items = table.getTable().getItems();
		for (TableItem item : items) {
			// create a new row
			XSSFRow row = sheet.createRow((short) rowIndex++);
			cellIndex = 0;

			for (int i = 0; i < columns.length; i++) {
				// create a new cell
				String columnName = columns[i].getText();
				XSSFCell cell = row.createCell(cellIndex++);

				// set the horizontal alignment (default to RIGHT)
				XSSFCellStyle cellStyle = wb.createCellStyle();
				cellStyle.setAlignment(HorizontalAlignment.RIGHT);
				cell.setCellStyle(cellStyle);

				// set the cell's value
				String text = item.getText(i);
				cell.setCellValue(text);
			}
		}

		// autofit the columns
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn((short) i);
		}

		//return wb;
		return null;
	}

	public void save(String fileName) {
		Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { "Batch Files", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xls", "*.*" }); 
		dialog.setFilterPath(System.getProperty("user.home"));
		dialog.setFileName(fileName);
		String path= dialog.open();
		try {
			FileOutputStream fos = new FileOutputStream(path);
			wb.write(fos);
			fos.flush();
			fos.close();
			MessageDialog.openInformation(shell,
					"Save Workbook Successful",
					"Workbook saved to the file:\n\n" + path);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			String msg = ioe.getMessage();
			MessageDialog.openError(shell, 
					"Save Workbook Failed",
					"Could not save workbook to the file:\n\n" + msg);
		}
	}


}
