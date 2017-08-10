package de.unima.ki.pmmc.evaluator.data;

import java.util.Iterator;
import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;

public class Solution implements Iterable<Alignment>{

	private String name;
	private String path;
	private double threshold;
	private List<Alignment> alignments;
	
	public Solution(String name, String path, double threshold, List<Alignment> alignments) {
		this.name = name;
		this.path = path;
		this.threshold = threshold;
		this.alignments = alignments;
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public double getThreshold() {
		return threshold;
	}


	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}


	public List<Alignment> getAlignments() {
		return alignments;
	}


	public void setAlignments(List<Alignment> alignments) {
		this.alignments = alignments;
	}


	@Override
	public Iterator<Alignment> iterator() {
		return alignments.iterator();
	}
	
	
}
