package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.Collection;
import java.util.Optional;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNAddStrategyParallelGateway implements BPMNAddStrategy {

	@Override
	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity) {
		Collection<ParallelGateway> flows = model.getModelElementsByType(ParallelGateway.class);
		Optional<ParallelGateway> gateway = flows.stream().filter(gate -> 
			{return gate.getAttributeValue("gatewayDirection").equals("Diverging");}).findAny();
		if(gateway.isPresent()) {
			ParallelGateway gateLeft = gateway.get();
			ParallelGateway gateRight = null;
			Optional<ParallelGateway> gateOptional = getRightGateway(model, gateLeft);
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
	
	private Optional<ParallelGateway> getRightGateway(BpmnModelInstance model, ParallelGateway gateLeft) {
		for(SequenceFlow flow : gateLeft.getOutgoing()) {
			FlowNode node = flow.getTarget();
			FlowNode target = node.getOutgoing().iterator().next().getTarget();
			if(target instanceof ParallelGateway) {
				ParallelGateway gateRight = (ParallelGateway) target;
				if(gateRight.getAttributeValue("gatewayDirection").equals("Diverging"))
					return Optional.of(gateRight);
			}
		}
		return Optional.empty();
	}

}
