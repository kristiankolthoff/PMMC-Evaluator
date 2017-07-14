package de.unima.ki.pmmc.evaluator.utils.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;

public class MultipleSheetMultipleColSpanGSReader implements GSReader {

	private String path;
	private FileInputStream excelFile;
	private Workbook workbook;
	private Sheet sheet;
	private Iterator<Row> iterator;
	private String sourcePrefix, targetPrefix;
	private int sourceIndex, targetIndex, sheetIndex;
	private String name;
	
	
	public MultipleSheetMultipleColSpanGSReader(String path, String name) throws IOException {
		this.path = path;
		this.excelFile = new FileInputStream(new File(path));
		this.workbook = new XSSFWorkbook(excelFile);
		this.sheet = workbook.getSheetAt(0);
		this.iterator = sheet.iterator();
		this.sourcePrefix = null;
		this.targetPrefix = null;
		this.sourceIndex = 0;
		this.targetIndex = 2;
		this.sheetIndex = 0;
		this.name = name;
	}
	
	@Override
	public Alignment nextAlignment() {
		Alignment alignment = null;
		while(iterator.hasNext()) {
			Row row = iterator.next();
			String label1 = (row.getCell(sourceIndex) != null) ? 
					row.getCell(sourceIndex).getStringCellValue() : "";
			String label2 = (row.getCell(targetIndex) != null) ? 
					row.getCell(targetIndex).getStringCellValue() : "";
			if(label1.contains("Element") && label2.contains("Element")) {
				sourcePrefix = label1.split("_")[1];
				targetPrefix = label2.split("_")[1];
				alignment = createNewAlignment("birthCertificate_" + sourcePrefix + "-" + "birthCertificate_" + targetPrefix);
			} else if(!label1.isEmpty() && !label2.isEmpty()){
				String[] ids1 = label1.split(",");
				String[] ids2 = label2.split(",");
				for (int i = 0; i < ids1.length; i++) {
					for (int j = 0; j < ids2.length; j++) {
						alignment.add(new Correspondence("http://birthCertificate_" + sourcePrefix + "#" + ids1[i], 
								"http://birthCertificate_" + targetPrefix + "#" + ids2[j], 1));
					}
				}
				} else {
					iterator = sheet.iterator();
					sourceIndex+=5;
					targetIndex+=5;
					if(!Objects.isNull(alignment)) {
						return alignment;					
					} else {
						sheetIndex++;
						sourceIndex = 0;
						targetIndex = 2;
						sheet = workbook.getSheetAt(sheetIndex);
						iterator = sheet.iterator();
					}
				}
			}
		//Longest rows found
		iterator = sheet.iterator();
		sourceIndex+=5;
		targetIndex+=5;
		if(!Objects.isNull(alignment)) {
			return alignment;					
		}
		  return null;
	}
	
	
	public static void main(String[] args) {
		try {
			MultipleSheetMultipleColSpanGSReader reader = 
					new MultipleSheetMultipleColSpanGSReader("src/main/resources/data/dataset2/New_GS_birth/Process_mapping.xlsx", "test");
		for (int i = 0; i < 3; i++) {
			System.out.println(reader.nextAlignment());
			System.out.println();
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private Alignment createNewAlignment(String name) {
		Alignment alignment = new Alignment();
		alignment.setName(sourcePrefix + "-" + targetPrefix);
		return alignment;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getName() {
		return name;
	}


}
