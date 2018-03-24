package de.unima.ki.pmmc.synthesizer.transformation;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.model.Activity;

public interface Transformer {
	
	public Alignment initializeModel(String modelPath);
	
	public void transformActivity(Activity a1, Activity a2);

	public List<Activity> one2manyParallel(String oriActivity, String... replacements);
	
	public List<Activity> one2manyParallel(String oriActivityId, List<Pair<String, CorrespondenceType>> replacements);
	
	public List<Activity> one2manySequencial(String oriActivity, String... replacements);
	
	public void many2one(String newActivity, String... replacements);
	
	public void addIrrelevant(Activity... activities);
	
	public void addIrrelevantFromDataset(File dataset, double ratio);
	
	public void flip(Direction direction);
	
	public void replaceSynonyms(Collection<String> synonyms);
	
	public void replaceSynonyms(String... synonyms);
	
}
