package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;
import edu.stanford.nlp.ling.TaggedWord;

public class CTMatcherSubNoun implements CTMatcher{

//	private static final CorrespondenceType TYPE = CorrespondenceType.DIFFICULT_SUB_NOUN;
	private static final boolean USE_POS = false;
	
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
					sameWords.add(Pair.of(w1, w2));
				}
			}
		}
		if(!sameWords.isEmpty()) {
			for(Pair<TaggedWord, TaggedWord> pair : sameWords) {
				if(!NLPHelper.isPennTreebankVerbTag(pair.getKey().tag()) 
						|| !NLPHelper.isPennTreebankVerbTag(pair.getValue().tag())) {
					return CorrespondenceType.DEFAULT;
				}
			}
		} else {
			return CorrespondenceType.DEFAULT;
		}
		return null;
	}


}
