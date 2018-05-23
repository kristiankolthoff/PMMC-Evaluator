package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

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
		Stack<FlowNode> stack = new Stack<>();
		stack.addAll(transformSequenceFlowsToFlowNodes(gateLeft));
		while(!stack.isEmpty()) {
			FlowNode flowNode = stack.pop();
			if(flowNode instanceof ParallelGateway) {
				ParallelGateway gateRight = (ParallelGateway) flowNode;
				if(gateRight.getAttributeValue("gatewayDirection").equals("Converging"))
					return Optional.of(gateRight);
			}
			stack.addAll(transformSequenceFlowsToFlowNodes(flowNode));
		}
		return Optional.empty();
		
	}
	
	private List<FlowNode> transformSequenceFlowsToFlowNodes(FlowNode flowNode) {
		return flowNode.getOutgoing().stream().map(flow -> 
				{return flow.getTarget();}).collect(Collectors.toList());
	}

}
