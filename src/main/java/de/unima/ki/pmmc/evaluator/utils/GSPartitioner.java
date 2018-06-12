package de.unima.ki.pmmc.evaluator.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.BPMNParser;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;

public class GSPartitioner {

	private List<String> annotators;
	private List<Model> models;
	private int numNulls;
	private int numDouble;
	
	public GSPartitioner() {
		//Hack improve models
		String[] modelIds = new String[]{
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
		Parser parser = new BPMNParser();
		this.models = new ArrayList<>();
		for (int i = 0; i < modelIds.length; i++) {
			Model model;
			try {
				model = parser.parse("src/main/resources/data/dataset1/models/" + modelIds[i] + ".bpmn");
				model.setPrefix("http://" + modelIds[i] + "#");
				model.setId(modelIds[i]);
				models.add(model);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
		this.annotators = new ArrayList<>();
		init();
	}
	
	
	/**
	 * Computes a list of all k combinations of elements 
	 * specified in the assigned list
	 * @param elements the elements to compute the combinations from
	 * @param k the number of combinations
	 * @return list containing all combinations as a list
	 */
	private <T> List<List<T>> combineK(List<T> elements, int k){
		List<List<T>> combinations = new ArrayList<>();
		int numOfElems = elements.size();
		if(k > numOfElems){
			return Collections.emptyList();
		}
		int combination[] = new int[k];
		int r = 0;		
		int index = 0;
		while(r >= 0){
			if(index <= (numOfElems + (r - k))){
					combination[r] = index;
				if(r == k-1){
					List<T> vals = new ArrayList<>();
					for (int i = 0; i < combination.length; i++) {
						vals.add(elements.get(combination[i]));
					}
					combinations.add(vals);
					index++;				
				}
				else{
					index = combination[r]+1;
					r++;										
				}
			}
			else{
				r--;
				if(r > 0)
					index = combination[r]+1;
				else
					index = combination[0]+1;	
			}			
		}
		return combinations;
	}
	
	private void init() {
		this.annotators.add("HIWI1");
		this.annotators.add("Heiner");
		this.annotators.add("Henrik");
		this.annotators.add("Viktor");
		this.annotators.add("Han");
		this.annotators.add("Elena");
		this.annotators.add("HIWI2");
		this.annotators.add("HIWI3");
	}
	
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, AlignmentException {
		GSPartitioner partitioner = new GSPartitioner();
		System.out.println("Started");
		List<Solution> vals = partitioner.getKGoldstandardsAsResult(1);
		AlignmentReader reader1 = new AlignmentReaderXml();
		Alignment notFound = new Alignment();
		for(Alignment a : vals.get(0).getAlignments()) {
			Alignment old = reader1.getAlignment("src/main/resources/data/dataset1/"
					+ "goldstandard_experts_heiner/n8/" + a.getName());
			for(Correspondence c : a) {
				boolean found = false;
				for(Correspondence cNew : old) {
					if(c.equals(cNew)) {
						found = true;
						break;
					}
				}
				if(!found) {
					notFound.add(c);
//					System.out.println("not found : " + c);
				}
			}
		}
		List<String> corres = Model.transformedAlignment(partitioner.getModels(), notFound);
		for (int i = 0; i < corres.size(); i++) {
			System.out.println(i + " " + notFound.get(i));
			System.out.println(corres.get(i));
		}
		
		notFound = new Alignment();
		System.out.println("!----------------------------------------");
		System.out.println("!----------------------------------------");
		System.out.println("!----------------------------------------");
		System.out.println("!----------------------------------------");
		System.out.println("!----------------------------------------");
		for(Alignment a : vals.get(0).getAlignments()) {
			Alignment old = reader1.getAlignment("src/main/resources/data/dataset1/"
					+ "goldstandard_experts_heiner/n8/" + a.getName());
			for(Correspondence c : old) {
				boolean found = false;
				for(Correspondence cNew : a) {
					if(c.equals(cNew)) {
						found = true;
						break;
					}
				}
				if(!found) {
					notFound.add(c);
//					System.out.println("not found : " + c);
				}
			}
			}
		List<String> corres2 = Model.transformedAlignment(partitioner.getModels(), notFound);
		for (int i = 0; i < corres2.size(); i++) {
			System.out.println(i + " " + notFound.get(i));
			System.out.println(corres.get(i));
		}
	}
	
	public List<List<Alignment>> getKGoldstandards(int k) {
		List<List<Alignment>> goldstandards = new ArrayList<>();
		List<List<String>> annotatorCombs = combineK(annotators, k);
		for(List<String> currAnnos : annotatorCombs) {
			try {
				goldstandards.add(getGoldstandard(models, currAnnos));
			} catch (AlignmentException | IOException e) {
				e.printStackTrace();
			}
		}
		return goldstandards;
	}
	
	
	public List<Solution> getKGoldstandardsAsResult(int k) {
		List<Solution> goldstandards = new ArrayList<>();
		List<List<String>> annotatorCombs = combineK(annotators, k);
		for(List<String> currAnnos : annotatorCombs) {
			try {
				goldstandards.add(getGoldstandardAsResult(models, currAnnos));
			} catch (AlignmentException | IOException e) {
				e.printStackTrace();
			}
		}
		return goldstandards;
	}
	
	public List<Alignment> getGoldstandard(List<Model> models, List<String> annotators) throws FileNotFoundException, IOException, AlignmentException {
		final Reader in = new FileReader("src/main/resources/data/dataset1/GoldStandard_8experts_merged_.csv");
		final Iterable<CSVRecord> records = CSVFormat.EXCEL.
				withDelimiter(';').
				withHeader().
				parse(in);
		Model sourceModel = null;
		Model targetModel = null;
		List<Alignment> alignments = new ArrayList<>();
		Alignment alignment = null;
		this.numNulls = 0;
		this.numDouble = 0;
		List<String> cache = new ArrayList<>();
		for(CSVRecord record : records) {
			String label1 = record.get("label1");
			String label2 = record.get("label2");
			String anns = record.get("annotators");
			if(label1.contains("Admission") && label2.contains("Admission")) {
				if(!Objects.isNull(alignment)) {
					alignment.setSourceModel(sourceModel);
					alignment.setTargetModel(targetModel);
					alignments.add(alignment);				
				}
				for(Model m : models) {
					if(label1.replace(" ", "_").contains(m.getId())) {
						sourceModel = m;
					} else if(label2.replace(" ", "_").contains(m.getId())) {
						targetModel = m;
					}
				}
				System.out.println("------------"+sourceModel.getId()+ "-" + targetModel.getId() +"------------");
				alignment = new Alignment();
				cache = new ArrayList<>();
			} else if(!label1.isEmpty() && !label2.isEmpty()){
				String sourceID = (label1.contains("sid")) ? label1 : this.getLabelId(sourceModel, label1);
				String targetID = (label2.contains("sid")) ? label2 : this.getLabelId(targetModel, label2);
//				String targetID = this.getLabelId(targetModel, label2);
				if(cache.contains(sourceID+targetID)) {
					System.out.println("--------Doppelt-------");
					System.out.println(label1 + " - " + label2);
					System.out.println(sourceID + " - " + targetID);
					System.out.println("----------------------");
					numDouble++;
				} else {
					cache.add(sourceID + targetID);
				}
				if(!Objects.isNull(sourceID) && !Objects.isNull(targetID)) {
					int numOfAnns = 0;
					for(String annotator : annotators) {
						if(anns.contains(annotator)) numOfAnns++;
					}
					if(numOfAnns > 0) {
						alignment.add(new Correspondence(sourceModel.getPrefix() + sourceID, 
								targetModel.getPrefix() + targetID, numOfAnns / (double) annotators.size()));
					}
				}
			}
		}
		alignment.setSourceModel(sourceModel);
		alignment.setTargetModel(targetModel);
		alignments.add(alignment);
//		System.out.println("Number of Double occurrences : " + this.numDouble);
//		System.out.println("Number of NULL occurrences : " + this.numNulls + "/" + numLabelsTotal);
		return alignments;
	}
	
	public Solution getGoldstandardAsResult(List<Model> models, List<String> annotators) throws FileNotFoundException, IOException, AlignmentException {
		final Reader in = new FileReader("src/main/resources/data/dataset1/GoldStandard_8experts_merged_ updated_conf.csv");
		final Iterable<CSVRecord> records = CSVFormat.EXCEL.
				withDelimiter(';').
				withHeader().
				parse(in);
		Model sourceModel = null;
		Model targetModel = null;
		List<Alignment> alignments = new ArrayList<>();
		Alignment alignment = null;
		this.numNulls = 0;
		this.numDouble = 0;
		List<String> cache = new ArrayList<>();
		for(CSVRecord record : records) {
			String label1 = record.get("label1");
			String label2 = record.get("label2");
			String anns = record.get("annotators");
			if(label1.contains("Admission") && label2.contains("Admission")) {
				if(!Objects.isNull(alignment)) {
					alignment.setSourceModel(sourceModel);
					alignment.setTargetModel(targetModel);
					alignment.setName(sourceModel.getId() + "-" + targetModel.getId() + ".rdf");
					alignments.add(alignment);				
				}
				for(Model m : models) {
					if(label1.replace(" ", "_").contains(m.getId())) {
						sourceModel = m;
					} else if(label2.replace(" ", "_").contains(m.getId())) {
						targetModel = m;
					}
				}
				System.out.println("------------"+sourceModel.getId()+ "-" + targetModel.getId() +"------------");
				alignment = new Alignment();
				cache = new ArrayList<>();
			} else if(!label1.isEmpty() && !label2.isEmpty()){
				String sourceID = (label1.contains("sid")) ? label1 : this.getLabelId(sourceModel, label1);
				String targetID = (label2.contains("sid")) ? label2 : this.getLabelId(targetModel, label2);
//				String targetID = this.getLabelId(targetModel, label2);
				if(cache.contains(sourceID+targetID)) {
					System.out.println("--------Doppelt-------");
					System.out.println(label1 + " - " + label2);
					System.out.println(sourceID + " - " + targetID);
					System.out.println("----------------------");
					numDouble++;
				} else {
					cache.add(sourceID + targetID);
				}
				if(!Objects.isNull(sourceID) && !Objects.isNull(targetID)) {
					int numOfAnns = 0;
					for(String annotator : annotators) {
						if(anns.contains(annotator)) numOfAnns++;
					}
					if(numOfAnns > 0) {
						alignment.add(new Correspondence(sourceModel.getPrefix() + sourceID, 
								targetModel.getPrefix() + targetID, numOfAnns / (double) annotators.size()));
					}
				}
			}
		}
		alignment.setSourceModel(sourceModel);
		alignment.setTargetModel(targetModel);
		alignment.setName(sourceModel.getId() + "-" + targetModel.getId() + ".rdf");
		alignments.add(alignment);
//		System.out.println("Number of Double occurrences : " + this.numDouble);
		System.out.println("Number of NULL occurrences : " + this.numNulls + "/");
		StringBuilder sb = new StringBuilder();
		for(String s : annotators) {
			sb.append(s);
			sb.append(" ");
		}
		return new Solution("goldstandard-" + sb.toString(), "src/main/resources/data/dataset1/GoldStandard_8experts_merged_.csv", 0.0, alignments);
	}
	
	//TODO: make annotators a class
	public void transformLabelsToIDsToFile(List<Model> models, List<String> annotators, final String OUTPUT_PATH) {
		
	}
	
	public String getLabelId(Model m, String label) {
		label = getSanitizeLabel(label);
		for(Activity a : m.getActivities()) {
			String sanLabel = getSanitizeLabel(a.getLabel());
			if(sanLabel.equals(label)) {
				return a.getId();
			}
		}
		this.numNulls++;
		System.out.println(m.getId() + "  --  " + label);
		return null;
	}
	
	public static String getSanitizeLabel(String label) {
		label = label.trim();
		label = label.toLowerCase();
		label = label.replace("\n", "");
		label = label.replace("§", "");
		return label;
	}

	public List<String> getAnnotators() {
		return annotators;
	}

	public void setAnnotators(List<String> annotators) {
		this.annotators = annotators;
	}
	
	public void setAnnotators(String... annotators) {
		this.annotators = Arrays.asList(annotators);
	}


	public List<Model> getModels() {
		return models;
	}


	public void setModels(List<Model> models) {
		this.models = models;
	}
	
	
}
