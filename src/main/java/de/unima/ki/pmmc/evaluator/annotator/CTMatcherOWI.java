package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.TaggedWord;
import javafx.util.Pair;

public class CTMatcherOWI implements CTMatcher{

	private static final CorrespondenceType TYPE = CorrespondenceType.ONE_WORD_IDENT;
	private static final boolean USE_POS = true;
	
	//TODO refactor pair
	@Override
	public CorrespondenceType match(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel2(label1);
		label2 = NLPHelper.getSanitizeLabel2(label2);
		List<TaggedWord> words1 = NLPHelper.getTaggedSentenceWithoutStopwords(label1);
		List<TaggedWord> words2 = NLPHelper.getTaggedSentenceWithoutStopwords(label2);
		for (int i = 0; i < words1.size(); i++) {
			words1.get(i).setValue(NLPHelper.getStemmedString(words1.get(i).value(), USE_POS));
		}
		for (int i = 0; i < words2.size(); i++) {
			words2.get(i).setValue(NLPHelper.getStemmedString(words2.get(i).value(), USE_POS));
		}
		List<Pair<TaggedWord, TaggedWord>> sameWords = new ArrayList<>();
		for(TaggedWord w1 : words1) {
			for(TaggedWord w2 : words2) {
				if(w1.value().equals(w2.value())) {
					sameWords.add(new Pair<TaggedWord, TaggedWord>(w1, w2));
				}
			}
		}
		if(sameWords.size() == 1) {
			Pair<TaggedWord, TaggedWord> pair = sameWords.get(0);
			if(NLPHelper.isPennTreebankVerbTag(pair.getKey().tag()) 
					|| NLPHelper.isPennTreebankVerbTag(pair.getValue().tag())) {
				return CorrespondenceType.DEFAULT;
			} else {
				return TYPE;
			}
		}
		return CorrespondenceType.DEFAULT;
	}
	
	public CorrespondenceType test(String label1, String label2) {
		label1 = NLPHelper.getSanitizeLabel(label1);
		label1 = NLPHelper.getStemmedString(label1, USE_POS);
		label2 = NLPHelper.getSanitizeLabel(label2);
		label2 = NLPHelper.getStemmedString(label2, USE_POS);
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
