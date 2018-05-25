Input-Model : Cologne.bpmn

Testcase : Tests if the matching system is able to ignore irrelevant correspondences
           (e.g. simply added from a totally different process model describing a 
           different scenario), and if the matcher identify label modifications 
           accordingly
	   
Transformations: .addIrrelevant(new BPMNAddStrategyUnconnected(), 
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