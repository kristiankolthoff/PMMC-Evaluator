package de.unima.ki.pmmc.synthesizer;

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
		List<Activity> insActivities = transformer.one2manyParallel(oriActivityId, replacements);
		goldstandard.getCorrespondences()
					.removeIf(corres -> {return corres.getUri1().equals(oriActivityId);});
		insActivities.stream()
					 .forEach(act -> {goldstandard.add(new Correspondence(oriActivityId, act.getId(),
							                                         SemanticRelation.EQUIV, 1.0));});
		return this;
	}
	
	public Synthesizer one2ManyParallel(String oriActivityId, List<Pair<String, CorrespondenceType>> replacements) {
		List<Activity> insActivities = transformer.one2manyParallel(oriActivityId, replacements);
		goldstandard.getCorrespondences()
					.removeIf(corres -> {return corres.getUri1().equals(oriActivityId);});
		if(insActivities.size() != replacements.size()) {
			throw new IllegalArgumentException("Different size of replacements and inserted activities");
		}
		for (int i = 0; i < insActivities.size(); i++) {
			Activity act = insActivities.get(i);
			Pair<String, CorrespondenceType> replacement = replacements.get(i);
			goldstandard.add(new Correspondence(oriActivityId, act.getId(), 
					SemanticRelation.EQUIV, 1.0, replacement.getValue()));
		}
		return this;
	}
	
}
