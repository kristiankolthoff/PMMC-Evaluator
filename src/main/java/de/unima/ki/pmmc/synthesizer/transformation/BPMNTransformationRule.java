package de.unima.ki.pmmc.synthesizer.transformation;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

@FunctionalInterface
public interface BPMNTransformationRule extends TransformationRule {

	public BpmnModelInstance transform(BpmnModelInstance model);
}
