package de.unima.ki.pmmc.evaluator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xerces.util.URI;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;



public class AlignmentLabelConverter {

	private List<Model> models;
	private Map<String, String> activityMap;
	private AlignmentReader alignmentReader;
	
	public AlignmentLabelConverter(List<Model> models) {
		this.models = models;
		this.activityMap = new HashMap<String, String>();
		this.alignmentReader = new AlignmentReaderXml();
		this.initMap();
		System.out.println();
	}
	
	public void initMap() {
		for(Model m : models) {
			for(Activity a : m.getActivities()) {
				this.activityMap.put(a.getId(), a.getLabel());
			}
		}
	}
	
	public void convertIDToLabel(String input, String output) throws AlignmentException, IOException {
		final Alignment alignment = this.alignmentReader.getAlignment(input);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output)));
		for(Correspondence c : alignment) {
			String id1 = c.getUri1();
			String id2 = c.getUri2();
			String label1 = activityMap.get(id1);
			String label2 = activityMap.get(id2);
			bw.newLine();
			bw.write("---------Correspondence---------");
			bw.newLine();
			bw.write(id1 + " : " + label1);
			bw.newLine();
			bw.write(id2 + " : " + label2);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	public String getLabel(String uri) {
		return activityMap.get(uri);
	}
	
	public void convertIDToLabel(Alignment alignment, String output) throws AlignmentException, IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output)));
		for(Correspondence c : alignment) {
			String id1 = new URI(c.getUri1()).getFragment();
			String id2 = new URI(c.getUri2()).getFragment();
			String label1 = activityMap.get(id1);
			String label2 = activityMap.get(id2);
			bw.newLine();
			bw.write("---------Correspondence---------");
			bw.newLine();
			bw.write(c.getCType().get().getName());
			bw.newLine();
			bw.write(c.getUri1() + " : " + label1);
			bw.newLine();
			bw.write(c.getUri2() + " : " + label2);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
}
