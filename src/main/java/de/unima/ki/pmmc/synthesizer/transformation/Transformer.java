package de.unima.ki.pmmc.synthesizer.transformation;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.model.Activity;

public interface Transformer {
	
	public Alignment initializeModel(String modelPath);
	
	public void transformActivity(String id, Activity activity);

	public Pair<Activity, List<Activity>> one2manyParallel(String oriActivity, String... replacements);
	
	public Pair<Activity, List<Activity>> one2manySequential(String oriActivity, String... replacements);
	
	public Pair<Activity, List<Activity>> many2oneParallel(String newActivity, String... replacements);
	
	public Pair<Activity, List<Activity>> many2oneSequential(String newActivity, String... replacements);
	
	public void addIrrelevant(Activity... activities);
	
	public void addIrrelevantFromDataset(File dataset, double ratio);
	
	public void flip(Direction direction);
	
	public void replaceSynonyms(double probability, List<String> synonyms);
	
	public void replaceSynonyms(double probability, String... synonyms);
	
	public void partMappingFromDataset(File dataset);
	
	public boolean writeModel(String name, String path);
	
}
