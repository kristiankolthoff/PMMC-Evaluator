package de.unima.ki.pmmc.evaluator.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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

	private Parser parser;
	private List<String> annotators;
	private List<String> modelPaths;
	private List<Model> models;
	private String goldstandardPath;
	private int numNulls;
	private int numDouble;
	private String lineBreak;
	private String labelAnnotators;
	private String label1;
	private String label2;
	
	public GSPartitioner(Parser parser, String goldstandardPath) {
		this.parser = parser;
		this.goldstandardPath = goldstandardPath;
		this.modelPaths = new ArrayList<>();
		this.models = new ArrayList<>();
		this.annotators = new ArrayList<>();
		this.setLabelAnnotators("annotators");
		this.label1 = "label1";
		this.label2 = "label2";
	}
	
	private void loadModels() {
		for (int i = 0; i < modelPaths.size(); i++) {
			Model model;
			try {
				model = this.parser.parse(modelPaths.get(i));
				String[] prefix = modelPaths.get(i).split("/");
				model.setPrefix("http://" + prefix[prefix.length-1].replaceAll(".pnml", "") + "#");
				model.setId(modelPaths.get(i));
				models.add(model);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
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
	
	public static void main(String[] args) throws ParserConfigurationException, 
					IOException, SAXException, AlignmentException {
		GSPartitioner partitioner = new GSPartitioner(new BPMNParser(), 
				"src/main/resources/data/dataset1/GoldStandard_8experts_merged_.csv");
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
	
	public List<Alignment> getGoldstandard(List<Model> models, List<String> annotators) throws FileNotFoundException, 
			IOException, AlignmentException {
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
	
	public Solution getGoldstandardAsResult(List<Model> models, List<String> annotators) throws 
			FileNotFoundException, IOException, AlignmentException {
		final Reader in = new FileReader(this.goldstandardPath);
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
			String label1 = record.get(0); //record.get(this.label1)
			String label2 = record.get(1); //record.get(this.label2)
			String anns = record.get(3); //record.get(this.labelAnnotators)
			if(label1.contains(this.lineBreak) && label2.contains(this.lineBreak)) {
				if(!Objects.isNull(alignment)) {
					alignment.setSourceModel(sourceModel);
					alignment.setTargetModel(targetModel);
					String[] sourceIds = sourceModel.getId().split("/");
					String[] targetIds = targetModel.getId().split("/");
					alignment.setName(sourceIds[sourceIds.length-1].replace(".pnml", "") + "-" 
								+ targetIds[targetIds.length-1].replace(".pnml", "")  + ".rdf");
					alignments.add(alignment);				
				}
				for(Model m : models) {
					if(m.getId().contains(label1)) {
						sourceModel = m;
					} else if(m.getId().contains(label2)) {
						targetModel = m;
					}
				}
				System.out.println("------------"+sourceModel.getId()+ "-" + targetModel.getId() +"------------");
				alignment = new Alignment();
				cache = new ArrayList<>();
			} else if(!label1.isEmpty() && !label2.isEmpty()){
				String sourceID = (label1.contains("sid")) ? label1 : this.getLabelId(sourceModel, label1);
				String targetID = (label2.contains("sid")) ? label2 : this.getLabelId(targetModel, label2);
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
		String[] sourceIds = sourceModel.getId().split("/");
		String[] targetIds = targetModel.getId().split("/");
		alignment.setName(sourceIds[sourceIds.length-1].replace(".pnml", "") + "-" 
					+ targetIds[targetIds.length-1].replace(".pnml", "")  + ".rdf");
		alignments.add(alignment);
		System.out.println("Number of NULL occurrences : " + this.numNulls + "/");
		StringBuilder sb = new StringBuilder();
		for(String s : annotators) {
			sb.append(s);
			sb.append(" ");
		}
		return new Solution("goldstandard-" + sb.toString(), "src/main/resources/data/dataset1/GoldStandard_8experts_merged_.csv", 0.0, alignments);
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
	
	public void loadModelPaths(String... modelPaths) {
		this.modelPaths = Arrays.asList(modelPaths);
		this.loadModels();
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public String getGoldstandardPath() {
		return goldstandardPath;
	}

	public void setGoldstandardPath(String goldstandardPath) {
		this.goldstandardPath = goldstandardPath;
	}

	public int getNumDouble() {
		return numDouble;
	}

	public void setNumDouble(int numDouble) {
		this.numDouble = numDouble;
	}

	public String getLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(String lineBreak) {
		this.lineBreak = lineBreak;
	}

	public String getLabel1() {
		return label1;
	}

	public void setLabel1(String label1) {
		this.label1 = label1;
	}

	public String getLabel2() {
		return label2;
	}

	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	public String getLabelAnnotators() {
		return labelAnnotators;
	}

	public void setLabelAnnotators(String labelAnnotators) {
		this.labelAnnotators = labelAnnotators;
	}
	
	
}
