package de.unima.ki.pmmc.evaluator.utils.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;

public class MultipleSheetAcrossGSReader implements GSReader {

	private String path;
	private FileInputStream excelFile;
	private Workbook workbook;
	private Sheet sheet;
	private Iterator<Row> iterator;
	private String sourcePrefix, targetPrefix;
	private int sourceIndex, targetIndex, sheetIndex;
	private String name;
	
	
	public MultipleSheetAcrossGSReader(String path, String name) throws IOException {
		this.path = path;
		this.excelFile = new FileInputStream(new File(path));
		this.workbook = new XSSFWorkbook(excelFile);
		this.sheet = workbook.getSheetAt(0);
		this.iterator = sheet.iterator();
		this.sourcePrefix = null;
		this.targetPrefix = null;
		this.sourceIndex = 1;
		this.targetIndex = 2;
		this.sheetIndex = 0;
		this.name = name;
	}
	
	@Override
	public Alignment nextAlignment() {
		Alignment alignment = null;
		headIterator();
		while(iterator.hasNext()) {
			Row row = iterator.next();
			String label1 = (row.getCell(sourceIndex) != null) ? 
					row.getCell(sourceIndex).getStringCellValue() : "";
			String label2 = (row.getCell(targetIndex) != null) ? 
					row.getCell(targetIndex).getStringCellValue() : "";
			if(contained(label1) && contained(label2)) {
				sourcePrefix = label1.split(" ")[1];
				targetPrefix = label2.split(" ")[1];
				alignment = createNewAlignment("birthCertificate_" + sourcePrefix + "-" + "birthCertificate_" + targetPrefix);
			} else if(!label1.isEmpty() && !label2.isEmpty()){
				try {
				alignment.add(new Correspondence("http://birthCertificate_" + sourcePrefix + "#" + label1, 
						"http://birthCertificate_" + targetPrefix + "#" + label2, 1));
				} catch(NullPointerException e) {
					System.out.println();
				}
				} else {
					iterator = sheet.iterator();
					sourceIndex+=2;
					targetIndex+=2;
					if(!Objects.isNull(alignment)) {
						return alignment;					
					} else {
						sheetIndex++;
						sourceIndex = 1;
						targetIndex = 2;
						sheet = workbook.getSheetAt(sheetIndex);
						headIterator();
					}
				}
			}
		//Longest rows found
		iterator = sheet.iterator();
		sourceIndex+=2;
		targetIndex+=2;
		if(!Objects.isNull(alignment)) {
			return alignment;					
		}
		  return null;
	}
	
	private void headIterator() {
		iterator = sheet.iterator();
		for (int i = 0; i < 3; i++) {
			if(iterator.hasNext()) iterator.next();
		}
	}
	
	public static void main(String[] args) {
		try {
			MultipleSheetAcrossGSReader reader = 
					new MultipleSheetAcrossGSReader("src/main/resources/data/dataset2/New_GS_birth/GS2_BR.xlsx", "test");
		for (int i = 0; i < 36; i++) {
			Alignment a = reader.nextAlignment();
			System.out.println(a);
			System.out.println(a.size());
			System.out.println();
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean contained(String label) {
		return Arrays.asList(new String[] {
				"p31", "p32", "p33", "p34",
				"p246", "p247", "p248", "p249", "p250"
		}).
				stream().
				anyMatch(test -> 
				{return label.contains(test);});
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
