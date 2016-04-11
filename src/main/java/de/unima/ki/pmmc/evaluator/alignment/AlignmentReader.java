package de.unima.ki.pmmc.evaluator.alignment;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;


/**
* The MappingReader defines the interface for its implementing classes that can be used to read
* Mappings from xml as well as txt-files. 
*
*/

public interface AlignmentReader {
	
	public Alignment getAlignment(String filepath) throws AlignmentException;
		
}