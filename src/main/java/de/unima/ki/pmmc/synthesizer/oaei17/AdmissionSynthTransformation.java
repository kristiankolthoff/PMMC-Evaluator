package de.unima.ki.pmmc.synthesizer.oaei17;


import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.synthesizer.Synthesizer;
import de.unima.ki.pmmc.synthesizer.transformation.BPMNTransformer;
import de.unima.ki.pmmc.synthesizer.transformation.Direction;
import de.unima.ki.pmmc.synthesizer.transformation.adding.BPMNAddStrategyParallelGateway;
import de.unima.ki.pmmc.synthesizer.transformation.adding.BPMNAddStrategyTaskSequential;
import de.unima.ki.pmmc.synthesizer.transformation.adding.BPMNAddStrategyUnconnected;

public class AdmissionSynthTransformation {
	
	public final static String MODEL_PATH = "src/main/resources/data/synthesized/";

	public static void main(String[] args) throws AlignmentException {
		AdmissionSynthTransformation.synthTransformationCase1();
		AdmissionSynthTransformation.synthTransformationCase2();
		AdmissionSynthTransformation.synthTransformationCase3();
	}
	
	private static void synthTransformationCase1() throws AlignmentException {
		Synthesizer synth = new Synthesizer(new BPMNTransformer());
		synth.readModel(MODEL_PATH + "case1/Cologne.bpmn", "data")
		     //Apply online -> (Fill out online application form, ...)
			 .one2ManyParallel("sid-B11703D1-08C3-416B-8947-76CE10A27978", 
					 		   "Fill out online application form",
					           "Upload CV",
					           "Upload language certificate (english)",
					           "Upload language certificate (german)",
					           "Upload motivation letter")
			 //Check documents -> (Check CV, ...)
			 .one2ManyParallel("sid-B41B5C8F-D1D5-4641-943B-DB396FD69D0B", 
					           "Check CV",
					           "Check language certificate (english)",
					           "Check language certificate (german)",
					           "Check motivation letter")
			 //Evaluate -> (Evaluate CV, ...)
			 .one2ManyParallel("sid-885F9233-8A3A-470C-90D9-FA7F483D69CA",
					 		   "Evaluate CV",
					 		   "Evaluate language certificate (english)",
					 		   "Evaluate language certificate (german)",
					 		   "Evaluate motivation letter")
			 .finished(MODEL_PATH + "/case1");
	}
	
	private static void synthTransformationCase2() throws AlignmentException {
		Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
		synthesizer.readModel(MODEL_PATH + "case2/Cologne.bpmn", "data")
				   .addIrrelevant(new BPMNAddStrategyUnconnected(), 
						   new Activity("id1", "Investigate birth certificate"),
						   new Activity("id2", "Fill out birth registration form"),
						   new Activity("id3", "Enter data into offline NADRA CRMS"))
				   .addIrrelevant(new BPMNAddStrategyTaskSequential(), 
						   new Activity("id4", "Visit UC and retrieve latest BR data on USB stick"),
						   new Activity("id5", "Upload data into NADRA central database"),
						   new Activity("id6", "Provide informant with paper receipt as proof of registration"))
				   .addIrrelevant(new BPMNAddStrategyParallelGateway(), 
						   new Activity("id7", "Travel to notify birth"),
						   new Activity("id8", "Provide details about birth to Sub-County Chief"),
						   new Activity("id9", "Check validation details and check short birth certifcate"))
				   //Invite to an aptitude test
				   .transformActivity("sid-257EB9F2-1203-4F54-8E2E-43FE6ABA096E", "Invite for aptitude test")
				   //Check documents
				   .transformActivity("sid-B41B5C8F-D1D5-4641-943B-DB396FD69D0B", "Validate documents")
				   //Rank students according to GPA and the test results
				   .transformActivity("sid-D4F6E283-16FB-458A-830A-03336924C1D2", "Rank students based on GPA and the conducted tests")
				   .replaceAllSynonyms("send", "deliver", "forward", "dispatch")
				   .finished(MODEL_PATH + "/case2");
				   
				   
	}
	
	private static void synthTransformationCase3() throws AlignmentException {
		Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
		synthesizer.readModel(MODEL_PATH + "/case3/Cologne.bpmn", "data")
				   .flip(Direction.VERTICAL)
				   .finished(MODEL_PATH + "/case3");
	}
}
