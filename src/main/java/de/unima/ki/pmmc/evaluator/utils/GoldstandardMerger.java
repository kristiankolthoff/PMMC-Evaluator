package de.unima.ki.pmmc.evaluator.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentWriter;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentWriterTxt;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentWriterXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.PNMLParser;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.utils.reader.GSReader;
import de.unima.ki.pmmc.evaluator.utils.reader.MultipleSheetAcrossGSReader;
import de.unima.ki.pmmc.evaluator.utils.reader.MultipleSheetMultipleColSpanGSReader;
import de.unima.ki.pmmc.evaluator.utils.reader.SinglePageAnnotationGSReader;
import de.unima.ki.pmmc.evaluator.utils.reader.SinglePageGSReader;
import javafx.util.Pair;

public class GoldstandardMerger {

	private List<String> annotators;
	private int numNulls;
	private int numDouble;
	
	private static final String ROOT = "src/main/resources/data/dataset2/New_GS_birth/";
	private static final String OUTPUT = "src/main/resources/data/dataset2/new_gs_rdf/";
	private static final int NUM_OF_ANNOTATORS = 8;
	
	private List<GSReader> readers;
	
	public GoldstandardMerger() {
		this.annotators = new ArrayList<>();
		this.readers = new ArrayList<>();
		init();
	}
	
	private void init() {
		try {
			readers.add(new SinglePageGSReader(ROOT + "goldstandard-pnml.xlsx", "HIWI1"));
			readers.add(new SinglePageAnnotationGSReader(ROOT + "GoldStandard_CS_CKT.xlsx", "Clemens", true));
			readers.add(new SinglePageAnnotationGSReader(ROOT + "GoldStandard_CS_CKT.xlsx", "Christoph", false));
			readers.add(new MultipleSheetAcrossGSReader(ROOT + "GS1_BR.xlsx", "GS1"));
			readers.add(new MultipleSheetAcrossGSReader(ROOT + "GS2_BR.xlsx", "GS2"));
			readers.add(new MultipleSheetAcrossGSReader(ROOT + "GS3_BR.xlsx", "GS3"));
			readers.add(new MultipleSheetAcrossGSReader(ROOT + "GS4_BR.xlsx", "GS4"));
			readers.add(new MultipleSheetMultipleColSpanGSReader(ROOT + "Process_mapping.xlsx", "Proc-mapp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, AlignmentException {
		String[] modelIds = new String[]{
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
		Parser parser = new PNMLParser();
		List<Model> models = new ArrayList<>();
		for (int i = 0; i < modelIds.length; i++) {
			Model model = parser.parse("src/main/resources/data/dataset2/models/" + modelIds[i] + ".pnml");
			model.setPrefix("http://" + modelIds[i] + "#");
			model.setId(modelIds[i]);
			models.add(model);
		}
		GoldstandardMerger merger = new GoldstandardMerger();
		merger.merge(models);
	}
	
	public void merge(List<Model> models) {
		int count = 1;
		List<Alignment> summaryAlignments = new ArrayList<>();
		for (int i = 0; i < models.size(); i++) {
			Model sourceModel = models.get(i);
			for (int j = i+1; j < models.size(); j++) {
				List<Pair<String, Alignment>> alignments = new ArrayList<>();
				Model targetModel = models.get(j);
//				System.out.println(count++ + " " + sourceModel.getId() + "-" + targetModel.getId());
				for(GSReader reader : readers) {
					Alignment alignment = reader.nextAlignment();
					if(verifyAlignment(alignment, sourceModel, targetModel)) {
						alignments.add(new Pair<String, Alignment>(reader.getName(), alignment));
					}
				}
				//All annotator alignments read, create summary alignment
				for(Pair<String, Alignment> pair : alignments) {
					for(Correspondence c : pair.getValue()) {
						String label1 = c.getUri1().split("#")[1];
						if(label1.length() > 3) {
							String id = getLabelId(sourceModel, label1);
							if(id == null) System.out.println("[" + pair.getKey() + "]" + " null @ : " + sourceModel.getId() + " - " + label1);
							c.setUri1("http://" + sourceModel.getId() + "#" + id);
						}
						String label2 = c.getUri2().split("#")[1];
						if(label2.length() > 3) {
							String id = getLabelId(targetModel, label2);
							if(id == null) System.out.println("[" + pair.getKey() + "]" + " null @ : " + targetModel.getId() + " - " + label2);
							c.setUri2("http://" + targetModel.getId() + "#" + id);
						}
					}
				}
				writeAlignmentToCSV(OUTPUT + sourceModel.getId() +
						"-" + targetModel.getId() + ".rdf",createSummaryAlignment(alignments));
//				Alignment a = createSummaryAlignment(alignments);
//				summaryAlignments.add(a);
//				writeAlignmentWithAnnotationToCSV(OUTPUT + sourceModel.getId() +
//						"-" + targetModel.getId(), sourceModel, targetModel , a);
			}
		}
//		writeAlignmentsWithAnnotationToCSV(OUTPUT + "birth-goldstandard", summaryAlignments, models);
	}
	
	
	private void writeAlignmentToCSV(String path, Alignment alignment) {
		AlignmentWriter writer = new AlignmentWriterXml();
		try {
			writer.writeAlignment(path, alignment);
		} catch (AlignmentException e) {
			e.printStackTrace();
		}
	}
	
	private void writeAlignmentsWithAnnotationToCSV(String path, List<Alignment> alignments, List<Model> models) {
		try {
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(path + ".csv"));
			int count = 0;
			for (int i = 0; i < models.size(); i++) {
				Model sourceModel = models.get(i);
				for (int j = i+1; j < models.size(); j++) {
					Model targetModel = models.get(j);
					Alignment a = alignments.get(count++);
					bw.append(sourceModel.getId() + ";" + targetModel.getId() + "; ; ");
					bw.newLine();
					System.out.println(count + " " + sourceModel.getId() + "-" + targetModel.getId());
					for(Correspondence c : a) {
						String label1 = Model.getLabelFromId(sourceModel, c.getUri1().split("#")[1]);
						String label2 = Model.getLabelFromId(targetModel, c.getUri2().split("#")[1]);
						if(label1 == null || label2 == null) {
							System.out.println(label1 + "-" + label2);
						}
						label1 = (label1 == null) ? c.getUri1().split("#")[1] : label1.replaceAll(";", ",");
						label2 = (label2 == null) ? c.getUri2().split("#")[1] : label2.replaceAll(";", ",");
						bw.append(label1 + ";" + label2 + ";" + c.getConfidence() + ";");
						for(String s : c.getAnnotators().get()) {
							bw.append(s + " ");
						}
						bw.newLine();
					}
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeAlignmentWithAnnotationToCSV(String path, Model sourceModel, Model targetModel, Alignment alignment) {
		try {
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(path + ".csv"));
			for(Correspondence c : alignment) {
				String label1 = Model.getLabelFromId(sourceModel, c.getUri1().split("#")[1]);
				String label2 = Model.getLabelFromId(targetModel, c.getUri2().split("#")[1]);
				bw.append(label1 + ";" + label2 + ";" + c.getConfidence() + ";");
				for(String s : c.getAnnotators().get()) {
					bw.append(s + " ");
				}
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Alignment createSummaryAlignment(List<Pair<String, Alignment>> alignnments) {
		Alignment joinAlignment = new Alignment();
		Alignment finalAlignment = new Alignment();
		for(Pair<String, Alignment> pair : alignnments) {
			joinAlignment.join(pair.getValue());
		}
		for(Correspondence c : joinAlignment) {
			List<String> annotators = new ArrayList<>();
			for(Pair<String, Alignment> pair : alignnments) {
				for(Correspondence pairC : pair.getValue()) {
					//TODO Test for double correspondence in one alignment
					if(c.equals(pairC)) {
						annotators.add(pair.getKey());
					}
				}
			}
			if(!annotators.isEmpty()) {
				Correspondence cNew = new Correspondence(c.getUri1(), 
						c.getUri2(), (annotators.size()/ (double) NUM_OF_ANNOTATORS));
				cNew.setAnnotators(Optional.of(annotators));
				finalAlignment.add(cNew);
			}
		}
		return finalAlignment;
	}
	
	private boolean verifyAlignment(Alignment alignment, Model sourceModel, Model targetModel) {
		return true;
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
	
}
