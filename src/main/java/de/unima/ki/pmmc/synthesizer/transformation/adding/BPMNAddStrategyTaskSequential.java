package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNAddStrategyTaskSequential implements BPMNAddStrategy{

	@Override
	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity) {
		UserTask userTask = model.newInstance(UserTask.class);
		userTask.setName(activity.getLabel());
		Collection<SequenceFlow> flows = model.getModelElementsByType(SequenceFlow.class);
		while(flows.iterator().hasNext()) {
			SequenceFlow flow = flows.iterator().next();
			FlowNode leftNode = flow.getSource();
			FlowNode rightNode = flow.getTarget();
			//TODO randomize selection
			if(leftNode instanceof Task && rightNode instanceof Task) {
				leftNode.getOutgoing().remove(flow);
				rightNode.getIncoming().remove(flow);
				ModelElementInstance parent = flow.getParentElement();
				parent.removeChildElement(flow);
				SequenceFlow flowLeft = model.newInstance(SequenceFlow.class);
				SequenceFlow flowRight = model.newInstance(SequenceFlow.class);
				parent.addChildElement(flowLeft);
				parent.addChildElement(flowRight);
				leftNode.getOutgoing().add(flowLeft);
				userTask.getIncoming().add(flowLeft);
				userTask.getOutgoing().add(flowRight);
				rightNode.getIncoming().add(flowRight);
			}
		}
		return model;
	}

}
