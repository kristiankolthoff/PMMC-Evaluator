package de.unima.ki.pmmc.evaluator.alignment;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AlignmentTest {

	private Alignment a1;
	private Alignment a2;
	
	@Before
	public void init() {
		this.a1 = new Alignment();
		this.a1.add(new Correspondence("1", "1"));
		this.a1.add(new Correspondence("2", "2"));
		this.a1.add(new Correspondence("5", "5"));
		this.a2 = new Alignment();
		this.a2.add(new Correspondence("2", "2"));
		this.a2.add(new Correspondence("1", "1"));
		this.a2.add(new Correspondence("7", "7"));
	}
	
	@Test
	public void alignJoinObjTest() {
		this.a1.join(a2);
		assertEquals(this.a1.getCorrespondences().get(0),this.a1.getCorrespondences().get(0));
		assertEquals(this.a1.getCorrespondences().get(1),this.a1.getCorrespondences().get(1));
		assertEquals(this.a1.getCorrespondences().get(2),this.a1.getCorrespondences().get(2));
		assertEquals(this.a2.getCorrespondences().get(2),this.a1.getCorrespondences().get(3));
	}
	
	@Test
	public void alignJoinStaticTest() {
		Alignment a = Alignment.join(a1, a2);
		assertEquals(this.a1.getCorrespondences().get(0), a.getCorrespondences().get(0));
		assertEquals(this.a1.getCorrespondences().get(1), a.getCorrespondences().get(1));
		assertEquals(this.a1.getCorrespondences().get(2), a.getCorrespondences().get(2));
		assertEquals(this.a2.getCorrespondences().get(2), a.getCorrespondences().get(3));
	}
}
