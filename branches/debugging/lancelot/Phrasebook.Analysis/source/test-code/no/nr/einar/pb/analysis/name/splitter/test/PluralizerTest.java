package no.nr.einar.pb.analysis.name.splitter.test;

import static org.junit.Assert.*;

import no.nr.einar.pb.analysis.name.collapser.Pluralizer;

import org.junit.Test;

public final class PluralizerTest {
	
	private void test(final String singular, final String expected) {
		assertEquals(expected, Pluralizer.pluralize(singular));
	}
	
	@Test
	public void test1() {
		test("node", "nodes");
	}
	
	@Test
	public void test2() {
		test("address", "addresses");
	}
	
	@Test
	public void test3() {
		test("query", "queries");
	}
	
	@Test
	public void test4() {
		test("y", "ys");
	}
	
	@Test
	public void test5() {
		test("s", "ses");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test6() {
		Pluralizer.pluralize(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test7() {
		Pluralizer.pluralize("");
	}
	

}
