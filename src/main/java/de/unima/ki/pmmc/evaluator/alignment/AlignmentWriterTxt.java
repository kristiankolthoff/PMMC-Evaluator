package de.unima.ki.pmmc.evaluator.alignment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;


/**
* A MappingWriterTxt writes Mappings to txt-files. 
*
*/
public class AlignmentWriterTxt  implements AlignmentWriter {
	
	public AlignmentWriterTxt() { }
	
	public void writeAlignment(String filepath, Alignment alignment) throws AlignmentException {
		File file = new File(filepath);
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(alignment.toString());
			fw.flush();
			fw.close();
		}
		catch (IOException e) {
			throw new AlignmentException(AlignmentException.IO_ERROR, "could not create/access file " + file);
		}
		
	}
	
	
}