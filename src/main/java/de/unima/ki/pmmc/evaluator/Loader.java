//package de.unima.ki.pmmc.evaluator;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.xml.sax.SAXException;
//
//import de.unima.ki.pmmc.evaluator.alignment.Alignment;
//import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
//import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
//import de.unima.ki.pmmc.evaluator.annotator.Annotator;
//import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
//import de.unima.ki.pmmc.evaluator.matcher.Result;
//import de.unima.ki.pmmc.evaluator.model.Activity;
//import de.unima.ki.pmmc.evaluator.model.Model;
//import de.unima.ki.pmmc.evaluator.model.parser.Parser;
//
//public class Loader {
//
//	private Parser parser;
//	private AlignmentReader reader;
//	private String currMatcherName;
//	private String currMatcherPath;
//	private List<Alignment> currAlignments;
//	private List<Alignment> goldstandard;
//	private static List<Model> models;
//	private static Annotator annotator;
//	private boolean tagCTOn;
//	private static boolean debugOn;
//	
//	private static final String SEPERATOR = "-";
//	private static final Logger LOG = Logger.getLogger(Loader.class.getName());
//	
//	public Loader(Parser parser, AlignmentReader reader) {
//		this.parser = parser;
//		this.reader = reader;
//		this.currAlignments = new ArrayList<>();
//	}
//	
//	public List<Model> loadModels(String path) throws ParserConfigurationException, SAXException, IOException {
//		log("Loading models from " + path);
//		List<Model> models = new ArrayList<>();
//		Files.walk(Paths.get(path)).forEach(filePath -> {
//			if(Files.isRegularFile(filePath)) {
//				try {
//					String p = filePath.getFileName().toString();
//					if(p.contains(Parser.TYPE_BPMN) ||
//							p.contains(Parser.TYPE_EPK) || p.contains(Parser.TYPE_PNML)) {
//						Model model = parser.parse(filePath.toString());
//						model.setName(p.split("\\.")[0]);
//						models.add(model);
//						log("Found model at " + filePath);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		this.models = models;
//		return models;
//	}
//	
//	public Result loadResult(String path, String name, double threshold) throws IOException {
//		List<Alignment> alignments = new ArrayList<>();
//		Files.walk(Paths.get(path)).forEach(filePath -> {
//				if(Files.isRegularFile(filePath)) {
//					try {
//						Alignment a = reader.getAlignment(filePath.toString());
//						a.applyThreshold(threshold);
//						List<Model> sourceTargetModels = findModels(filePath);
//						//TODO what if list empty or only contains one model
//						a.setSourceModel(sourceTargetModels.get(0));
//						a.setTargetModel(sourceTargetModels.get(1));
//						a.setName(filePath.getFileName().toString());
//						alignments.add(a);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//		});
//		return new Result(name, path, threshold, alignments);
//	}
//	
//	/**
//	 * Automatically searches at a given path for matcher alignments
//	 * to build up the <code>Result</code> collection.
//	 * @param path the root path used as a starting point to search for results
//	 * @throws IOException
//	 */
//	public List<Result> searchForResults(Optional<String> path, List<String> matcherPaths, 
//			double threshold) throws IOException {
//		log("Generate Results for threshold [" + threshold + "]");
//		List<Result> results = new ArrayList<>();
//		for(String matcherPath : matcherPaths) {
//			results.add(loadResult(matcherPath, extractName(matcherPath), threshold));
//		}
//		if(path.isPresent()) {
//			Files.walk(Paths.get(path.get())).forEach(filePath -> {
//				if(isFileInGS(filePath.getFileName().toString())) {
//					try {
//						Alignment a = reader.getAlignment(filePath.toString());
//						a.applyThreshold(threshold);
//						a.setName(filePath.getFileName().toString());
//						List<Model> sourceTargetModels = findModels(filePath);
//						//TODO what if list empty or only contains one model
//						a.setSourceModel(sourceTargetModels.get(0));
//						a.setTargetModel(sourceTargetModels.get(1));
//						currAlignments.add(a);
//						currMatcherName = findName(filePath, path.get());
//						currMatcherPath = filePath.getParent().toString();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				} else if(!currAlignments.isEmpty()){
//					//TODO make check with models to verify explicitly
//					//Verify if currently read matcher alignment has the same size as goldstandard
////					Result goldstandard = this.goldstandard.values().iterator().next();
////					if(goldstandard.size() != currAlignments.size()) {
////						throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
////								+ currMatcherName + " GS: " + goldstandard.size() + " Matcher: " + currAlignments.size());
////					}
//					results.add(new Result(currMatcherName, currMatcherPath, threshold, currAlignments));
//					log("[" + currMatcherName + "] @ " + currMatcherPath + " [" + currAlignments.size() + "]");
//					currAlignments = new ArrayList<>();
//				}
//			});
//		}
//		return results;
//	}
//	
//	public void computeCharacteristic(Result result, Result goldstandard) {
//		if(tagCTOn) {
//			log("CTTagging...");
//			for(Alignment a : result) {
//				this.annotator.annotateAlignment(a);
//			}
//			try {
//				result.computeCharacteristics(goldstandard, tagCTOn);
//			} catch (CorrespondenceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////			this.goldstandard = applyCTAnnotationGS(this.goldstandard);
////			this.mapResult = applyCTAnnotation(this.mapResult);
//		} else {
//			try {
//				result.computeCharacteristics(goldstandard, tagCTOn);
//			} catch (CorrespondenceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	public static Alignment crossProductAlignment(Model sourceModel, Model targetModel) {
//		log("CrossProduct : " + sourceModel.getName() + "-" + targetModel.getName());
//		annotator = new Annotator(models);
//		Alignment alignment = new Alignment();
//		for(Activity source : sourceModel.getActivities()) {
//			for(Activity target : targetModel.getActivities()) {
//				alignment.add(new Correspondence(sourceModel.getName() + "#" + source.getId(), 
//						targetModel.getName() + "#" + target.getId(), 1.0));
//			}
//		}
//		annotator.annotateAlignment(alignment);
//		return alignment;
//	}
//	
//	private List<Model> findModels(Path path) {
//		List<Model> models = new ArrayList<>();
//		for(Model model : this.models) {
//			if(path.toString().contains(model.getName())) {
//				models.add(model);
//			}
//		}
//		return models;
//	}
//	
//	/**
//	 * Given a file path and a root name, automatically
//	 * extracts the name for a matcher as the directory
//	 * chain from the root to the actual RDF <code>Alignment</code>
//	 * files.
//	 * @param filePath the file path to start searching from
//	 * @param root the top most root path of the matcher
//	 * @return the extracted matcher name for a <code>Result</code>
//	 */
//	private String findName(Path filePath, String root) {
//		List<String> dirs = new ArrayList<>();
//		while(filePath.getParent() != null && 
//				!filePath.getFileName().toString()
//				.equals(Paths.get(root).getFileName().toString())) {
//			if(Files.isDirectory(filePath)) {
//				dirs.add(filePath.getFileName().toString());
//			}
//			filePath = filePath.getParent();
//		}
//		String name = "";
//		for (int i = dirs.size()-1; i >= 0; i--) {
//			name += dirs.get(i);
//			if(i != 0) {
//				name += SEPERATOR;
//			}
//		}
//		return name;
//	}
//	
//	private boolean isFileInGS(String filename) {
//		for(Alignment a : goldstandard) {
//			if(a.getName().equals(filename)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	private String extractName(String path) {
//		return path;
//	}
//	
//	private static void log(String info) {
//		if(debugOn) {
//			LOG.info(info);
//		}
//	}
//
//	public Parser getParser() {
//		return parser;
//	}
//
//	public void setParser(Parser parser) {
//		this.parser = parser;
//	}
//
//	public AlignmentReader getReader() {
//		return reader;
//	}
//
//	public void setReader(AlignmentReader reader) {
//		this.reader = reader;
//	}
//
//	public List<Alignment> getGoldstandard() {
//		return goldstandard;
//	}
//
//	public void setGoldstandard(List<Alignment> goldstandard) {
//		this.goldstandard = goldstandard;
//	}
//
//	public List<Model> getModels() {
//		return models;
//	}
//
//	public void setModels(List<Model> models) {
//		this.models = models;
//	}
//
//	public boolean isDebugOn() {
//		return debugOn;
//	}
//
//	public void setDebugOn(boolean debugOn) {
//		this.debugOn = debugOn;
//	}
//
//	public boolean isTagCTOn() {
//		return tagCTOn;
//	}
//
//	public void setTagCTOn(boolean tagCTOn) {
//		this.tagCTOn = tagCTOn;
//	}
//	
//}
