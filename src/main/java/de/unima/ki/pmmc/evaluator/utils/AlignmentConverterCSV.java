package de.unima.ki.pmmc.evaluator.utils;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.model.Model;

public class AlignmentConverterCSV {

	private AlignmentReader reader;
	private List<Alignment> currAlignments;
	private String currMatcherName;
	private String currMatcherPath;
	private Solution goldstandard;
	
	public AlignmentConverterCSV() {
		this.reader = new AlignmentReaderXml();
		this.currAlignments = new ArrayList<>();
	}
	
	
	public static void main(String[] args) {
		AlignmentConverterCSV converter = new AlignmentConverterCSV();
		final String MATCHER_PATH = "src/main/resources/data/results/OAEI16/";
		final String GS_PATH = "src/main/resources/data/dataset2/new_gs_rdf/";
		final String OUTPUT = "src/main/resources/data/results/OAEI17CSV-br/";
		try {
			converter.convert(GS_PATH, MATCHER_PATH, OUTPUT);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CorrespondenceException e) {
			e.printStackTrace();
		}
	}
	
	private String findName(Path filePath, String root) {
		List<String> dirs = new ArrayList<>();
		while(filePath.getParent() != null && 
				!filePath.getFileName().toString()
				.equals(Paths.get(root).getFileName().toString())) {
			if(Files.isDirectory(filePath)) {
				dirs.add(filePath.getFileName().toString());
			}
			filePath = filePath.getParent();
		}
		String name = "";
		for (int i = dirs.size()-1; i >= 0; i--) {
			name += dirs.get(i);
			if(i != 0) {
				name += "-";
			}
		}
		return name;
	}
	
	private boolean isFileInGS(String filename) {
		for(Alignment a : goldstandard) {
			if(a.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	private List<Solution> searchForResults(Optional<String> path, double threshold) throws IOException {
		List<Solution> results = new ArrayList<>();
		if(path.isPresent()) {
			Files.walk(Paths.get(path.get())).forEach(filePath -> {
				if(isFileInGS(filePath.getFileName().toString())) {
					try {
						Alignment a = reader.getAlignment(filePath.toString());
						a.applyThreshold(threshold);
						a.setName(filePath.getFileName().toString());
						//TODO what if list empty or only contains one model
						currAlignments.add(a);
						currMatcherName = findName(filePath, path.get());
						currMatcherPath = filePath.getParent().toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(!currAlignments.isEmpty()){
					//TODO make check with models to verify explicitly
					//Verify if currently read matcher alignment has the same size as goldstandard
//					if(goldstandard.size() != currAlignments.size()) {
//						throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
//								+ currMatcherName + " GS: " + goldstandard.size() + " Matcher: " + currAlignments.size());
//					}
					results.add(new Solution(currMatcherName, currMatcherPath, threshold, currAlignments));
					currAlignments = new ArrayList<>();
				}
			});
		}
		return results;
	}
	
	public Solution load(String name, String path, double threshold) throws IOException {
		List<Alignment> alignments = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
				if(Files.isRegularFile(filePath)) {
					try {
						Alignment a = reader.getAlignment(filePath.toString());
						a.applyThreshold(threshold);
						a.setName(filePath.getFileName().toString());
						alignments.add(a);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
		return new Solution(name, path, threshold, alignments);
	}


	private void convert(String gspath, String matcherpath, String output) throws IOException, CorrespondenceException {
		goldstandard = load("goldstandard", gspath, 0.0);
		List<Solution> gsList = new ArrayList<>();
		gsList.add(goldstandard);
		List<Solution> matcherResults = searchForResults(Optional.of(matcherpath), 0.0);
		for(Solution result : matcherResults) {
			List<Characteristic> characteristics = computeCharacteristics(gsList, result);
			writeAlignmentToCSV(characteristics, result.getAlignments(), goldstandard.getAlignments(), output + result.getName());
		}
	}
	
	private List<Characteristic> computeCharacteristics(List<Solution> goldstandards, Solution matcher) throws CorrespondenceException {
		List<Characteristic> characteristics = new ArrayList<>();
		for(Solution gs : goldstandards) {
			for (int i = 0; i < gs.getAlignments().size(); i++) {
				Alignment alignGS = gs.getAlignments().get(i);
				Alignment alignMatcher = matcher.getAlignments().get(i);
				characteristics.add(new Characteristic(alignMatcher, alignGS));
			}
		}
		return characteristics;
	}


	private void writeAlignmentToCSV(List<Characteristic> chars, List<Alignment> mapping, List<Alignment> gs, String output) {
		final String DELIMITER = ",";
		try {
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(output + ".csv"));
			writer.append("ID-label1" + DELIMITER + "ID-label2" + DELIMITER + "GS conf" + DELIMITER + "conf");
			writer.newLine();
			for(int i = 0; i < chars.size(); i++) {
				Characteristic c = chars.get(i);
				Alignment tp = c.getTP();
				Alignment fp = c.getFP();
				Alignment fn = c.getFN();
				Alignment join = Alignment.join(Alignment.join(tp, fp), fn);
				for(Correspondence corres : join) {
					Correspondence cMatcher = findCorrespondence(mapping, corres);
					Correspondence cgs = findCorrespondence(gs, corres);
					System.out.println("Matcher " + cMatcher);
					System.out.println("GS" + cgs);
					double gsConf = (cgs != null) ? cgs.getConfidence() : 0.0;
					double matcherConf = (cMatcher != null) ? cMatcher.getConfidence() : 0.0;
					writer.append(corres.getUri1() + DELIMITER + corres.getUri2() + DELIMITER + 
							gsConf + DELIMITER + matcherConf);
					writer.newLine();
				};
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Correspondence findCorrespondence(List<Alignment> alignments, Correspondence c) {
		for(Alignment a : alignments) {
			for(Correspondence corres : a) {
				if(corres.getUri1().equals(c.getUri1()) && corres.getUri2().equals(c.getUri2())) {
					return corres;
				}
			}
		}
		return null;
	}


	private List<Alignment> load(String path) throws IOException {
		List<Alignment> alignments = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
			if(Files.isRegularFile(filePath)) {
				try {
					Alignment a = reader.getAlignment(filePath.toString());
					a.setName(filePath.getFileName().toString());
					alignments.add(a);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return alignments;
	}
}
