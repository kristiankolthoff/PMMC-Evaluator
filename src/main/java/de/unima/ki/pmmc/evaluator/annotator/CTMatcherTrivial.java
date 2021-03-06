package de.unima.ki.pmmc.evaluator.annotator;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;

public class CTMatcherTrivial implements CTMatcher {

	private static final CorrespondenceType TYPE = CorrespondenceType.TRIVIAL;
	private static final boolean USE_POS = true;
	
	@Override
	public CorrespondenceType match(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel(label1);
		label1 = NLPHelper.getStemmedString(label1, USE_POS);
		label2 = NLPHelper.getSanitizeLabel(label2);
//		try
//		{
			label2 = NLPHelper.getStemmedString(label2, USE_POS);
//		} catch(StringIndexOutOfBoundsException e) {
//			System.out.println();
//		}
		return label1.equals(label2) ? TYPE : CorrespondenceType.DEFAULT;
	}

}
