package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNAddStrategyTaskSequential implements BPMNAddStrategy{

	private Random random;
	
	public BPMNAddStrategyTaskSequential() {
		this.random = new Random();
	}
	
	@Override
	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity) {
		UserTask userTask = model.newInstance(UserTask.class);
		userTask.setName(activity.getLabel());
		//Select sequence flow to place new task in
		List<SequenceFlow> flows = selectCandidates(model);
		SequenceFlow flow = flows.get(random.nextInt(flows.size()));
		//Select the left node and right node of that sequence flow
		//and remove the flow
		FlowNode leftNode = flow.getSource();
		FlowNode rightNode = flow.getTarget();
		leftNode.getOutgoing().remove(flow);
		rightNode.getIncoming().remove(flow);
		//Remove the flow itself
		ModelElementInstance parent = flow.getParentElement();
		parent.removeChildElement(flow);
		//Create new flows
		SequenceFlow flowLeft = model.newInstance(SequenceFlow.class);
		SequenceFlow flowRight = model.newInstance(SequenceFlow.class);
		parent.addChildElement(userTask);
		parent.addChildElement(flowLeft);
		parent.addChildElement(flowRight);
		flowLeft.setSource(leftNode);
		flowLeft.setTarget(userTask);
		flowRight.setSource(userTask);
		flowRight.setTarget(rightNode);
		leftNode.getOutgoing().add(flowLeft);
		userTask.getIncoming().add(flowLeft);
		userTask.getOutgoing().add(flowRight);
		rightNode.getIncoming().add(flowRight);
		return model;
	}
	
	private List<SequenceFlow> selectCandidates(BpmnModelInstance model) {
		List<SequenceFlow> seqFlows = new ArrayList<>();
		Iterator<SequenceFlow> flows = model.getModelElementsByType(SequenceFlow.class).iterator();
		while(flows.hasNext()) {
			SequenceFlow flow = flows.next();
			FlowNode leftNode = flow.getSource();
			FlowNode rightNode = flow.getTarget();
			if(leftNode instanceof Task && rightNode instanceof Task) 
				seqFlows.add(flow);
		}
		return seqFlows;
	}

}
