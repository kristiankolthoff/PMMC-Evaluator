package de.unima.ki.pmmc.evaluator.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.annotator.Annotator;
import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.loader.Loader;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;

public class MatcherSetTypeFraction {

	private AlignmentReader reader;
	private Loader loader;
	private Parser parser;
	private Annotator annotator;
	private DecimalFormat format;
	
	
	public MatcherSetTypeFraction(Parser parser) {
		this.format = new DecimalFormat("#.###");
		this.parser = parser;
		this.reader = new AlignmentReaderXml();
		this.loader = new Loader(reader);
	}
	
	public List<Solution> loadSolutionsAdmission() throws IOException {
		List<String> paths = new ArrayList<>();
		paths.add("src/main/resources/data/results/OAEI16/AML/");
		paths.add("src/main/resources/data/results/OAEI16/AML-PM/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/BPLangMatch/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/DKP/");
		paths.add("src/main/resources/data/results/OAEI16/DKP-lite/");
		paths.add("src/main/resources/data/results/OAEI16/KnoMa-Proc/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/Know-Match-SSS/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/LogMap/");
		paths.add("src/main/resources/data/results/OAEI16/Match-SSS/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/OPBOT/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/pPalm-DS/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/RMM-NHCM/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/RMM-NLM/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/RMM-SMSL/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/RMM-VM2/dataset1/");
		paths.add("src/main/resources/data/results/OAEI16/TripleS/dataset1/");
		return loader.loadAll(Optional.empty(), paths, 0d);
	}
	
	public List<Solution> loadSolutionsBirthCertificate() throws IOException {
		List<String> paths = new ArrayList<>();
		paths.add("src/main/resources/data/results/submitted-matchers/AML-PM/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/BPLangMatch/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/KnoMa-Proc/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/Know-Match-SSS/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/Match-SSS/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/OPBOT/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/pPalm-DS/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/RMM-NHCM/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/RMM-NLM/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/RMM-SMSL/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/RMM-VM2/dataset2/");
		paths.add("src/main/resources/data/results/submitted-matchers/TripleS/dataset2/");
		return loader.loadAll(Optional.empty(), paths, 0d);
	}
	
	private List<Model> loadModels(String path) throws ParserConfigurationException, SAXException, IOException {
		List<Model> models = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
			if(Files.isRegularFile(filePath)) {
				try {
					String p = filePath.getFileName().toString();
					if(p.contains(Parser.Type.BPMN.getSuffix()) ||
							p.contains(Parser.Type.EPML.getSuffix()) || 
							p.contains(Parser.Type.PNML.getSuffix())) {
						Model model = parser.parse(filePath.toString());
						model.setName(p.split("\\.")[0]);
						models.add(model);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return models;
	}
	
	public void computeFraction(final int filterThresh, List<Solution> solutions, List<Model> models) {
		this.annotator = new Annotator(models);
		Map<Correspondence, Integer> counts = new HashMap<>();
		solutions.stream()
				 .flatMap(solution -> {return solution.getAlignments().stream();})
				 .flatMap(alignment -> {return alignment.getCorrespondences().stream();})
				 .forEach(corres -> {
						if(counts.containsKey(corres)) {
							counts.put(corres, counts.get(corres) + 1);
						} else {
							counts.put(corres, 1);
						}
					});
		List<Correspondence> stream = counts.entrySet()
			  .stream()
			  .filter(entry -> {return entry.getValue() >= filterThresh;})
			  .map(entry -> {return entry.getKey();}).collect(Collectors.toList());
		long total = stream.stream().count();
		System.out.println(total);
		for(Correspondence corres : stream) {
			CorrespondenceType type = annotator.annotateCorrespondence(corres);
			corres.setType(type);
		}
		for(CorrespondenceType type : CorrespondenceType.values()) {
			List<Correspondence> typeCorres = stream.stream().filter(corres -> {return corres.getCType().get().equals(type);})
											   .collect(Collectors.toList());
			System.out.println(type + " | #" + typeCorres.size() + " | " + format.format((typeCorres.size() / (double) total) * 100) + "%");
		}
	}
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		MatcherSetTypeFraction fraction = new MatcherSetTypeFraction(ParserFactory.getParser(Parser.Type.PNML_2));
		List<Solution> solutions = fraction.loadSolutionsBirthCertificate();
		List<Model> models = fraction.loadModels("src/main/resources/data/dataset2/models/");
		fraction.computeFraction(3, solutions, models);
	}
}
