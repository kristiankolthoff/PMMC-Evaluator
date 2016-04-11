package de.unima.ki.pmmc.evaluator;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.BPMNParser;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;


public class Evaluation {

	public static String[] modelIdsDS1 = new String[]{
			"Cologne",
			"Frankfurt",
			"FU_Berlin",
			"Hohenheim",
			"IIS_Erlangen",
			"Muenster",
			"Potsdam",
			"TU_Munich",
			"Wuerzburg"
	};
	
	public static String[] modelIdsDS2 = new String[]{
			"birthCertificate_p31",
			"birthCertificate_p32",
			"birthCertificate_p33",
			"birthCertificate_p34",
			"birthCertificate_p246",
			"birthCertificate_p247",
			"birthCertificate_p248",
			"birthCertificate_p249",
			"birthCertificate_p250"
	};
	
	public static void main(String[] args) throws AlignmentException, ParserConfigurationException, SAXException, IOException {
		

		
		String[] modelIds = modelIdsDS1;
		String datasetId = "dataset1";
		String gsExtension = ".rdf";
		String modelExtension = ".bpmn";
		Parser parser = new BPMNParser();
		
		
		/*
		String[] modelIds = modelIdsDS2;
		String datasetId = "dataset2";
		String gsExtension = "";
		String modelExtension = ".pnml";
		Parser parser = new PNMLParser();
		*/
		
		Alignment alignmentAll = new Alignment();
		Alignment referenceAll = new Alignment();
		
		
		
		
		for (int i = 0; i < modelIds.length - 1; i++) {
			String sourceId = modelIds[i];
			Model sourceModel = parser.parse("src/main/resources/data/" + datasetId + "/models/" + sourceId  + modelExtension);
			for (int j = i+1; j < modelIds.length; j++) {	
				String targetId = modelIds[j];
				Model targetModel = parser.parse("src/main/resources/data/" + datasetId + "/models/" + targetId  + modelExtension);
				String mappingId = sourceId + "-" + targetId;
				Alignment alignment = new Alignment("src/main/resources/data/results/basicmatcher1/" + datasetId + "/" +  mappingId + ".rdf");
				Alignment reference = new Alignment("src/main/resources/data/" + datasetId + "/goldstandard/" +  mappingId + gsExtension);
				
				
				Alignment notFound = reference.minus(alignment);
				Alignment notFoundL = new Alignment(notFound, sourceModel, targetModel);
				
				
				referenceAll.add(reference);
				alignmentAll.add(alignment);
				Characteristic c = new Characteristic(alignment, reference);
				System.out.println(mappingId);
				System.out.println("not found");
				System.out.println(notFoundL);
				System.out.println(c.toShortDesc());
			}
		}
		System.out.println("-----------------");
		Characteristic cAll = new Characteristic(alignmentAll, referenceAll);
		System.out.println(cAll.toShortDesc());
		System.out.println("overall " + alignmentAll.size() + " correspondences generated");

	}

}
