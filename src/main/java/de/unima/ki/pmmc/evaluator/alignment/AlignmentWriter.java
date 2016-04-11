package de.unima.ki.pmmc.evaluator.alignment;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;



/**
* The MappingReader defines the interface for its implementing classes that can be used to write
* Mappings to xml as well as txt-files. 
*
*/
public interface AlignmentWriter {
	
	public void writeAlignment(String filepath, Alignment alignment) throws AlignmentException;
	
	
		
}