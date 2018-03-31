package de.unima.ki.pmmc.synthesizer.transformation;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.model.Activity;

public interface Transformer {
	
	public Alignment initializeModel(String modelPath, String modelName);
	
	public Activity transformActivity(String id, String transformation, boolean isValidCorres);
	
	public Activity transformActivity(Activity activity, boolean isValidCorres);

	public List<Pair<Activity, Activity>> one2manyParallel(String oriActivity, String... replacements);
	
	public List<Pair<Activity, Activity>> one2manySequential(String oriActivity, String... replacements);
	
	public List<Pair<Activity, Activity>> many2oneParallel(String newActivity, String... replacements);
	
	public List<Pair<Activity, Activity>> many2oneSequential(String newActivity, String... replacements);
	
	public void addIrrelevant(AddStrategy addStrategy, Activity... activities);
	
	public void addIrrelevantFromDataset(File dataset, double ratio, AddStrategy addStrategy);
	
	public List<Pair<Activity, Activity>> flip(Direction direction);
	
	public void replaceSynonyms(double probability, List<String> synonyms);
	
	public void replaceSynonyms(double probability, String... synonyms);
	
	public void partMappingFromDataset(File dataset);
	
	public boolean writeModel(String name, String path);
	
	public List<Activity> getActivitiesByName(String name);
	
	public Activity getActivityById(String id);
	
}
