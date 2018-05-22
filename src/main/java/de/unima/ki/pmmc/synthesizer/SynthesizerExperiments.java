package de.unima.ki.pmmc.synthesizer;

import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.synthesizer.transformation.BPMNTransformer;

public class SynthesizerExperiments {

	public static final String INS_TEST_PATH =  "src/main/resources/data/dataset1/synthesizer/1ab_tests_insert/";
	public static final String TRANSFORM_TEST_PATH = "src/main/resources/data/dataset1/synthesizer/7_tests_manipulate";
	public static final String SYNONYMS_TEST_PATH = "src/main/resources/data/dataset1/synthesizer/6_tests_synonyms";
	public static final String ONE_2_MANY_PATH = INS_TEST_PATH + "one2many.bpmn";
	public static final String MANY_2_ONE_PATH = INS_TEST_PATH + "many2one.bpmn";
	
	
	public static void main(String[] args) throws AlignmentException {
		SynthesizerExperiments.runOne2ManyExperiment();
		SynthesizerExperiments.runMany2OneExperiment();
		SynthesizerExperiments.runTransformExperiment();
		SynthesizerExperiments.runSynonymsExperiemnt();
	}
	
	private static void runOne2ManyExperiment() throws AlignmentException {
		Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
		synthesizer.readModel(ONE_2_MANY_PATH, "one2many")
				   .one2ManyParallel("Task_0et9ryz", "Upload necessary documents", 
						   							 "Upload cv", 
						   							 "Fill out questionaire")
				   .one2ManyParallel("Task_0w1pt40", "Write final exams",
						   							 "Search for programs")
				   .finished(INS_TEST_PATH);
	}
	
	private static void runMany2OneExperiment() throws AlignmentException {
		Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
		synthesizer.readModel(MANY_2_ONE_PATH, "many2One")
			       .many2OneParallel("Apply online", "Task_0et9ryz", "Task_06istns", "Task_1yn0bki")
			       .finished(INS_TEST_PATH);
	}
	
	private static void runTransformExperiment() throws AlignmentException {
		Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
		synthesizer.readModel(TRANSFORM_TEST_PATH + "/manipulate.bpmn", "manipulate")
			       .transformActivity("Task_0et9ryz", "Apply via online")
				   .transformActivity("Task_0w1pt40", "Graduate from school")
		           .finished(TRANSFORM_TEST_PATH);
	}
	
	private static void runSynonymsExperiemnt() throws AlignmentException {
		Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
		synthesizer.readModel(SYNONYMS_TEST_PATH + "/synonyms.bpmn", "synonyms")
			       .replaceAllSynonyms("graduate", "exmatriculate")
			       .replaceAllSynonyms("do", "make", "try")
			       .finished(SYNONYMS_TEST_PATH);
	}
}