package de.unima.ki.pmmc.synthesizer;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.alignment.SemanticRelation;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.synthesizer.transformation.Transformer;

public class Synthesizer {

	private Transformer transformer;
	private Alignment goldstandard;

	
	public Synthesizer(Transformer transformer) {
		this.transformer = transformer;
	}
	
	public Synthesizer readModel(String modelPath) {
		goldstandard = transformer.initializeModel(modelPath);
		return this;
	}
	
	public Synthesizer one2ManyParallel(String oriActivityId, String... replacements) {
//		List<Activity> insActivities = transformer.one2manyParallel(oriActivityId, replacements);
//		goldstandard.getCorrespondences()
//					.removeIf(corres -> {return corres.getUri1().equals(oriActivityId);});
//		insActivities.stream()
//					 .forEach(act -> {goldstandard.add(new Correspondence(oriActivityId, act.getId(),
//							                                         SemanticRelation.EQUIV, 1.0));});
		return this;
	}
	
	public Synthesizer many2OneParallel(String replacement, String... oriActivityIds) {
//		Activity insActivity = transformer.manyParallel2one(replacement, oriActivityIds);
		return this;
	}
	
	public Synthesizer replaceAllSynonyms(String... synonyms) {
		replaceSynonymsWithProbability(1.0, synonyms);
		return this;
	}
	
	public Synthesizer replaceSynonymsWithProbability(double probability, List<String> synonyms) {
		transformer.replaceSynonyms(probability, synonyms);
		return this;
	}
	
	public Synthesizer replaceSynonymsWithProbability(double probability, String... synonyms) {
		transformer.replaceSynonyms(probability, synonyms);
		return this;
	}
	
	public Synthesizer transformActivity(String id, Activity activity) {
		transformer.transformActivity(id, activity);
		return this;
	}
	
	public Synthesizer transformActivity(String id, String label) {
		transformer.transformActivity(id, new Activity(null, label));
		return this;
	}
	
	public Synthesizer addIrrelevant(Activity... activities) {
		transformer.addIrrelevant(activities);
		return this;
	}
	
	public Synthesizer addIrrelevantFromDataset(File dataset, double ratio) {
		transformer.addIrrelevantFromDataset(dataset, ratio);
		return this;
	}
	
	public Synthesizer finished(String name, String path) {
		//Check validation state
		transformer.writeModel(name, path);
		return this;
	}
	
}
