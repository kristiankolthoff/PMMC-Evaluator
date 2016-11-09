package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;
import edu.mit.jwi.item.POS;

public class CTMatcherDVI implements CTMatcher {

	private static final CorrespondenceType TYPE = CorrespondenceType.DIFFICULT_VERB_IDENT;
	private static final boolean USE_POS = true;
	
	@Override
	public CorrespondenceType match(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel(label1);
		label1 = NLPHelper.getStemmedString(label1, USE_POS);
		label2 = NLPHelper.getSanitizeLabel(label2);
		label2 = NLPHelper.getStemmedString(label2, USE_POS);
		List<String> tokens1 = NLPHelper.getTokens(label1);
		List<String> tokens2 = NLPHelper.getTokens(label2);
		List<String> sameTokens = new ArrayList<>();
		for(String s1 : tokens1) {
			for(String s2 : tokens2) {
				if(s1.equals(s2)) {
					sameTokens.add(s1);
				}
			}
		}
		//TODO hack, needs to be improved
		if(sameTokens.size() > 1) {
			return CorrespondenceType.DEFAULT;
		}
		for(String token : sameTokens) {
			Set<POS> pos = NLPHelper.getPOS(token);
			if(pos.contains(POS.VERB)) {
				return TYPE;
			}
		}
		return CorrespondenceType.DEFAULT;
	}

	
}
