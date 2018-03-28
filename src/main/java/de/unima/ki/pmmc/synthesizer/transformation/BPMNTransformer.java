package de.unima.ki.pmmc.synthesizer.transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.alignment.SemanticRelation;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;

public class BPMNTransformer implements Transformer{

	private BpmnModelInstance model;
	private Random random;
	
	private static final String ATTR_NAME = "name";
	private static final String ATTR_ID = "id";
	
	public BPMNTransformer() {
		this.random = new Random();
	}
	
	@Override
	public Alignment initializeModel(String modelPath) {
		model =  Bpmn.readModelFromFile(new File(modelPath));
		Alignment alignment = new Alignment();
		ModelElementType taskType = model.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = model.getModelElementsByType(taskType);
		for(ModelElementInstance task : taskInstances) {
			Correspondence corres = new Correspondence(task.getAttributeValue(ATTR_ID), 
					task.getAttributeValue(ATTR_ID), SemanticRelation.EQUIV, 1.0, CorrespondenceType.TRIVIAL);
			alignment.add(corres);
		}
		return alignment;
	}

	@Override
	public void transformActivity(String id, Activity activity) {
		Task task = (Task) model.getModelElementById(id);
		if(Objects.isNull(task)) {
			throw new IllegalArgumentException("Activity with id " + id + " not found");
		}
		task.setAttributeValue(ATTR_NAME, activity.getLabel());
	}

	@Override
	public Pair<Activity, List<Activity>> one2manyParallel(String oriActivity, String... replacements) {
		Task task = (Task) model.getModelElementById(oriActivity);
		if(Objects.isNull(task)) {
			throw new IllegalArgumentException("Activity with id " + oriActivity + " not found");
		}
		ModelElementInstance parent = task.getParentElement();
		//Get the bordering sequence flow elements of the task 
		SequenceFlow sequenceFlowIncoming = task.getIncoming().iterator().next();
		SequenceFlow sequenceFlowOutgoing = task.getOutgoing().iterator().next();
		//Retrieve preceeding and succeding FlowNodes of the task
		FlowNode preceedingElement = sequenceFlowIncoming.getSource();
		FlowNode succedingElement = sequenceFlowOutgoing.getTarget();
		//Remove original sequence flows and task from the model
		preceedingElement.getOutgoing().remove(sequenceFlowIncoming);
		succedingElement.getIncoming().remove(sequenceFlowOutgoing);
		task.getIncoming().remove(sequenceFlowIncoming);
		task.getOutgoing().remove(sequenceFlowOutgoing);
		parent.removeChildElement(sequenceFlowIncoming);
		parent.removeChildElement(sequenceFlowOutgoing);
		parent.removeChildElement(task);
		//Create the first parallel gateway
		ParallelGateway gatewayDiv = model.newInstance(ParallelGateway.class);
		gatewayDiv.setGatewayDirection(GatewayDirection.Diverging);
		parent.addChildElement(gatewayDiv);
		//Connect gateway to model
		SequenceFlow flowTo = model.newInstance(SequenceFlow.class);
		parent.addChildElement(flowTo);
		flowTo.setSource(preceedingElement);
		flowTo.setTarget(gatewayDiv);
		//Create the second parallel gateway
		ParallelGateway gatewayConv = model.newInstance(ParallelGateway.class);
		gatewayConv.setGatewayDirection(GatewayDirection.Converging);
		parent.addChildElement(gatewayConv);
		//Connect gateway to model
		SequenceFlow flowFrom = model.newInstance(SequenceFlow.class);
		parent.addChildElement(flowFrom);
		flowFrom.setSource(gatewayConv);
		flowFrom.setTarget(succedingElement);
		//Update bordering sequence flow lists
		preceedingElement.getOutgoing().add(flowTo);
		gatewayDiv.getIncoming().add(flowTo);
		gatewayConv.getOutgoing().add(flowFrom);
		succedingElement.getIncoming().add(flowFrom);
		//Create all the tasks and add them to the gateway
		List<Activity> activities = new ArrayList<>();
		for(String replacement : replacements) {
			UserTask userTask = model.newInstance(UserTask.class);
			userTask.setName(replacement);
			parent.addChildElement(userTask);
			//First sequence flow from the gateway
			SequenceFlow seqFlowTaskFrom = model.newInstance(SequenceFlow.class);
			parent.addChildElement(seqFlowTaskFrom);
			seqFlowTaskFrom.setSource(gatewayDiv);
			seqFlowTaskFrom.setTarget(userTask);
			//Second sequence flow to the gatway
			SequenceFlow seqFlowTaskTo = model.newInstance(SequenceFlow.class);
			parent.addChildElement(seqFlowTaskTo);
			seqFlowTaskTo.setSource(userTask);
			seqFlowTaskTo.setTarget(gatewayConv);
			activities.add(new Activity(userTask.getId(), userTask.getName()));
			//Update all sequence flow lists
			gatewayDiv.getOutgoing().add(seqFlowTaskFrom);
			userTask.getIncoming().add(seqFlowTaskFrom);
			userTask.getOutgoing().add(seqFlowTaskTo);
			gatewayConv.getIncoming().add(seqFlowTaskTo);
		}
		return null;
	}
	
	@Override
	public Pair<Activity, List<Activity>> many2oneParallel(String newActivity, String... oriActivityIds) {
		if(oriActivityIds.length == 0) {
			return Pair.of(new Activity(null, null), Collections.emptyList());
		}
		List<Task> tasks = new ArrayList<>();
		List<SequenceFlow> sequenceFlowsIn = new ArrayList<>();
		List<SequenceFlow> sequenceFlowsOut = new ArrayList<>();
		List<ParallelGateway> gatewaysLeft = new ArrayList<>();
		List<ParallelGateway> gatewaysRight = new ArrayList<>();
		for(String oriActivity : oriActivityIds) {
			Task task = (Task) model.getModelElementById(oriActivity);
			tasks.add(task);
			if(Objects.isNull(task)) {
				throw new IllegalArgumentException("Activity with id " + oriActivity + " not found");
			}
			SequenceFlow sequenceFlowIn = task.getIncoming().iterator().next();
			sequenceFlowsIn.add(sequenceFlowIn);
			SequenceFlow sequenceFlowOut = task.getOutgoing().iterator().next();
			sequenceFlowsOut.add(sequenceFlowOut);
			FlowNode flowNodeLeft = sequenceFlowIn.getSource();
			FlowNode flowNodeRight = sequenceFlowOut.getTarget();
			if(flowNodeLeft instanceof ParallelGateway && flowNodeRight instanceof ParallelGateway) {
				gatewaysLeft.add((ParallelGateway) flowNodeLeft);
				gatewaysRight.add((ParallelGateway) flowNodeRight);
			} else {
				throw new IllegalArgumentException("Neighbouring flow nodes of "
						+ "specified activities need to be ParallelGateways");
			}
		}
		if(!verifyGatewayEquality(gatewaysLeft)) {
			throw new IllegalArgumentException("Specified activities reference "
					+ "different ParallelGateways on the left");
		}
		if(!verifyGatewayEquality(gatewaysRight)) {
			throw new IllegalArgumentException("Specified activities reference "
					+ "different ParallelGateways on the right");
		}
		//Delete all tasks, sequence flows and gateways from the model
		ModelElementInstance parent = tasks.get(0).getParentElement();
		for (int i = 0; i < tasks.size(); i++) {
			parent.removeChildElement(tasks.get(i));
			parent.removeChildElement(sequenceFlowsIn.get(i));
			parent.removeChildElement(sequenceFlowsOut.get(i));
		}
		ParallelGateway gatewayLeft = gatewaysLeft.get(0);
		ParallelGateway gatewayRight = gatewaysRight.get(0);
		FlowNode preceeding = gatewayLeft.getIncoming().iterator().next().getSource();
		FlowNode succeding = gatewayRight.getOutgoing().iterator().next().getTarget();
		parent.removeChildElement(gatewayLeft);
		parent.removeChildElement(gatewayRight);
		SequenceFlow sequenceFlowLeft = preceeding.getOutgoing().iterator().next();
		SequenceFlow sequenceFlowRight = succeding.getIncoming().iterator().next();
		preceeding.getOutgoing().remove(sequenceFlowLeft);
		succeding.getIncoming().remove(sequenceFlowRight);
		parent.removeChildElement(sequenceFlowLeft);
		parent.removeChildElement(sequenceFlowRight);
		//Insert new activity
		UserTask userTask = model.newInstance(UserTask.class);
		userTask.setName(newActivity);
		parent.addChildElement(userTask);
		//New sequence flow to the inserted activity
		SequenceFlow flowTo = model.newInstance(SequenceFlow.class);
		parent.addChildElement(flowTo);
		flowTo.setSource(preceeding);
		flowTo.setTarget(userTask);
		//New sequence flow from the inserted activity
		SequenceFlow flowFrom = model.newInstance(SequenceFlow.class);
		parent.addChildElement(flowFrom);
		flowFrom.setSource(userTask);
		flowFrom.setTarget(succeding);
//		return new Activity(userTask.getName(), userTask.getId());
		return null;
	}
	
	private boolean verifyGatewayEquality(List<ParallelGateway> gateways) {
		for (int i = 0; i < gateways.size() - 1; i++) {
			ParallelGateway gatewayLeft = gateways.get(i);
			ParallelGateway gatewayRight = gateways.get(i+1);
			if(!gatewayLeft.equals(gatewayRight)) {
				return false;
			}
		}
		return true;
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
	public void replaceSynonyms(double probability, List<String> synonyms) {
		ModelElementType taskType = model.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = model.getModelElementsByType(taskType);
		for(ModelElementInstance task : taskInstances) {
			String taskName = task.getAttributeValue(ATTR_NAME);
			String label = NLPHelper.getSanitizeLabel(taskName);
			Optional<String> synFound = find(label, synonyms);
			if(synFound.isPresent()) {
				if(random.nextDouble() <= probability) {
					String replacement = NLPHelper.getSanitizeLabel(
							synonyms.get(random.nextInt(synonyms.size())));
					while(replacement.equals(synFound.get())) {
						replacement = NLPHelper.getSanitizeLabel(
								synonyms.get(random.nextInt(synonyms.size())));
					}
					label = label.replace(synFound.get(), replacement);
					task.setAttributeValue(ATTR_NAME, label);
				}
			}
		}
	}
	
	private Optional<String> find(String label, Collection<String> synonyms) {
		return synonyms.stream()
					   .map(s -> {return NLPHelper.getSanitizeLabel(s);})
					   .filter(s -> {return label.contains(s);}).findAny();
	}

	@Override
	public void replaceSynonyms(double probability, String... synonyms) {
		replaceSynonyms(probability, Arrays.asList(synonyms));
	}

	@Override
	public boolean writeModel(String name, String path) {
		Bpmn.validateModel(model);
		Bpmn.writeModelToFile(new File(path + name), model);
		return false;
	}

	@Override
	public void partMappingFromDataset(File dataset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Pair<Activity, List<Activity>> one2manySequential(String oriActivity, String... replacements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Activity, List<Activity>> many2oneSequential(String newActivity, String... replacements) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
