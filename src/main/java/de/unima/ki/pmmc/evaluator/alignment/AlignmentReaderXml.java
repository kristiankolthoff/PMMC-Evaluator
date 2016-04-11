
package de.unima.ki.pmmc.evaluator.alignment;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
* A mapping reader for rdf/xml format reads mappings from rdf-files. The format supported is the
* one described in the Alignment API. But notice that not all kinds of specifications are supported.
* More precise it should work with any level 0 mapping, where the relations =, < and > are used
* to state correspodences between concepts or properties.
*/
public class AlignmentReaderXml extends DefaultHandler implements AlignmentReader {
    
	
	private String entity1;
	private String entity2;
	
	private String relation;
	private boolean readRelation;
	private String confidence;
	private boolean readConfidence;
	private String type;
	private boolean readType;
	private ArrayList<Correspondence> correspondences;
	private AlignmentException internalException = null;
	private String filepathOrUri;
	
	
	public AlignmentReaderXml() {
		this.confidence = "";
		this.type = "";
	}

	public Alignment getAlignment(String filepathOrUri) throws AlignmentException {
		this.readRelation = false;
		this.readConfidence = false;
		this.correspondences = new ArrayList<Correspondence>();   
		
		Alignment mapping = new Alignment();
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        
        URI uri =  null;
        File file = null;
        this.filepathOrUri = filepathOrUri;
       	try { uri = new URI(this.filepathOrUri); }
       	catch (URISyntaxException e1) { file = new File(this.filepathOrUri); }
     
       	
		try {
			saxParser = factory.newSAXParser();
			if (uri != null) {
				saxParser.parse(uri.toString(), this);
			}
			else {
				saxParser.parse(file, this);
			}
		}
		catch (ParserConfigurationException e) {
			throw new AlignmentException(
				AlignmentException.IO_ERROR,
				"caused by parsing " + filepathOrUri.toString() + " (ParserConfigurationException)",
				e
			);
		}
		catch (SAXException e) {
			// System.out.println(e.getMessage());
			throw new AlignmentException(
				AlignmentException.IO_ERROR,
				"caused by parsing " + filepathOrUri.toString() + " (SAXException)",
				e
			);
		}
		catch (IOException e) {
			throw new AlignmentException(
				AlignmentException.IO_ERROR,
				"caused by parsing " + filepathOrUri.toString(),
				e
			);
		}
		if (this.internalException != null) { throw internalException; }
		mapping.setCorrespondences(correspondences);
		return mapping;
	}

    public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
    	if (qName.toLowerCase().equals("entity1")) {
    		this.entity1 = attrs.getValue("rdf:resource");
    	}
    	if (qName.toLowerCase().equals("entity2")) {
    		this.entity2 = attrs.getValue("rdf:resource");
    	}
    	if (qName.toLowerCase().equals("relation")) {
    		this.readRelation = true;
    	}
    	if (qName.toLowerCase().equals("measure")) {
    		this.readConfidence = true;
    	}
    	if(qName.toLowerCase().equals("type")) {
    		this.readType = true; 
    	}
    }
    
    public void characters(char buf[], int offset, int len) throws SAXException {
        if (this.readRelation) {
        	this.relation = new String(buf, offset, len);	
        }
        if (this.readConfidence) {
        	this.confidence += new String(buf, offset, len);
        }
        if(this.readType) {
        	this.type = new String(buf, offset, len);
        }
        
    }    

    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
    	if (qName.toLowerCase().equals("cell")) {
    		float sim = (float)Double.parseDouble(this.confidence);
    		if (sim < 0.0 || sim > 1.0) {
    			BigDecimal d = new BigDecimal(this.confidence);
    			sim = d.floatValue();
    		}
    		this.confidence = "";
    		Correspondence correspondence = null;
    		
    		SemanticRelation relationType = SemanticRelation.NA;
    		if (this.relation == null) { relationType = SemanticRelation.EQUIV; }
    		else {
	    		if (this.relation.equals("=")) { relationType = SemanticRelation.EQUIV; }
	    		if (this.relation.equals("\"=\"")) { relationType = SemanticRelation.EQUIV; }
	    		if (this.relation.startsWith("fr.inrialpes.exmo.align.impl.rel.EquivRelation")) { relationType = SemanticRelation.EQUIV; }
	    		if (this.relation.equals("<")) { relationType = SemanticRelation.SUB; }
	    		if (this.relation.equals(">")) { relationType = SemanticRelation.SUPER; }
    		}
    		SemanticRelation semanticRelation;
			semanticRelation = relationType;
			/**
			 * Hack that should be improved
			 * TODO: This is a bug, creates type if type == ** or == ??, there should not be a type here
			 */
			if(this.type.isEmpty() || this.type.equals("**") || this.type.equals("??")) {
				correspondence = new Correspondence(this.entity1 , this.entity2, semanticRelation, (double)sim);
			} else {
				try {
					correspondence = new Correspondence(this.entity1, this.entity2, semanticRelation, (double)sim, CorrespondenceType.getValue(this.type));
				} catch (CorrespondenceException e) {
					e.printStackTrace();
				}
			}
			this.type = "";
			correspondences.add(correspondence);

			
    	}
    	this.readConfidence = false;
    	this.readRelation = false;
    	this.readType = false;
    }
}
