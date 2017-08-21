//package de.unima.ki.pmmc.evaluator.metrics;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.ecs.storage.Array;
//import org.junit.Before;
//import org.junit.Test;
//
//import de.unima.ki.pmmc.evaluator.alignment.Alignment;
//import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
//import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
//import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
//
//
//
//public class CharacteristicTest {
//
//	private Characteristic characteristic;
//	private Characteristic characteristic2;
//	private Characteristic characteristic3;
//	private List<Characteristic> characteristicsZero;
//	private List<Characteristic> characteristics;
//	
//	private static final double ALLOWED_DEV = 0.001;
//	private static final boolean NORMALIZE = true;
//	
//	@Before
//	public void init() throws CorrespondenceException {
//		this.characteristic = new Characteristic(new Alignment(), new Alignment());
//		this.characteristicsZero = new ArrayList<>();
//		this.characteristicsZero.add(characteristic);
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 1.0, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1.0, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.42, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.48, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.6, CorrespondenceType.DEFAULT));
//		this.characteristic2 = new Characteristic(matcher, reference);
//		this.characteristic3 = new Characteristic(matcher, reference);
//		this.characteristics = new ArrayList<Characteristic>();
//		this.characteristics.add(this.characteristic2);
//		this.characteristics.add(this.characteristic3);
//	}
//	
//	@Test
//	public void zeroRulesMatcherPrecisionTest() {
//		assertTrue(Double.isNaN(this.characteristic.getPrecision()));
//	}
//	
//	@Test
//	public void zeroRulesGoldRecallTest() {
//		assertTrue(Double.isNaN(this.characteristic.getRecall()));
//	}
//	
//	@Test
//	public void zeroPrecisionAndRecallFMeasureTest() {
//		assertTrue(Double.isNaN(this.characteristic.getFMeasure()));
//	}
//	
//	
//	@Test
//	public void computeZeroPrecisionAndRecallFMeasureTest() {
//		assertEquals(0, Characteristic.computeFFromPR(0, 0), 0);
//	}
//	
//	@Test
//	public void getFMeasureTest() {
//		assertEquals(4/(double)7, this.characteristic2.getFMeasure(), ALLOWED_DEV);
//		assertEquals(4/(double)7, this.characteristic3.getFMeasure(), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getPrecisionTypedTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 1, CorrespondenceType.TRIVIAL));
//		reference.add(new Correspondence("2", "2", 1, CorrespondenceType.TRIVIAL));
//		reference.add(new Correspondence("6", "6", 1, CorrespondenceType.TRIVIAL));
//		reference.add(new Correspondence("3", "3", 1, CorrespondenceType.MISC));
//		reference.add(new Correspondence("7", "7", 1, CorrespondenceType.ONE_WORD_IDENT));
//		reference.add(new Correspondence("4", "4", 1, CorrespondenceType.DIFFICULT_NO_WORD_IDENT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.42, CorrespondenceType.TRIVIAL));
//		matcher.add(new Correspondence("2", "2", 0.48, CorrespondenceType.TRIVIAL));
//		matcher.add(new Correspondence("3", "3", 0.58, CorrespondenceType.MISC));
//		matcher.add(new Correspondence("4", "4", 0.6, CorrespondenceType.DIFFICULT_NO_WORD_IDENT));
//		matcher.add(new Correspondence("9", "9", 0.6, CorrespondenceType.DIFFICULT_NO_WORD_IDENT));
//		Characteristic c = new Characteristic(matcher, reference);
//		assertEquals(1, c.getPrecision(CorrespondenceType.TRIVIAL), ALLOWED_DEV);
//		assertEquals(0, c.getPrecision(CorrespondenceType.ONE_WORD_IDENT), ALLOWED_DEV);
//		assertEquals(1/2d, c.getPrecision(CorrespondenceType.DIFFICULT_NO_WORD_IDENT), ALLOWED_DEV);
//		assertEquals(3/4d, c.getPrecision(CorrespondenceType.DIFFICULT_NO_WORD_IDENT, 
//				CorrespondenceType.TRIVIAL), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRecallTypedTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 1, CorrespondenceType.TRIVIAL));
//		reference.add(new Correspondence("2", "2", 1, CorrespondenceType.TRIVIAL));
//		reference.add(new Correspondence("6", "6", 1, CorrespondenceType.TRIVIAL));
//		reference.add(new Correspondence("3", "3", 1, CorrespondenceType.MISC));
//		reference.add(new Correspondence("4", "4", 1, CorrespondenceType.DIFFICULT_NO_WORD_IDENT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.42, CorrespondenceType.TRIVIAL));
//		matcher.add(new Correspondence("2", "2", 0.48, CorrespondenceType.TRIVIAL));
//		matcher.add(new Correspondence("3", "3", 0.58, CorrespondenceType.MISC));
//		matcher.add(new Correspondence("4", "4", 0.6, CorrespondenceType.DIFFICULT_NO_WORD_IDENT));
//		Characteristic c = new Characteristic(matcher, reference);
//		assertEquals(2/3d, c.getRecall(CorrespondenceType.TRIVIAL), ALLOWED_DEV);
//	}
//	
//
//	@Test
//	public void getCorrelationNoZerosTest() throws CorrespondenceException {
//		final boolean ALLOW_ZEROS = false;
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.13, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 1.0, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.42, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.48, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.58, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.6, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		assertEquals(0.9740, c.getCorrelation(ALLOW_ZEROS), ALLOWED_DEV);
//		assertEquals(0.9740, c.getCorrelation(!ALLOW_ZEROS), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getCorrelationNoZerosTest2() throws CorrespondenceException {
//		final boolean ALLOW_ZEROS = false;
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.13, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 1.0, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 1.00, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.6, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.8, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.5, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		assertEquals(-0.6002, c.getCorrelation(ALLOW_ZEROS), ALLOWED_DEV);
//		assertEquals(-0.6002, c.getCorrelation(!ALLOW_ZEROS), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getCorrelationNoZerosTest3() throws CorrespondenceException {
//		final boolean ALLOW_ZEROS = false;
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.13, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 1.0, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.5, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.6, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.8, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 1.0, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("6", "6", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		assertEquals(0.9894, c.getCorrelation(ALLOW_ZEROS), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getCorrelationZerosTest() throws CorrespondenceException {
//		final boolean ALLOW_ZEROS = true;
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.13, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 1.0, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.42, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.48, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.58, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.6, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 0.71, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		assertEquals(0.1094, c.getCorrelation(ALLOW_ZEROS), ALLOWED_DEV);
//		assertEquals(0.9740, c.getCorrelation(!ALLOW_ZEROS), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRelativeDistanceSLM10Test() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("3", "3", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("6", "6", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(1.00100708, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRelativeDistanceSLM11Test() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("3", "3", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(0.00100708, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRelativeDistanceFLMEqualCorresTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.08, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.10, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.22, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.7, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(0.279, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
//	
//	@Test
//	public void getRelativeDistanceFLMNonEqualCorresMatcherTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.08, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.10, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.22, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.7, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(1.279, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//		matcher.add(new Correspondence("6", "6",0.9));
//		Characteristic c1 = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics1 = new ArrayList<>();
//		characteristics1.add(c1);
//		assertEquals(2.089, Characteristic.getRelativeDistance(characteristics1, NORMALIZE), ALLOWED_DEV + 0.01);
//	}
//	
//	@Test
//	public void getRelativeDistanceFLMNonEqualCorresRefTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("6", "6", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("7", "7", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.08, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.10, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.22, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.7, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("7", "7", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(1.81, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getNormalizationFLMTest1() {
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.08, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.31, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.22, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 0.35, CorrespondenceType.DEFAULT));
//		List<Alignment> alignments = new ArrayList<>();
//		alignments.add(matcher);
//		Alignment normAlignment = Characteristic.getNormalizedAlignments(alignments).get(0);
//		assertEquals(0.125, normAlignment.get(0).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.189, normAlignment.get(1).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.870, normAlignment.get(2).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.579, normAlignment.get(3).getConfidence(), ALLOWED_DEV);
//		assertEquals(1, normAlignment.get(4).getConfidence(), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getNormalizationFLMTest2() {
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.33, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.80, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.96, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("6", "6", 1, CorrespondenceType.DEFAULT));
//		List<Alignment> alignments = new ArrayList<>();
//		alignments.add(matcher);
//		Alignment normAlignment = Characteristic.getNormalizedAlignments(alignments).get(0);
//		assertEquals(0.125, normAlignment.get(0).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.739, normAlignment.get(1).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.948, normAlignment.get(2).getConfidence(), ALLOWED_DEV);
//		assertEquals(1, normAlignment.get(3).getConfidence(), ALLOWED_DEV);
//		assertEquals(1, normAlignment.get(4).getConfidence(), ALLOWED_DEV);
//		assertEquals(1, normAlignment.get(5).getConfidence(), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getNormalizationFLMTest3() {
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.7, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.75, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.80, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.86, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("6", "6", 0.97, CorrespondenceType.DEFAULT));
//		List<Alignment> alignments = new ArrayList<>();
//		alignments.add(matcher);
//		Alignment normAlignment = Characteristic.getNormalizedAlignments(alignments).get(0);
//		assertEquals(0.125, normAlignment.get(0).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.271, normAlignment.get(1).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.417, normAlignment.get(2).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.592, normAlignment.get(3).getConfidence(), ALLOWED_DEV);
//		assertEquals(1, normAlignment.get(4).getConfidence(), ALLOWED_DEV);
//		assertEquals(0.913, normAlignment.get(5).getConfidence(), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRelativeDistanceFLM6Test() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("1", "1", 0.08, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("2", "2", 0.1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("3", "3", 0.31, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 0.22, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 0.35, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(0.106, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRelativeDistanceSLMTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("3", "3", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("6", "6", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(1.001007, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
//	@Test
//	public void getRelativeDistanceSLM2Test() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		reference.add(new Correspondence("1", "1", 0.125, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("2", "2", 0.25, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("3", "3", 0.75, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("4", "4", 0.875, CorrespondenceType.DEFAULT));
//		reference.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		final Alignment matcher = new Alignment();
//		matcher.add(new Correspondence("3", "3", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("4", "4", 1, CorrespondenceType.DEFAULT));
//		matcher.add(new Correspondence("5", "5", 1, CorrespondenceType.DEFAULT));
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(c);
//		assertEquals(0.001007, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//
//	@Test
//	public void getRelativeDistanceZeroInputTest() throws CorrespondenceException {
//		final Alignment reference = new Alignment();
//		final Alignment matcher = new Alignment();
//		Characteristic c = new Characteristic(matcher, reference);
//		List<Characteristic> characteristics = new ArrayList<>();
//		assertEquals(0, Characteristic.getRelativeDistance(characteristics, NORMALIZE), ALLOWED_DEV);
//	}
//	
////	@Test
////	public void getSpearmanRangCorrTest() {
////		final Alignment reference = new Alignment();
////		reference.add(new Correspondence("1", "1", 0));
////		reference.add(new Correspondence("2", "2", 0));
////		reference.add(new Correspondence("3", "3", 0.125));
////		reference.add(new Correspondence("4", "4", 0.125));
////		reference.add(new Correspondence("5", "5", 0.125));
////		reference.add(new Correspondence("6", "6", 0.125));
////		reference.add(new Correspondence("7", "7", 0.25));
////		reference.add(new Correspondence("8", "8", 0.375));
////		reference.add(new Correspondence("9", "9", 0.500));
////		reference.add(new Correspondence("10", "10", 0.625));
////		reference.add(new Correspondence("11", "11", 0.750));
////		reference.add(new Correspondence("12", "12", 0.875));
////		reference.add(new Correspondence("13", "13", 1));
////		reference.add(new Correspondence("14", "14", 1));
////		reference.add(new Correspondence("15", "15", 1));
////		final Alignment matcher = new Alignment();
////		matcher.add(new Correspondence("1", "1", 1));
////		matcher.add(new Correspondence("2", "2", 1));
////		matcher.add(new Correspondence("3", "3", 1));
////		matcher.add(new Correspondence("4", "4", 0));
////		matcher.add(new Correspondence("5", "5", 0));
////		matcher.add(new Correspondence("6", "6", 1));
////		matcher.add(new Correspondence("7", "7", 1));
////		matcher.add(new Correspondence("8", "8", 1));
////		matcher.add(new Correspondence("9", "9", 0));
////		matcher.add(new Correspondence("10", "10", 0));
////		matcher.add(new Correspondence("11", "11", 0));
////		matcher.add(new Correspondence("12", "12", 0));
////		matcher.add(new Correspondence("13", "13", 0));
////		matcher.add(new Correspondence("14", "14", 1));
////		matcher.add(new Correspondence("15", "15", 1));
////		Characteristic c = new Characteristic(matcher, reference);
////		List<Characteristic> cs = new ArrayList<>();
////		cs.add(c);
////		assertEquals(-0.2821, Characteristic.getSpearRangCorrMacro(cs), ALLOWED_DEV);
////	}
//	
////	@Test
////	public void getSpearmanRangCorr2Test() throws CorrespondenceException {
////		final Alignment reference = new Alignment();
////		reference.add(new Correspondence("3", "3", 0.125));
////		reference.add(new Correspondence("4", "4", 0.125));
////		reference.add(new Correspondence("5", "5", 0.125));
////		reference.add(new Correspondence("6", "6", 0.125));
////		reference.add(new Correspondence("7", "7", 0.25));
////		reference.add(new Correspondence("8", "8", 0.375));
////		reference.add(new Correspondence("9", "9", 0.500));
////		reference.add(new Correspondence("10", "10", 0.625));
////		reference.add(new Correspondence("11", "11", 0.750));
////		reference.add(new Correspondence("12", "12", 0.875));
////		reference.add(new Correspondence("13", "13", 1));
////		reference.add(new Correspondence("14", "14", 1));
////		reference.add(new Correspondence("15", "15", 1));
////		final Alignment matcher = new Alignment();
////		matcher.add(new Correspondence("1", "1", 1));
////		matcher.add(new Correspondence("2", "2", 1));
////		matcher.add(new Correspondence("3", "3", 1));
////		matcher.add(new Correspondence("6", "6", 1));
////		matcher.add(new Correspondence("7", "7", 1));
////		matcher.add(new Correspondence("8", "8", 1));
////		matcher.add(new Correspondence("14", "14", 1));
////		matcher.add(new Correspondence("15", "15", 1));
////		Characteristic c = new Characteristic(matcher, reference);
////		List<Characteristic> cs = new ArrayList<>();
////		cs.add(c);
////		assertEquals(-0.2821, Characteristic.getSpearRangCorrMacro(cs), ALLOWED_DEV);
////	}
////	
////	@Test
////	public void getSpearmanRangCorr3Test() throws CorrespondenceException {
////		final Alignment reference = new Alignment();
////		reference.add(new Correspondence("3", "3", 0.125));
////		reference.add(new Correspondence("4", "4", 0.125));
////		reference.add(new Correspondence("5", "5", 0.125));
////		reference.add(new Correspondence("6", "6", 0.125));
////		reference.add(new Correspondence("7", "7", 0.25));
////		reference.add(new Correspondence("8", "8", 0.375));
////		reference.add(new Correspondence("9", "9", 0.500));
////		reference.add(new Correspondence("10", "10", 0.625));
////		reference.add(new Correspondence("11", "11", 0.750));
////		reference.add(new Correspondence("12", "12", 0.875));
////		reference.add(new Correspondence("13", "13", 1));
////		reference.add(new Correspondence("14", "14", 1));
////		reference.add(new Correspondence("15", "15", 1));
////		final Alignment matcher = new Alignment();
////		matcher.add(new Correspondence("1", "1", 0.5));
////		matcher.add(new Correspondence("2", "2", 0.6));
////		matcher.add(new Correspondence("3", "3", 0.25));
////		matcher.add(new Correspondence("6", "6", 0.94));
////		matcher.add(new Correspondence("7", "7", 0.1));
////		matcher.add(new Correspondence("8", "8", 0.2));
////		matcher.add(new Correspondence("14", "14", 0.3));
////		matcher.add(new Correspondence("15", "15", 0.9));
////		Characteristic c = new Characteristic(matcher, reference);
////		List<Characteristic> cs = new ArrayList<>();
////		cs.add(c);
////		assertEquals(-0.2821, Characteristic.getSpearRangCorrMacro(cs), ALLOWED_DEV);
////	}
//	
//}
