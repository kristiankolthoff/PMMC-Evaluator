package de.unima.ki.pmmc.evaluator.alignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;



/**
* A Alignment reader for txt format reads Alignments from txt-files. 
*
*/
public class AlignmentReaderTxt implements AlignmentReader {
	
	public AlignmentReaderTxt() {	}
	
	public Alignment getAlignment(String filepath) throws AlignmentException {
		File file = new File(filepath);
		Alignment alignment = new Alignment();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			ArrayList<Correspondence> correspondences = new ArrayList<Correspondence>();
			String[] parts;
			Correspondence correspondence;
			String line = "";
			double sim;
			try {
				
				while ((line = reader.readLine())!= null) {
					// System.out.println("---> " + line);
					if (line.length() == 0 || line.startsWith("#")) {
						// do nothing if line is empty or comment	
					}
					
					else if (line.contains("|") && (!line.startsWith("#"))) {
						// System.out.println("yyy" + line);
						parts = line.split(" .{1,2} ");
						sim = Double.parseDouble(parts[2]);
						SemanticRelation sr = null;
						if (line.contains(" = ")) {
							sr = SemanticRelation.EQUIV;
						}
						else if (line.contains(" < ")) {
							sr = SemanticRelation.SUB;
						}
						else if (line.contains(" > ")) {
							sr = SemanticRelation.SUPER;
						}
						else if (line.contains(" != ")) { 
							sr = SemanticRelation.DISJOINT;
						}
						else {
							sr = SemanticRelation.NA;
						}
						correspondence = new Correspondence(parts[0].trim(), parts[1].trim(), sr, sim);
						correspondences.add(correspondence);
					}
					else {
						reader.close();
						throw new AlignmentException(
								AlignmentException.INVALID_FORMAT,
								"format of " + file.toString() + " is invalid (see line '" + line + "')"
						);						
						
					}
				}
		
			}
			catch (NumberFormatException nfe) {
				throw new AlignmentException(
						AlignmentException.IO_ERROR,
						"caused by " + file.toString() + " where a number has been expected",
						nfe
				);
			}
			catch (IOException ioe) {
				throw new AlignmentException(
						AlignmentException.IO_ERROR,
						"file at " + file.toString() + " cannot be accessed",
						ioe
				);
			}
			catch (ArrayIndexOutOfBoundsException ae) {
				throw new AlignmentException(
						AlignmentException.IO_ERROR,
						"line " + line + " in file " + file.toString() + " has bad format (check missing # | or whitespace)",
						ae
				);
			}
			alignment.setCorrespondences(correspondences);	
		}
		catch (FileNotFoundException e) {
			throw new AlignmentException(
					AlignmentException.IO_ERROR,
					"file at " + file.toString() + " not found",
					e
			);
		}
		try {
			reader.close();
		} catch (IOException e) {
			throw new AlignmentException(
					AlignmentException.IO_ERROR,
					"reader for file at " + file.toString() + " could not be closed",
					e
			);
		}
		return alignment;
	}
	
	
}
