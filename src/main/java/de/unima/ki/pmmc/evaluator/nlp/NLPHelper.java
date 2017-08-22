package de.unima.ki.pmmc.evaluator.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import de.unima.ki.pmmc.evaluator.utils.Utils;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class NLPHelper {
	
	private static IRAMDictionary dict;
	private static HashSet<String> stopwords;
	private static MaxentTagger tagger;
	private static POSTaggerME posTagger;
	
	public static final String WORDNET_DIRECTORY = "src/main/resources/libs/wordnet/dict";
	public static final String TAGGER_BIDIR_DIRECTORY = "src/main/resources/libs/tagger/english-bidirectional-distsim.tagger";
	public static final String TAGGER_LEFT_DIRECTORY = "src/main/resources/libs/tagger/english-left3words-distsim.tagger";
	public static final String POS_TAGGER_MAXENT = "src/main/resources/libs/tagger/en-pos-maxent.bin";
	public static final String POS_TAGGER_PERCEPTRON = "src/main/resources/libs/tagger/en-pos-perceptron.bin";
	private static final String[] STOP_WORDS;
	
	static {
		dict = new RAMDictionary(new File(WORDNET_DIRECTORY) , ILoadPolicy.NO_LOAD); 
		try {
			dict.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		STOP_WORDS = new String[]{
				"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot",
				"could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however",
				"i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off",
				"often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them",
				"then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why",
				"will","with","would","yet","you","your"};
		stopwords = new HashSet<String>();
		stopwords.addAll(Arrays.asList(STOP_WORDS));
	}
	

	private NLPHelper() {
		
	}
	
	public static Set<POS> getPOS(String w) {
		IIndexWord indexWord;
		Set<ISynset> synsets;
		Set<POS> pos = new HashSet<POS>();
		POS[] all = new POS[]{POS.NOUN, POS.VERB, POS.ADVERB, POS.ADJECTIVE};
		for (POS p : all) {
			indexWord = dict.getIndexWord(w, p);
			synsets = getSynsetsByIndexWord(indexWord);
			if (synsets.size() > 0) {
				pos.add(p);
			}
		}
		// do some basic stemming for verbs
		if (w.endsWith("ing") && !pos.contains(POS.VERB)) {
			String w1 = w.substring(0, w.length() - 3);
			String w2 = w1 + "e";
			indexWord = dict.getIndexWord(w1, POS.VERB);
			synsets = getSynsetsByIndexWord(indexWord);
			if (synsets.size() > 0) {
				pos.add(POS.VERB);
			}
			indexWord = dict.getIndexWord(w2, POS.VERB);
			synsets = getSynsetsByIndexWord(indexWord);
			if (synsets.size() > 0) {
				pos.add(POS.VERB);
			}
			if (w1.endsWith("nn") || w1.endsWith("tt")) {
				String w3 = w1.substring(0, w1.length() - 1);
				indexWord = dict.getIndexWord(w3, POS.VERB);
				synsets = getSynsetsByIndexWord(indexWord);
				if (synsets.size() > 0) {
					pos.add(POS.VERB);
				}
			}
		}
		// do some basic stemming for plural nouns
		if (w.endsWith("s") && !pos.contains(POS.NOUN)) {
			String w1 = w.substring(0, w.length() - 1);
			indexWord = dict.getIndexWord(w1, POS.NOUN);
			synsets = getSynsetsByIndexWord(indexWord);
			if (synsets.size() > 0) {
				pos.add(POS.NOUN);
			}
		}
		return pos;
	}
	
	public static List<TaggedWord> getPOSSentence(String sentence) {
		sentence = getSanitizeLabel(sentence);
		String[] words = sentence.split(" ");
		List<Word> taggedWords = new ArrayList<>();
		for (int i = 0; i < words.length; i++) {
			taggedWords.add(new Word(words[i]));
		}
		return getMaxentTagger().tagSentence(taggedWords);
	}
	
	public static String getNormalized(String w, POS p) {
		IIndexWord indexWord;
		Set<ISynset> synsets;
		if (p == POS.ADVERB) {
			return w;
		}
		if (p == POS.ADJECTIVE) {
			return w;
		}
		if (p == POS.NOUN) {
			indexWord = dict.getIndexWord(w, POS.NOUN);
			synsets = getSynsetsByIndexWord(indexWord);
			if (synsets.size() == 0 && w.endsWith("s")) {
				return w.substring(0, w.length()-1);
			}			
			else {
				return w;
			}
		}
		if (p == POS.VERB) {
			indexWord = dict.getIndexWord(w, POS.VERB);
			synsets = getSynsetsByIndexWord(indexWord);
			if (synsets.size() > 0) {
				return w;
			}
			if (w.endsWith("ing")) {
				String w1 = w.substring(0, w.length() - 3);
				String w2 = w1 + "e";
				indexWord = dict.getIndexWord(w1, POS.VERB);
				synsets = getSynsetsByIndexWord(indexWord);
				if (synsets.size() > 0) return w1;
				indexWord = dict.getIndexWord(w2, POS.VERB);
				synsets = getSynsetsByIndexWord(indexWord);		
				if (synsets.size() > 0) return w1;
				
			}			
			else {
				return w;
			}			
		}
		return w;
	}
	
		
	public static boolean isStopword(String w) {
		return stopwords.contains(w);
	}
	
	public static void showSenses(String w, POS p) {

		IIndexWord indexWord = dict.getIndexWord(w, p);
		Set<ISynset> synsets = getSynsetsByIndexWord(indexWord);
		for (ISynset synset : synsets) {
			System.out.println(synset);
		}
	}
	
	
	private static Set<ISynset> getSynsetsByIndexWord(IIndexWord indexWord) {
		Set<ISynset> synsets = new HashSet<ISynset>();
		if (indexWord != null && indexWord.getWordIDs() != null) {
			for (IWordID wordId : indexWord.getWordIDs()) {
				ISynsetID synsetid = wordId.getSynsetID();
				ISynset synset = dict.getSynset(synsetid);
				synsets.add(synset);
			}
		}
		return synsets;
	}
	
	public POS getWordType() {
		
		
		return POS.ADJECTIVE;
	}
	
	public static boolean isPennTreebankVerbTag(String t) {
		return t.equals("VB") || t.equals("VBZ") || t.equals("VBD") || t.equals("VBG")
				|| t.equals("VBN") || t.equals("VBP");
	}
	
	public static String getTokenizedString(String sentence) {
		PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(sentence), 
				new CoreLabelTokenFactory(), "");
		List<CoreLabel> tokens = tokenizer.tokenize();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i).originalText());
			if(i!=tokens.size()-1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	public static String getStemmedString(String sentence, boolean usePOS) {
		return getStemmedTokens(sentence, usePOS)
				.stream()
				.reduce("", (s,t) -> (s + " " + t))
				.trim();
	}
	
	public static List<String> getTokens(String sentence) {
		PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(sentence),
				new CoreLabelTokenFactory(), "");
		List<CoreLabel> tokens = tokenizer.tokenize();
		List<String> sTokens = new ArrayList<>();
		for(CoreLabel token : tokens) {
			sTokens.add(token.originalText());
		}
		return sTokens;
	}
	
	public static List<String> getStemmedTokens(String sentence, boolean usePOS) {
		List<String> tokens = getTokens(sentence);
		List<String> stemmedTokens = new ArrayList<>();
		for (int i = 0; i < tokens.size(); i++) {
			List<String> possibleStems = null;
			if(usePOS) {
				possibleStems = getWordStemWithPOS(tokens.get(i));				
			} else {
				possibleStems = getWordStem(tokens.get(i));
			}
			for (int j = 0; j < possibleStems.size(); j++) {
				if(Character.isUpperCase(tokens.get(i).charAt(0))) {
					possibleStems.set(j, WordUtils.capitalize(possibleStems.get(j)));
				}
			}
			if(possibleStems.size() > 1) {
				for(String stem : possibleStems) {
					if(!tokens.get(i).equals(stem)) {
						stemmedTokens.add(stem);
						break;
					}
				}				
			} else if(possibleStems.size() == 1){
				stemmedTokens.add(possibleStems.get(0));
			} else {
				stemmedTokens.add(tokens.get(i));
			}
		}
		return stemmedTokens;
	}
	
	public static List<String> getWordStem(String word) {
		WordnetStemmer ws = new WordnetStemmer(dict);
		return ws.findStems(word, null);
	}
	
	public static List<String> getWordStemWithPOS(String word) {
		WordnetStemmer ws = new WordnetStemmer(dict);
		Set<POS> pos = getPOS(word);
		for(POS p : pos) {
			if(p == POS.VERB) {
				return ws.findStems(word, p);
			}
		}
		if(pos.size() == 0) {
			return ws.findStems(word, null);
		} else {
			return ws.findStems(word, pos.iterator().next());			
		}
	}
	
	public static List<String> getWordStem(String word, POS pos) {
		WordnetStemmer ws = new WordnetStemmer(dict);
		return ws.findStems(word, pos);
	}
	
	public static Set<String> getNGrams(String sentence, int n) {
		Set<String> ngrams = new HashSet<>();
		sentence = getSanitizeLabel(sentence).replace(" ", "");
		for (int i = 0; i < sentence.length() - n + 1; i++) {
			ngrams.add(sentence.substring(i,i+n));
		}
		return ngrams;
	}
	
	public static double jaccardNGramSimilarity(String s1, String s2, int n) {
		Set<String> sameNgrams = getNGrams(s1, n);
		sameNgrams.retainAll(getNGrams(s2, n));
		Set<String> unionNgrams = getNGrams(s1, n);
		unionNgrams.addAll(getNGrams(s2, n));
		return sameNgrams.size() / (double) unionNgrams.size();
	}
	
	public static double jaccardSimilarity(String s1, String s2) {
		Set<String> sameTokens = Utils.toSet(getTokens(s1));
		sameTokens.retainAll(Utils.toSet(getTokens(s2)));
		Set<String> unionTokens = Utils.toSet(getTokens(s1));
		unionTokens.addAll(Utils.toSet(getTokens(s2)));
		return sameTokens.size() / (double) unionTokens.size();
	}
	
	public static String getSanitizeLabel(String label) {
		label = label.trim();
		label = label.toLowerCase();
		label = getTokenizedString(label);
		label = label.replace("\n", "");
		return label;
	}
	
	public static void main(String[] args) {
		String label = NLPHelper.getSanitizeLabel2("Receiving acceptance letter");
		List<TaggedWord> taggedWords = NLPHelper.getTaggedSentenceWithoutStopwords(label);
		for(TaggedWord t : taggedWords) {
			System.out.println(t.word() + " " + t.tag());
		}
//		System.out.println(NLPHelper.getSanitizeLabel2("Checking if complete"));
////		System.out.println(NLPHelper.identicalTokens("Apply at the university with documents", "Send application documents to the university"));
//		String[] sentence = new String[]{"Checking", "if", "complete"};
//		String[] posTags = getPOSTaggerME().tag(sentence);
//		for (int i = 0; i < posTags.length; i++) {
//			System.out.print(sentence[i] + "_" + posTags[i]);
//			System.out.println();
//		}
	}
	
	public static MaxentTagger getMaxentTagger() {
		if(tagger == null) {
			tagger = new MaxentTagger(TAGGER_BIDIR_DIRECTORY);
		}
		return tagger;
	}
	
	public static POSTaggerME getPOSTaggerME() {
		if(posTagger == null) {
			try {
			InputStream modelIn = new FileInputStream(POS_TAGGER_PERCEPTRON);
			POSModel model = new POSModel(modelIn);
			posTagger = new POSTaggerME(model);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return posTagger;
	}

	public static String getSanitizeLabel2(String label) {
		label = label.trim();
		label = getStemmedString(label, true);
		label = label.replace("\n", "");
		return label;
	}

	public static List<TaggedWord> getTaggedSentenceWithoutStopwords(String label) {
		String[] tokens = label.split(" ");
		List<Word> words = new ArrayList<>();
		for(String token : tokens) {
			words.add(new Word(token));
		}
		return getMaxentTagger().tagSentence(words)
				.stream()
				.map(taggedWord -> {taggedWord.setValue(getSanitizeLabel(taggedWord.value())); return taggedWord;})
				.filter(taggedWord -> {return !isStopword(taggedWord.value());})
				.collect(Collectors.toList());
	}

	public static Set<String> identicalTokens(String label1, String label2) {
		List<String> tokens1 = getTokens(label1);
		List<String> tokens2 = getTokens(label2);
		return tokens1.stream()
				.filter(token -> {return tokens2.contains(token);})
				.collect(Collectors.toSet());
	}

	public static String getStemmedStringWithoutStopWords(String label, boolean usePos) {
		label = getSanitizeLabel(label);
		return getStemmedTokens(label, usePos)
			.stream()
			.filter(token -> {return !isStopword(token);})
			.reduce((first, second) -> {return first + " " + second;})
			.get();
	}
	
}