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

public class SinglePageAnnotationGSReader implements GSReader {

	private String path;
	private FileInputStream excelFile;
	private Workbook workbook;
	private Sheet sheet;
	private Iterator<Row> iterator;
	private String sourcePrefix, targetPrefix;
	private boolean readC;
	private String name;
	
	public SinglePageAnnotationGSReader(String path, String name, boolean readC) throws IOException {
		this.path = path;
		this.excelFile = new FileInputStream(new File(path));
		this.workbook = new XSSFWorkbook(excelFile);
		this.sheet = workbook.getSheetAt(0);
		this.iterator = sheet.iterator();
		this.sourcePrefix = null;
		this.targetPrefix = null;
		this.readC = readC;
		this.name = name;
	}
	
	@Override
	public Alignment nextAlignment() {
		Alignment alignment = null;
		if(sourcePrefix != null && targetPrefix != null) {
			alignment = createNewAlignment("birthCertificate_" + sourcePrefix + 
					"-" + "birthCertificate_" + targetPrefix);
		}
		while(iterator.hasNext()) {
			Row row = iterator.next();
			String label1 = row.getCell(0).getStringCellValue();
			String label2 = row.getCell(1).getStringCellValue();
			Cell cell = readC ? row.getCell(2) : row.getCell(3);
			if(contained(label1) && contained(label2)) {
				sourcePrefix = label1;
				targetPrefix = label2;
				if(!Objects.isNull(alignment)) {
					return alignment;					
				}
				alignment = createNewAlignment("birthCertificate_" + sourcePrefix + 
						"-" + "birthCertificate_" + targetPrefix);
			} else if(!label1.isEmpty() && !label2.isEmpty() && cell != null){
				alignment.add(new Correspondence("http://birthCertificate_" + sourcePrefix + "#" + label1, 
						"http://birthCertificate_" + targetPrefix + "#" + label2, 1));
				}
			}
		  return (alignment != null) ? alignment : new Alignment();
	}
	
	public static void main(String[] args) {
		try {
			SinglePageAnnotationGSReader reader = new SinglePageAnnotationGSReader("src/main/resources/data/dataset2/New_GS_birth/GoldStandard_CS_CKT.xlsx", "test", false);
			for (int i = 0; i < 36; i++) {
				Alignment a = reader.nextAlignment();
				System.out.println(a);
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
				{return test.equals(label);});
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
