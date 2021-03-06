package de.unima.ki.pmmc.evaluator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;


public class Model {

	private String name;
	private String id;
	private String prefix;
	private Set<Activity> activities;
	private Set<Flow> flows;
	
	public Model(String name, String id) {
		this.name = name;
		this.id = id;
		this.activities = new HashSet<>();
		this.flows = new HashSet<>();
	}
	
	public Model() {
		this.activities = new HashSet<>();
		this.flows = new HashSet<>();
	}
	
	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}
	
	
	public void addFlow(Flow flow) {
		this.flows.add(flow);
	}

	public Set<Activity> getActivities() {
		return this.activities;
	}
	
	public String getLabelById(String id) {
		for (Activity a : this.activities) {
			if (a.getId().equals(id)) {
				return a.getLabel();
			}
		}
		return "?-" + id;
	}
	
	public void normalizeLabels() {
		for (Activity a : this.activities) {
			a.normalizeLabels();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static List<String> transformedAlignment(List<Model> models, Alignment alignment) {
		if(models.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> correspondences = new ArrayList<>();
		for(Correspondence c : alignment) {
			String[] labels = new String[2];
			for(Model m : models) {
				for(Activity a : m.getActivities()) {
					if(c.getUri1().contains(a.getId())) {
						labels[0] = a.getLabel();
					} else if(c.getUri2().contains(a.getId())) {
						labels[1] = a.getLabel();
						if(c.getCType().isPresent()) {
							labels[1] += " (" + c.getCType().get() + ")";
							if(c.getCType().get().equals(CorrespondenceType.TRIVIAL.getName())) {
								System.err.println(String.join(" = ", labels));
							}
						}
					}
				}
			}
			correspondences.add(String.join(" = ", labels));
		}
		return correspondences;
	}
	
	public static String getLabelFromId(Model model, String id) {
		for(Activity a : model.getActivities()) {
			if(a.getId().equals(id)) {
				return a.getLabel();
			}
		}
		return null;
	}
	
	public static String getLabelFromId(List<Model> models, String id) {
		for(Model model : models) {
			for(Activity a : model.getActivities()) {
				if(a.getId().equals(id)) {
					return a.getLabel();
				}
			} 
		}
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Model [name=" + name + ", id=" + id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	
}
