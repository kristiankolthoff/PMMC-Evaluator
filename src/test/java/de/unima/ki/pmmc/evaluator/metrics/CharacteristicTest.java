package de.unima.ki.pmmc.evaluator.metrics;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;



public class CharacteristicTest {

	private Characteristic characteristic;
	private Characteristic characteristic2;
	private Characteristic characteristic3;
	private List<Characteristic> characteristicsZero;
	private List<Characteristic> characteristics;
	
	@Before
	public void init() {
		this.characteristic = new Characteristic();
		this.characteristic2 = new Characteristic(100, 50, 25);
		this.characteristic3 = new Characteristic(10,5,5);
		this.characteristicsZero = new ArrayList<>();
		this.characteristicsZero.add(characteristic);
		this.characteristics = new ArrayList<Characteristic>();
		this.characteristics.add(this.characteristic2);
		this.characteristics.add(this.characteristic3);
	}
	
	@Test
	public void zeroRulesMatcherPrecisionTest() {
		assertTrue(Double.isNaN(this.characteristic.getPrecision()));
	}
	
	@Test
	public void zeroRulesGoldRecallTest() {
		assertTrue(Double.isNaN(this.characteristic.getRecall()));
	}
	
	@Test
	public void zeroPrecisionAndRecallFMeasureTest() {
		assertTrue(Double.isNaN(this.characteristic.getFMeasure()));
	}
	
	@Test
	public void computeZeroInputRecallTest() {
		assertTrue(Double.isNaN(Characteristic.computeRecall(0, 0)));
	}
	
	@Test
	public void computeZeroPrecisionAndRecallFMeasureTest() {
		assertEquals(0, Characteristic.computeFFromPR(0, 0), 0);
	}
	
	@Test
	public void getFMeasureTest() {
		assertEquals(1/(double)3, this.characteristic2.getFMeasure(), 0);
		assertEquals(2/(double)3, this.characteristic3.getFMeasure(), 0);
	}
	
	@Test
	public void getRecallMacroZeroTest() {
		assertTrue(Double.isNaN(Characteristic.getRecallMacro(characteristicsZero)));
	}
	
	@Test
	public void getRecallMacroTest() {
		assertEquals(0.375, Characteristic.getRecallMacro(this.characteristics),0);
	}
	
	@Test
	public void getRecallMicroTest() {
		assertEquals(30/(double)110, Characteristic.getRecallMicro(this.characteristics), 0);
	}
	
	@Test
	public void getPrecisionMacroTest() {
		assertEquals(0.75, Characteristic.getPrecisionMacro(this.characteristics), 0);
	}
	
	@Test
	public void getPrecisionMicroTest() {
		assertEquals(30/(double)55, Characteristic.getPrecisionMicro(this.characteristics), 0);
	}
	
	@Test
	public void getFMeasureMacroTest() {
		assertEquals(0.5, Characteristic.getFMeasureMacro(this.characteristics), 0);
	}
	
	@Test
	public void getFMeasureMicroTest() {
		assertEquals(Characteristic.computeFFromPR(30/(double)55, 30/(double)110), 
				Characteristic.getFMeasureMicro(this.characteristics), 0);
	}

	@Test
	public void getCorrelationNoZerosTest() {
		final boolean ALLOW_ZEROS = false;
		final Alignment reference = new Alignment();
		reference.add(new Correspondence("1", "1", 0.13));
		reference.add(new Correspondence("2", "2", 0.25));
		reference.add(new Correspondence("3", "3", 0.75));
		reference.add(new Correspondence("4", "4", 1.0));
		final Alignment matcher = new Alignment();
		matcher.add(new Correspondence("1", "1", 0.42));
		matcher.add(new Correspondence("2", "2", 0.48));
		matcher.add(new Correspondence("3", "3", 0.58));
		matcher.add(new Correspondence("4", "4", 0.6));
		Characteristic c = new Characteristic(matcher, reference);
		assertEquals(0.9740, c.getCorrelation(ALLOW_ZEROS), 0.1);
		assertEquals(0.9740, c.getCorrelation(!ALLOW_ZEROS), 0.1);
	}
	
	@Test
	public void getCorrelationNoZerosTest2() {
		final boolean ALLOW_ZEROS = false;
		final Alignment reference = new Alignment();
		reference.add(new Correspondence("1", "1", 0.13));
		reference.add(new Correspondence("2", "2", 0.25));
		reference.add(new Correspondence("3", "3", 0.75));
		reference.add(new Correspondence("4", "4", 1.0));
		final Alignment matcher = new Alignment();
		matcher.add(new Correspondence("1", "1", 1.00));
		matcher.add(new Correspondence("2", "2", 0.6));
		matcher.add(new Correspondence("3", "3", 0.8));
		matcher.add(new Correspondence("4", "4", 0.5));
		Characteristic c = new Characteristic(matcher, reference);
		assertEquals(-0.6002, c.getCorrelation(ALLOW_ZEROS), 0.1);
		assertEquals(-0.6002, c.getCorrelation(!ALLOW_ZEROS), 0.1);
	}
	
	@Test
	public void getCorrelationNoZerosTest3() {
		final boolean ALLOW_ZEROS = false;
		final Alignment reference = new Alignment();
		reference.add(new Correspondence("1", "1", 0.13));
		reference.add(new Correspondence("2", "2", 0.25));
		reference.add(new Correspondence("3", "3", 0.75));
		reference.add(new Correspondence("4", "4", 1.0));
		final Alignment matcher = new Alignment();
		matcher.add(new Correspondence("1", "1", 0.5));
		matcher.add(new Correspondence("2", "2", 0.6));
		matcher.add(new Correspondence("3", "3", 0.8));
		matcher.add(new Correspondence("4", "4", 1.0));
		Characteristic c = new Characteristic(matcher, reference);
		assertEquals(0.9894, c.getCorrelation(ALLOW_ZEROS), 0.1);
	}
	
	@Test
	public void getCorrelationZerosTest() {
		final boolean ALLOW_ZEROS = true;
		final Alignment reference = new Alignment();
		reference.add(new Correspondence("1", "1", 0.13));
		reference.add(new Correspondence("2", "2", 0.25));
		reference.add(new Correspondence("3", "3", 0.75));
		reference.add(new Correspondence("4", "4", 1.0));
		final Alignment matcher = new Alignment();
		matcher.add(new Correspondence("1", "1", 0.42));
		matcher.add(new Correspondence("2", "2", 0.48));
		matcher.add(new Correspondence("3", "3", 0.58));
		matcher.add(new Correspondence("4", "4", 0.6));
		matcher.add(new Correspondence("5", "5", 0.71));
		Characteristic c = new Characteristic(matcher, reference);
		assertEquals(0.1094, c.getCorrelation(ALLOW_ZEROS), 0.1);
		assertEquals(0.9740, c.getCorrelation(!ALLOW_ZEROS), 0.1);
	}
}
