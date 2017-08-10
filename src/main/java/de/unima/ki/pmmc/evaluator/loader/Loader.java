package de.unima.ki.pmmc.evaluator.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.data.Solution;

public class Loader {

	private AlignmentReader reader;
	private List<Alignment> currAlignments;
	private String currMatcherName;
	private String currMatcherPath;
	
	private static final String SEPERATOR = "-";

	
	public Loader(AlignmentReader reader) {
		this.reader = reader;
		this.currAlignments = new ArrayList<>();
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

	public List<Solution> loadAll(Optional<String> path, 
			List<String> matcherPaths, double threshold) throws IOException {
//		log("Generate Results for threshold [" + threshold + "]");
		List<Solution> results = new ArrayList<>();
		for(String matcherPath : matcherPaths) {
			results.add(load(matcherPath, extractName(matcherPath), threshold));
		}
		if(path.isPresent()) {
			Files.walk(Paths.get(path.get())).forEach(filePath -> {
					try {
						Alignment a = reader.getAlignment(filePath.toString());
						a.applyThreshold(threshold);
						a.setName(filePath.getFileName().toString());
						currAlignments.add(a);
						currMatcherName = findName(filePath, path.get());
						currMatcherPath = filePath.getParent().toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
				if(!currAlignments.isEmpty()){
					
					results.add(new Solution(currMatcherName, currMatcherPath, threshold, currAlignments));
//					log("[" + currMatcherName + "] @ " + currMatcherPath + " [" + currAlignments.size() + "]");
					currAlignments = new ArrayList<>();
				}
			});
		}
		return results;
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
				name += SEPERATOR;
			}
		}
		return name;
	}
	
	private String extractName(String path) {
		return path;
	}

}
