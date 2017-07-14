package de.unima.ki.pmmc.evaluator.annotator;

import java.util.Set;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;

public class CTMatcherMisc implements CTMatcher{

	private static final CorrespondenceType TYPE = CorrespondenceType.MISC;
	private static final boolean USE_POS = true;
	
	@Override
	public CorrespondenceType match(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel(label1);
		label1 = NLPHelper.getStemmedString(label1, USE_POS);
		label2 = NLPHelper.getSanitizeLabel(label2);
		label2 = NLPHelper.getStemmedString(label2, USE_POS);
		Set<String> words = NLPHelper.identicalTokens(label1, label2);
		if(NLPHelper.identicalTokens(label1, label2).size() >= 2) {
			return TYPE;
		} else {
			return CorrespondenceType.DEFAULT;
		}
	}

}
