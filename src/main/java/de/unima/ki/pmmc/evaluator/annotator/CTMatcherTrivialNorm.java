package de.unima.ki.pmmc.evaluator.annotator;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;

public class CTMatcherTrivialNorm implements CTMatcher{

	private static final CorrespondenceType TYPE = CorrespondenceType.TRIVIAL_NORM;
	private static final boolean USE_POS = true;
	
	@Override
	public CorrespondenceType match(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel(label1);
		label1 = NLPHelper.getStemmedString(label1, USE_POS);
		label2 = NLPHelper.getSanitizeLabel(label2);
		label2 = NLPHelper.getStemmedString(label2, USE_POS);
		return label1.equals(label2) ? TYPE : CorrespondenceType.DEFAULT;
	}

}
