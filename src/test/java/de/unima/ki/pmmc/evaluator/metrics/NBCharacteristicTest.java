package de.unima.ki.pmmc.evaluator.metrics;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;


public class NBCharacteristicTest {

	private Characteristic characteristic;
	private Characteristic characteristic2;
	private Characteristic characteristic3;
	private List<Characteristic> characteristicsZero;
	private List<Characteristic> characteristics;
	private Characteristic characteristic4;
	
	@Before
	public void init() {
		//Initial test set
		this.characteristic = new Characteristic(new Alignment(), 
				new Alignment());
		final Alignment align11 = new Alignment();
		align11.add(new Correspondence("11", "1", 0.8));
		align11.add(new Correspondence("11", "2", 0.2));
		final Alignment align12 = new Alignment();
		align12.add(new Correspondence("11", "1", 0.9));
		this.characteristic2 = new Characteristic(align12, align11);
		final Alignment align21 = new Alignment();
		align21.add(new Correspondence("21", "1", 0.7));
		align21.add(new Correspondence("21", "2", 0.1));
		final Alignment align22 = new Alignment();
		align22.add(new Correspondence("21", "2", 0.6));
		this.characteristic3 = new Characteristic(align22, align21);
		this.characteristicsZero = new ArrayList<>();
		this.characteristicsZero.add(characteristic);
		this.characteristics = new ArrayList<Characteristic>();
		this.characteristics.add(this.characteristic2);
		this.characteristics.add(this.characteristic3);
		//Test from example sheet
		final Alignment goldAlign = new Alignment();
		goldAlign.add(new Correspondence("1", "1", 0.75));
		goldAlign.add(new Correspondence("2", "2", 0.88));
		goldAlign.add(new Correspondence("3", "3", 0.25));
		goldAlign.add(new Correspondence("4", "4", 0.13));
		final Alignment matcher1Align = new Alignment();
		matcher1Align.add(new Correspondence("1", "1", 0.5));
		matcher1Align.add(new Correspondence("2", "2", 0.7));
		matcher1Align.add(new Correspondence("5", "5", 0.1));
		matcher1Align.add(new Correspondence("6", "6", 0.8));
		this.characteristic4 = new Characteristic(matcher1Align, goldAlign);
	}
	
	@Test
	public void nonBinaryRecallTest() {
		assertEquals(0.8d, this.characteristic2.getNBRecall(), 0d);
		assertEquals(0.1/0.8, this.characteristic3.getNBRecall(), 0.0001d);
	}
	
	@Test
	public void nonBinaryPrecisionTest() {
		assertEquals(1, this.characteristic2.getNBPrecision(), 0d);
	}
	
	@Test
	public void nonBinaryRecallExampleTest() {
		assertEquals(0.81, this.characteristic4.getNBRecall(), 0.001);
	}
	
	@Test
	public void nonBinaryPrecisionExampleTest() {
		assertEquals(0.44, this.characteristic4.getNBPrecision(), 0.1d);
	}
	
	@Test
	public void zeroRulesMatcherPrecisionTest() {
		assertTrue(Double.isNaN(this.characteristic.getNBPrecision()));
	}
	
	@Test
	public void zeroRulesGoldRecallTest() {
		assertTrue(Double.isNaN(this.characteristic.getNBRecall()));
	}
	
	@Test
	public void zeroPrecisionAndRecallFMeasureTest() {
		assertTrue(Double.isNaN(this.characteristic.getNBFMeasure()));
	}
	
	@Test
	public void computeZeroPrecisionAndRecallFMeasureTest() {
		assertEquals(0, Characteristic.computeFFromPR(0, 0), 0);
	}
	
//	@Test
//	public void getFMeasureTest() {
//		assertEquals(0.8, this.characteristic2.getFMeasure(), 0.0001d);
//		assertEquals(0.11, this.characteristic3.getFMeasure(), 0.1d);
//	}
	
	@Test
	public void getRecallMacroZeroTest() {
		assertTrue(Double.isNaN(Characteristic.getNBRecallMacro(characteristicsZero)));
	}
	
	@Test
	public void getRecallMacroTest() {
		assertEquals(0.4625, Characteristic.getNBRecallMacro(this.characteristics),0);
	}
	
	@Test
	public void getRecallMicroTest() {
		assertEquals(0.5, Characteristic.getNBRecallMicro(this.characteristics), 0.00001d);
	}
	
	@Test
	public void getPrecisionMacroTest() {
		assertEquals(1, Characteristic.getNBPrecisionMacro(this.characteristics), 0.01d);
	}
	
	@Test
	public void getPrecisionMicroTest() {
		assertEquals(1, Characteristic.getNBPrecisionMicro(this.characteristics), 0);
	}
	
//	@Test
//	public void getFMeasureMacroTest() {
////		assertEquals(0.45, Characteristic.getFMeasureMacro(this.characteristics), 0.1d);
//	}
	
//	@Test
//	public void getFMeasureMicroTest() {
//		assertEquals(Characteristic.computeFFromPR(0.45, 0.5), 
//				Characteristic.getFMeasureMicro(this.characteristics), 0);
//	}
}
