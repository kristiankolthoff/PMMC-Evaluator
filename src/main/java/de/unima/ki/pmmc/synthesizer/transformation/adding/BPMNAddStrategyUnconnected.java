package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNAddStrategyUnconnected implements BPMNAddStrategy{

	@Override
	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity) {
		UserTask userTask = model.newInstance(UserTask.class);
		userTask.setName(activity.getLabel());
		Collection<Task> tasks = model.getModelElementsByType(Task.class);
		if(tasks.iterator().hasNext()) {
			ModelElementInstance parent = tasks.iterator().next().getParentElement();
			parent.addChildElement(userTask);
		}
		return model;
	}

}
