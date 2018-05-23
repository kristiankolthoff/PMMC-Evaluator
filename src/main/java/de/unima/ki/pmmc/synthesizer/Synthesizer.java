package de.unima.ki.pmmc.synthesizer;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentWriterXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.SemanticRelation;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.synthesizer.transformation.Direction;
import de.unima.ki.pmmc.synthesizer.transformation.Transformer;
import de.unima.ki.pmmc.synthesizer.transformation.adding.AddStrategy;

public class Synthesizer {

	private Transformer transformer;
	private Alignment goldstandard;
	private String modelName;
	
	public Synthesizer(Transformer transformer) {
		this.transformer = transformer;
	}
	
	public Synthesizer readModel(String modelPath, String modelName) {
		goldstandard = transformer.initializeModel(modelPath, modelName);
		this.modelName = modelName;
		return this;
	}
	
	public Synthesizer transformActivity(String id, String transformation, boolean remainsValid) {
		Activity insActivity = transformer.transformActivity(id, transformation);
		if(!remainsValid) {
			removeFromGoldstandard(insActivity);
		}
		return this;
	}
	
	public Synthesizer transformActivity(Activity activity, boolean remainsValid) {
		Activity insActivity = transformer.transformActivity(activity);
		if(!remainsValid) {
			removeFromGoldstandard(insActivity);
		}
		return this;
	}
	
	public Synthesizer one2ManyParallel(String oriActivityId, String... replacements) {
		List<Pair<Activity, Activity>> insActivities = transformer.one2manyParallel(oriActivityId, replacements);
		goldstandard.getCorrespondences()
					.removeIf(corres -> {return corres.getUri1().contains(oriActivityId);});
		insActivities.stream()
					 .forEach(pair -> {goldstandard.add(new Correspondence(pair.getLeft().getId(), 
							 pair.getRight().getId(), SemanticRelation.EQUIV, 1.0));});
		return this;
	}
	
	public Synthesizer one2ManySequential(String oriActivityId, String... replacements) {
		transformer.one2manySequential(oriActivityId, replacements);
		return this;
	}
	
	public Synthesizer many2OneParallel(String replacement, String... oriActivityIds) {
		List<Pair<Activity, Activity>> pairs = transformer.many2oneParallel(replacement, oriActivityIds);
		pairs.stream().forEach(pair -> 
		        {goldstandard.getCorrespondences()
		        	.removeIf(corres -> {return corres.getUri1().contains(pair.getLeft().getId());});
		        goldstandard.add(new Correspondence(pair.getLeft().getId(), 
		        		pair.getRight().getId(), SemanticRelation.EQUIV, 1.0));;});
		return this;
	}
	
	public Synthesizer many2OneSequential(String oriActivityId, String... replacements) {
		transformer.many2oneSequential(oriActivityId, replacements);
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
	
	public Synthesizer transformActivity(Activity activity) {
		transformer.transformActivity(activity);
		return this;
	}
	
	public Synthesizer transformActivity(String id, String label) {
		transformer.transformActivity(id, label);
		return this;
	}
	
	public Synthesizer addIrrelevant(AddStrategy addStrategy, Activity... activities) {
		transformer.addIrrelevant(addStrategy, activities);
		return this;
	}
	
	public Synthesizer addIrrelevantFromDataset(AddStrategy addStrategy, File dataset, double ratio) {
		transformer.addIrrelevantFromDataset(addStrategy, dataset, ratio);
		return this;
	}
	
	public Synthesizer flip(Direction direction) {
		transformer.flip(direction);
		goldstandard.getCorrespondences().clear();
		return this;
	}
	
	public Synthesizer finished(String path) throws AlignmentException {
		final String directoryName = path + "/" + modelName;
		File directory = new File(directoryName);
		directory.mkdir();
		transformer.writeModel(modelName, directoryName);
		new AlignmentWriterXml().writeAlignment(directoryName + "/goldstandard.rdf", goldstandard);
		return this;
	}
	
	private void removeFromGoldstandard(Activity activity) {
		goldstandard.getCorrespondences().removeIf(corres -> 
			{return corres.getUri2().equals(activity.getId());});
	}
	
	private void addToGoldstandard(List<Pair<Activity, Activity>> pairs) {
		List<Correspondence> corres = pairs.stream().map(pair -> {return 
				new Correspondence(pair.getLeft().getId(), pair.getRight().getId(), 
					SemanticRelation.EQUIV, 1.0);}).collect(Collectors.toList());
		goldstandard.addAll(corres);
	}
	
}
