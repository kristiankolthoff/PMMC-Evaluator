package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;
import edu.mit.jwi.item.POS;

public class CTMatcherOWI implements CTMatcher{

	private static final CorrespondenceType TYPE = CorrespondenceType.ONE_WORD_IDENT;
	
	@Override
	public CorrespondenceType match(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel(label1);
		label2 = NLPHelper.getSanitizeLabel(label2);
		//TODO needs to normalize
		List<String> label1Tokens = NLPHelper.getTokens(label1);
		List<String> label2Tokens = NLPHelper.getTokens(label2);
		List<String> sameTokens = new ArrayList<>();
		for(String s1 : label1Tokens) {
			for(String s2 : label2Tokens) {
				if(s1.equals(s2)) {
					sameTokens.add(s1);
				}
			}
		}
		if(sameTokens.size() > 1) {
			return CorrespondenceType.DEFAULT;
		}
		Set<POS> pos = new HashSet<>();
		for(String token : sameTokens) {
			pos.addAll(NLPHelper.getPOS(token));
		}
		if(!pos.isEmpty() && !pos.contains(POS.VERB)) {
			return TYPE;
		}
		return CorrespondenceType.DEFAULT;
	}

}
