// *****************************************************************************
//
// Copyright (c) 2011 Christian Meilicke (University of Mannheim)
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge,
// publish, distribute, sublicense, and/or sell copies of the Software,
// and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
// IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// *********************************************************************************

package de.unima.ki.pmmc.evaluator.alignment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;



/**
* A MappingWriterXml writes Mappings to xml-files. 
*
*/
public class AlignmentWriterXml implements AlignmentWriter {
	
	public AlignmentWriterXml() { }
	
	public void writeAlignment(String filepath, Alignment mapping) throws AlignmentException {
		File file = new File(filepath);
		try {
			file.createNewFile();
			BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
			fw.write(getXMLString(mapping));
			fw.flush();
			fw.close();
		}
		catch (IOException e) {
			throw new AlignmentException(AlignmentException.IO_ERROR, "could not create/access file " + file);
		}
		
	}
	
	private String getXMLString(Alignment mapping) {
		StringBuffer mappingXML = new StringBuffer();
		List<Correspondence> correspondences = mapping.getCorrespondences();
		mappingXML.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		// mappingXML.append("<!DOCTYPE rdf:RDF SYSTEM \"align.dtd\">\n");
		mappingXML.append("<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\" ");
		mappingXML.append("\n\t xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" ");
		mappingXML.append("\n\t xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n\n");		
		mappingXML.append("<Alignment>\n");
		mappingXML.append("<xml>yes</xml>\n");
		mappingXML.append("<level>0</level>\n");
		mappingXML.append("<type>??</type>\n\n");
		for (Correspondence c : correspondences) { mappingXML.append(getMappedCell(c)); }
		mappingXML.append("\n</Alignment>\n");
		mappingXML.append("</rdf:RDF>\n");
		return mappingXML.toString();
	}
	
	private StringBuffer getMappedCell(Correspondence correspondence) {
		StringBuffer mappedCell = new StringBuffer();
		mappedCell.append("<map>\n");
		mappedCell.append("\t<Cell>\n");	
		mappedCell.append("\t\t<entity1 rdf:resource=\"" + correspondence.getUri1() + "\"/>\n");
		mappedCell.append("\t\t<entity2 rdf:resource=\"" + correspondence.getUri2() + "\"/>\n");
		mappedCell.append("\t\t<measure rdf:datatype=\"xsd:float\">" + correspondence.getConfidence() + "</measure>\n");
        mappedCell.append("\t\t<relation>" + mask(correspondence.getRelationSymbol()) + "</relation>\n");
		mappedCell.append("\t</Cell>\n");
		mappedCell.append("</map>\n");
		return mappedCell;
	}
	
	
	private String mask(String r) {
		if (r.equals("<")) { return "&lt;"; }
		else if (r.equals(">")) { return "&gt;"; }
		else { return r; }
	}

}

