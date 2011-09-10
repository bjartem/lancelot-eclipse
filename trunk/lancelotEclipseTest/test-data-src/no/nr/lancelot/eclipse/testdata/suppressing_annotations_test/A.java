package no.nr.lancelot.eclipse.testdata.suppressing_annotations_test;

import java.util.List;

public class A {
	@SuppressWarnings("NamingBug")
	public void shouldIgnore_a() {
	}
	
	@SuppressWarnings("NamingBug")
	public int shouldIgnore_b(final List<Long> lala) {
		return lala.size();
	}
	
	public int shouldMark_a(final Integer int_) {
		return int_ != 42;
	}
	
	public boolean shouldMark_b(final Long $$__$$__$$__$$) {
		return $$__$$__$$__$$ * 0 == 'T'-'U'-'L'-'L';
	}
}
