package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.Collection;
import java.util.Optional;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNAddStrategyExclusiveGateway implements BPMNAddStrategy {

	@Override
	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity) {
		Collection<ExclusiveGateway> flows = model.getModelElementsByType(ExclusiveGateway.class);
		Optional<ExclusiveGateway> gateway = flows.stream().filter(gate -> 
			{return gate.getAttributeValue("gatewayDirection").equals("Diverging");}).findAny();
		if(gateway.isPresent()) {
			ExclusiveGateway gateLeft = gateway.get();
			ExclusiveGateway gateRight = null;
			Optional<ExclusiveGateway> gateOptional = getRightGateway(model, gateLeft);
			if(gateOptional.isPresent()) gateRight = gateOptional.get();
			else return model;
			//Add the new task to the parallel gateway
			ModelElementInstance parent = gateLeft.getParentElement();
			UserTask userTask = model.newInstance(UserTask.class);
			userTask.setName(activity.getLabel());
			SequenceFlow flowLeft = model.newInstance(SequenceFlow.class);
			SequenceFlow flowRight = model.newInstance(SequenceFlow.class);
			parent.addChildElement(userTask);
			parent.addChildElement(flowLeft);
			parent.addChildElement(flowRight);
			flowLeft.setSource(gateLeft);
			flowLeft.setTarget(userTask);
			flowRight.setSource(userTask);
			flowRight.setTarget(gateRight);
			gateLeft.getOutgoing().add(flowLeft);
			userTask.getIncoming().add(flowLeft);
			userTask.getOutgoing().add(flowRight);
			gateRight.getIncoming().add(flowRight);
		}
		return model;
	}
	
	private Optional<ExclusiveGateway> getRightGateway(BpmnModelInstance model, ExclusiveGateway gateLeft) {
		for(SequenceFlow flow : gateLeft.getOutgoing()) {
			FlowNode node = flow.getTarget();
			FlowNode target = node.getOutgoing().iterator().next().getTarget();
			if(target instanceof ExclusiveGateway) {
				ExclusiveGateway gateRight = (ExclusiveGateway) target;
				if(gateRight.getAttributeValue("gatewayDirection").equals("Converging"))
					return Optional.of(gateRight);
			}
		}
		return Optional.empty();
	}

}
