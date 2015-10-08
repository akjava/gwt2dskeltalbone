package com.akjava.gwt.skeltalboneanimation.unittest;

import static org.junit.Assert.*;

import org.junit.Test;

public class BetweenAngleTest {
	private static final double DELTA = 1e-15;
	
	private void betweenTest(double a,double b,double between){
		assertEquals(between,betweenAngle(a,b),DELTA);
	}
	
	@Test
	public void test_0_90() {
		betweenTest(0,90,45);
	}
	@Test
	public void test_0__90() {
		betweenTest(0,-90,-45);
	}
	@Test
	public void test_0_180() {
		betweenTest(0,180,90);
	}
	@Test
	public void test_0_178() {
		betweenTest(0,178,89);
	}
	@Test
	public void test_0_182() {
		betweenTest(0,182,271);
	}
	@Test
	public void test_0__180() {
		betweenTest(0,-180,-90);
	}
	@Test
	public void test_0__178() {
		betweenTest(0,-178,-89);
	}
	@Test
	public void test_0__182() {
		betweenTest(0,-182,89);
	}
	
	@Test
	public void test_90_180() {
		betweenTest(90,180,135);
	}
	@Test
	public void test_90_0() {
		betweenTest(90,0,45);
	}
	@Test
	public void test_90_270() {
		betweenTest(90,270,180);
	}
	@Test
	public void test_90_268() {
		betweenTest(90,268,179);
	}
	@Test
	public void test_90_272() {
		betweenTest(90,272,361);
	}
	
	@Test
	public void test_90__90() {
		betweenTest(90,-90,0);
	}
	@Test
	public void test_90__88() {
		betweenTest(90,-88,1);
	}
	@Test
	public void test_90__92() {
		betweenTest(90,-92,179);
	}
	

	
	public double betweenAngle(double angleA,double angleB){
		double angleBetween=Math.abs(angleA-angleB)<=180?angleA+(angleB-angleA)/2:angleA+(angleB-angleA)/2+180;
		return angleBetween;
	}
}
