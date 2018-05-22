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
import org.apache.ecs.xhtml.pre;
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
import de.unima.ki.pmmc.synthesizer.transformation.adding.AddStrategy;
import de.unima.ki.pmmc.synthesizer.transformation.adding.BPMNAddStrategy;

public class BPMNTransformer implements Transformer{

	private BpmnModelInstance model;
	private BpmnModelInstance unmodifiedModel;
	private Random random;
	private String modelName;
	
	private static final String ATTR_NAME = "name";
	private static final String ATTR_ID = "id";
	private static final String HTTP = "http://";
	private static final String SEPERATOR = "#";
	private static final String ORIGINAL = "original";
	private static final String TRANSF = "transformed";
	
	public BPMNTransformer() {
		this.random = new Random();
	}
	
	@Override
	public Alignment initializeModel(String modelPath, String modelName) {
		File file = new File(modelPath);
		unmodifiedModel = Bpmn.readModelFromFile(file);
		model =  Bpmn.readModelFromFile(file);
		this.modelName = modelName;
		Alignment alignment = new Alignment();
		ModelElementType taskType = model.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = model.getModelElementsByType(taskType);
		for(ModelElementInstance task : taskInstances) {
			String uri1 = getFullSpecifiedOriModelId(task.getAttributeValue(ATTR_ID));
			String uri2 = getFullSpecifiedTransModelId(task.getAttributeValue(ATTR_ID));
			Correspondence corres = new Correspondence(uri1, uri2, 
					SemanticRelation.EQUIV, 1.0, CorrespondenceType.TRIVIAL);
			alignment.add(corres);
		}
		return alignment;
	}

	@Override
	public Activity transformActivity(String id, String transformation) {
		Task task = (Task) model.getModelElementById(id);
		if(Objects.isNull(task)) {
			throw new IllegalArgumentException("Activity with id " + id + " not found");
		}
		task.setAttributeValue(ATTR_NAME, transformation);
		return new Activity(task.getAttributeValue(ATTR_ID), task.getAttributeValue(ATTR_NAME));
	}

	@Override
	public Activity transformActivity(Activity activity) {
		Task task = (Task) model.getModelElementById(activity.getId());
		if(Objects.isNull(task)) {
			throw new IllegalArgumentException("Activity with id " + activity.getId() + " not found");
		}
		task.setAttributeValue(ATTR_NAME, activity.getLabel());
		return new Activity(task.getAttributeValue(ATTR_ID), task.getAttributeValue(ATTR_NAME));
	}

	@Override
	public List<Pair<Activity, Activity>> one2manyParallel(String oriActivity, String... replacements) {
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
		Activity activity = new Activity(task.getId(), task.getName());
		List<Pair<Activity, Activity>> activities = new ArrayList<>();
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
			activities.add(Pair.of(
					new Activity(getFullSpecifiedOriModelId(activity.getId()), activity.getLabel()),
					new Activity(getFullSpecifiedTransModelId(userTask.getId()), userTask.getName())));
			//Update all sequence flow lists
			gatewayDiv.getOutgoing().add(seqFlowTaskFrom);
			userTask.getIncoming().add(seqFlowTaskFrom);
			userTask.getOutgoing().add(seqFlowTaskTo);
			gatewayConv.getIncoming().add(seqFlowTaskTo);
		}
		return activities;
	}

	@Override
	public List<Pair<Activity, Activity>> one2manySequential(String oriActivity, String... replacements) {
		return Collections.emptyList();
	}

	@Override
	public List<Pair<Activity, Activity>> many2oneParallel(String newActivity, String... replacements) {
		if(replacements.length == 0) {
			return Collections.emptyList();
		}
		List<Task> tasks = new ArrayList<>();
		List<SequenceFlow> sequenceFlowsIn = new ArrayList<>();
		List<SequenceFlow> sequenceFlowsOut = new ArrayList<>();
		List<ParallelGateway> gatewaysLeft = new ArrayList<>();
		List<ParallelGateway> gatewaysRight = new ArrayList<>();
		for(String oriActivity : replacements) {
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
		preceeding.getOutgoing().add(flowTo);
		userTask.getIncoming().add(flowTo);
		//New sequence flow from the inserted activity
		SequenceFlow flowFrom = model.newInstance(SequenceFlow.class);
		parent.addChildElement(flowFrom);
		flowFrom.setSource(userTask);
		flowFrom.setTarget(succeding);
		userTask.getOutgoing().add(flowFrom);
		succeding.getIncoming().add(flowFrom);
		//Compute activity pairs for the goldstandard
		List<Pair<Activity, Activity>> activites = new ArrayList<>();
		Activity insActivity = new Activity(getFullSpecifiedTransModelId(userTask.getId()), 
				userTask.getName());
		for(Task task : tasks) {
			activites.add(Pair.of(new Activity(getFullSpecifiedOriModelId(task.getId()), task.getName()), 
					insActivity));
		}
		return activites;
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
	public List<Pair<Activity, Activity>> many2oneSequential(String newActivity, String... replacements) {
		return Collections.emptyList();
	}

	@Override
	public void addIrrelevant(AddStrategy addStrategy, Activity... activities) {
		if(addStrategy instanceof BPMNAddStrategy) {
			BPMNAddStrategy bpmnAddStrategy = (BPMNAddStrategy) addStrategy;
			for (int i = 0; i < activities.length; i++) {
				model = bpmnAddStrategy.addActivity(model, activities[i]);
			}
		} else {
			throw new IllegalArgumentException("Using not compatible AddStrategy " 
							+ addStrategy + " for BPMN models");
		}
	}

	@Override
	public void addIrrelevantFromDataset(AddStrategy addStrategy, File dataset, double ratio) {
		BpmnModelInstance modelB = Bpmn.readModelFromFile(dataset);
		ModelElementType taskType = modelB.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = modelB.getModelElementsByType(taskType);
		for(ModelElementInstance task : taskInstances) {
			if(random.nextDouble() <= ratio) {
				addIrrelevant(addStrategy, new Activity(task.getAttributeValue(ATTR_ID), 
						task.getAttributeValue(ATTR_NAME)));
			}
		}
	}

	@Override
	public List<Pair<Activity, Activity>> flip(Direction direction) {
		Collection<SequenceFlow> flows = model.getModelElementsByType(SequenceFlow.class);
		for(SequenceFlow flow : flows) {
			FlowNode leftNode = flow.getSource();
			FlowNode rightNode = flow.getTarget();
			leftNode.getOutgoing().remove(flow);
			rightNode.getIncoming().remove(flow);
			flow.setSource(rightNode);
			flow.setTarget(leftNode);
			leftNode.getIncoming().add(flow);
			rightNode.getOutgoing().add(flow);
		}
		return Collections.emptyList();
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
	public void partMappingFromDataset(File dataset) {
		return;
	}

	@Override
	public boolean writeModel(String name, String path) {
		Bpmn.validateModel(unmodifiedModel);
		Bpmn.validateModel(model);
		Bpmn.writeModelToFile(new File(path + "/" + name + "_original.bpmn"), unmodifiedModel);
		Bpmn.writeModelToFile(new File(path + "/" + name + "_transformed.bpmn"), model);
		return false;
	}

	@Override
	public List<Activity> getActivitiesByName(String name) {
		List<Activity> activities = new ArrayList<>();
		ModelElementType taskType = model.getModel().getType(Task.class);
		Collection<ModelElementInstance> taskInstances = model.getModelElementsByType(taskType);
		for(ModelElementInstance task : taskInstances) {
			if(task.getAttributeValue(ATTR_NAME).equals(name)) 
				activities.add(new Activity(task.getAttributeValue(ATTR_ID), 
						task.getAttributeValue(ATTR_NAME)));
		}
		return activities;
	}

	@Override
	public Activity getActivityById(String id) {
		Task task = (Task) model.getModelElementById(id);
		if(Objects.isNull(task)) {
			throw new IllegalArgumentException("Activity with id " + id + " not found");
		}
		return null;
	}
	
	private String getFullSpecifiedOriModelId(String id) {
		return HTTP + modelName + "_" + ORIGINAL + SEPERATOR + id;
	}
	
	private String getFullSpecifiedTransModelId(String id) {
		return HTTP + modelName + "_" + TRANSF + SEPERATOR + id;
	}
	
}
