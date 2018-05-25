Input-Model : Cologne.bpmn

Testcase : Tests if the matching system is able to identify 1:m correspondences
           (same process model, however, some activities are more fine-grained
           described by replacing them with corresponding parallel activities)
	   
Transformations: //Apply online -> (Fill out online application form, ...)
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