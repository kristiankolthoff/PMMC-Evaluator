package de.unima.ki.pmmc.evaluator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoldstandardGroup {

	private String name;
	private List<String> paths;
	private List<Solution> goldstandards;
	
	public GoldstandardGroup(String name, List<String> paths) {
		this.name = name;
		this.paths = paths;
		this.goldstandards = new ArrayList<>();
	}

	public GoldstandardGroup(String name) {
		this.name = name;
		this.paths = new ArrayList<>();
		this.goldstandards = new ArrayList<>();
	}
	
	public GoldstandardGroup addPath(String path) {
		this.paths.add(path);
		return this;
	}
	
	public GoldstandardGroup addSolution(Solution goldstandard) {
		this.goldstandards.add(goldstandard);
		return this;
	}
	
	public boolean isPreloaded() {
		return !goldstandards.isEmpty();
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<String> getPaths() {
		return paths;
	}


	public void setPaths(List<String> paths) {
		this.paths = paths;
	}


	public List<Solution> getGoldstandards() {
		return goldstandards;
	}
	
	public List<Solution> getGoldstandards(double threshold) {
		return goldstandards.stream()
				.filter(solution -> {return solution.getThreshold() == threshold;})
				.collect(Collectors.toList());
	}


	public void setGoldstandards(List<Solution> goldstandards) {
		this.goldstandards = goldstandards;
	}
	
	
}
