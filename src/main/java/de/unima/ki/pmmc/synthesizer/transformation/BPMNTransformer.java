package de.unima.ki.pmmc.synthesizer.transformation;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.alignment.SemanticRelation;
import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNTransformer implements Transformer{

	private BpmnModelInstance model;
	
	@Override
	public Alignment initializeModel(String modelPath) {
		model =  Bpmn.readModelFromFile(new File(modelPath));
		Alignment alignment = new Alignment();
		ModelElementType taskType = model.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = model.getModelElementsByType(taskType);
		for(ModelElementInstance task : taskInstances) {
			Correspondence corres = new Correspondence(task.getAttributeValue("id"), 
					task.getAttributeValue("id"), SemanticRelation.EQUIV, 1.0, CorrespondenceType.TRIVIAL);
			alignment.add(corres);
		}
		return alignment;
	}

	@Override
	public void transformActivity(Activity a1, Activity a2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Activity> one2manyParallel(String oriActivity, String... replacements) {
		return null;
	}

	@Override
	public List<Activity> one2manyParallel(String oriActivityId, List<Pair<String, CorrespondenceType>> replacements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Activity> one2manySequencial(String oriActivity, String... replacements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void many2one(String newActivity, String... replacements) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIrrelevant(Activity... activities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIrrelevantFromDataset(File dataset, double ratio) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flip(Direction direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replaceSynonyms(Collection<String> synonyms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replaceSynonyms(String... synonyms) {
		// TODO Auto-generated method stub
		
	}


}
