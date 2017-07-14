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

/**
 * Implements a goldstandard reader for "goldstandard-pnml.xlsx"
 *
 */
public class SinglePageGSReader implements GSReader{

	private String path;
	private FileInputStream excelFile;
	private Workbook workbook;
	private Sheet sheet;
	private Iterator<Row> iterator;
	private String sourcePrefix, targetPrefix;
	private String name;
	
	public SinglePageGSReader(String path, String name) throws IOException {
		this.path = path;
		this.excelFile = new FileInputStream(new File(path));
		this.workbook = new XSSFWorkbook(excelFile);
		this.sheet = workbook.getSheetAt(0);
		this.iterator = sheet.iterator();
		this.sourcePrefix = null;
		this.targetPrefix = null;
		this.name = name;
	}
	
	@Override
	public Alignment nextAlignment() {
		Alignment alignment = null;
		if(sourcePrefix != null && targetPrefix != null) {
			alignment = createNewAlignment(sourcePrefix + "-" + targetPrefix);
		}
		while(iterator.hasNext()) {
			Row row = iterator.next();
			String label1 = row.getCell(0).getStringCellValue();
			String label2 = row.getCell(1).getStringCellValue();
			if(label1.contains("birthCertificate") && label2.contains("birthCertificate")) {
				sourcePrefix = label1;
				targetPrefix = label2;
				if(!Objects.isNull(alignment)) {
					return alignment;					
				}
				alignment = createNewAlignment(sourcePrefix + "-" + targetPrefix);
			} else if(!label1.isEmpty() && !label2.isEmpty()){
				alignment.add(new Correspondence("http://birtCertificate" + sourcePrefix + "#" + label1, 
						"http://birtCertificate" + targetPrefix + "#" + label2, 1));
				}
			}
		  return (alignment != null) ? alignment : new Alignment();
	}
	
	private Alignment createNewAlignment(String name) {
		Alignment alignment = new Alignment();
		alignment.setName(sourcePrefix + "-" + targetPrefix);
		return alignment;
	}
	
	public static void main(String[] args) {
		try {
			SinglePageGSReader reader = new SinglePageGSReader("src/main/resources/data/dataset2/New_GS_birth/goldstandard-pnml.xlsx", "test");
			for (int i = 0; i < 36; i++) {
				Alignment a = reader.nextAlignment();
				System.out.println(a);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
