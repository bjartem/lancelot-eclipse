package no.nr.einar.pb.analysis.name.collapser.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import no.nr.einar.pb.analysis.name.collapser.Fragment;
import no.nr.einar.pb.analysis.name.collapser.FragmentCollapser;
import no.nr.einar.pb.model.JavaMethod;
import no.nr.einar.pb.model.TypeDictionary;

import org.junit.Test;

public final class FragmentCollapserTest {
	
	private void test(final String methodName,
			final String[] fragments,
			final String[] types,
			final String[] collapsed,
			final boolean[] isType) {
		final List<String> fragmentList = new ArrayList<String>();
		for (int i = 0; i < fragments.length; i++) {
			fragmentList.add(fragments[i]);
		}
		final TypeDictionary typeDict = new TypeDictionary();
		for (int i = 0; i < types.length; i++) {
			typeDict.add(types[i]);
		}
		final JavaMethod javaMethod = new JavaMethod(methodName, "", new String[] {}, 0, typeDict, 0, false);
		final List<Fragment> collapsedList = FragmentCollapser.collapse(fragmentList, javaMethod);
		assertNotNull(collapsedList);
		assertEquals(collapsed.length, collapsedList.size());
		for (int i = 0; i < collapsed.length; i++) {
			final Fragment frag = collapsedList.get(i);
			assertEquals(collapsed[i], frag.getText());
			assertEquals(isType[i], frag.isTypeName());
		}
	}
	
	@Test
	public void test1() {
		test("getFoo",	
				new String[] { "get", "foo" },
				new String[] { "Foo" },
				new String[] { "get", "Foo" },
				new boolean[] { false, true }
				);
	}
	
	@Test
	public void test2() {
		test("getClassLoader",	
				new String[] { "get", "class", "loader" },
				new String[] { "Class" },
				new String[] { "get", "Class", "loader" },
				new boolean[] { false, true, false }
				);
	}
	
	@Test
	public void test3() {
		test("getClassLoader",	
				new String[] { "get", "class", "loader" },
				new String[] { "ClassLoader" },
				new String[] { "get", "ClassLoader" },
				new boolean[] { false, true }
				);
	}
	
	@Test
	public void test4() {
		test("getClassLoader",	
				new String[] { "get", "class", "loader" },
				new String[] { "Class", "ClassLoader" },
				new String[] { "get", "ClassLoader" },
				new boolean[] { false, true }
				);
	}
	
	@Test
	public void test5() {
		test("getClassLoader",	
				new String[] { "get", "class", "loader", "by", "class" },
				new String[] { "Class", "ClassLoader" },
				new String[] { "get", "ClassLoader", "by", "Class" },
				new boolean[] { false, true, false, true }
				);
	}
	
	@Test
	public void test6() {
		test("getFilteredTreeViewer",	
				new String[] { "get", "filtered", "tree", "viewer" },
				new String[] { "FilteredTree", "TreeViewer" },
				new String[] { "get", "FilteredTree", "viewer" },
				new boolean[] { false, true, false }
				);
	}
	
	@Test
	public void test7() {
		test("getFizzByBuzz",	
				new String[] { "get", "fizz", "by", "buzz" },
				new String[] { "Fizz", "Buzz" },
				new String[] { "get", "Fizz", "by", "Buzz" },
				new boolean[] { false, true, false, true }
				);
	}
	
	@Test
	public void test8() {
		test("getFoo",	
				new String[] { "get", "foo" },
				new String[] { "no.nr.einar.Foo" },
				new String[] { "get", "Foo" },
				new boolean[] { false, true }
				);
	}
	
	@Test
	public void test9() {
		test("getInputStream",	
				new String[] { "get", "input", "stream" },
				new String[] { "InputStream" },
				new String[] { "get", "InputStream" },
				new boolean[] { false, true }
				);
	}
	
	@Test
	public void test10() {
		test("getInputStream",	
				new String[] { "get", "input", "stream" },
				new String[] { "Stream" },
				new String[] { "get", "input", "Stream" },
				new boolean[] { false, false, true }
				);
	}
	
	@Test
	public void testIterator() {
		
	}
	
}
