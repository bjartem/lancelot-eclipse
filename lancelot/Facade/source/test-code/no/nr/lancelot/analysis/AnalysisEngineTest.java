package no.nr.lancelot.analysis;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;

import no.nr.einar.pb.analysis.code.asm.ClassStreamAnalyzer;
import no.nr.einar.pb.model.Attribute;
import no.nr.einar.pb.model.JavaClass;

import org.junit.Test;

@SuppressWarnings("unused")
public class AnalysisEngineTest {
	@Test
	public void testA() throws Exception {
		check(
			new Object(){
				@edu.umd.cs.findbugs.annotations.SuppressWarnings
				boolean methodUnderInspection() {
					int j = 2;
					for (int i = 0; i < 10; ++i)
						j *= i * j;
					return true;
				}
			}.getClass(),
			new Attribute[]{
				Attribute.HAS_BRANCHES,
				Attribute.CONTAINS_LOOP,
				Attribute.RETURNS_BOOLEAN,
				Attribute.NO_PARAMETERS
			}
		);
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	public class TestBClass {
		int methodUnderInspection(int f) throws Exception {
			int n = 55;
			n *= System.identityHashCode(new TestBClass());

			if (3 < 4)
				throw new Exception();
			
			if (f > 2)
				methodUnderInspection(f/2); // FIXME! DOES NOT GIVE "Same name call"

			if (f > 2)
				new TestBClass().methodUnderInspection(f/2); // DOES GIVE "Same name call"
			
			return n;
		}
	}	
	@Test
	public void testB() throws Exception {
		check(
			TestBClass.class,
			new Attribute[]{
				Attribute.RETURNS_INT,
				Attribute.FIELD_READER, // @TODO!! WHY? Check theory.
				Attribute.THROWS_EXCEPTIONS,
				Attribute.EXPOSES_CHECKED_EXCEPTIONS,
				Attribute.CREATES_REGULAR_OBJECTS,
				Attribute.CREATES_CUSTOM_OBJECTS,
				Attribute.CREATES_OWN_CLASS_OBJECTS,
				Attribute.SAME_NAME_CALL
			}
		);
	}

	private void check(final Class<?> class_, final Attribute[] expectedAttributes) throws Exception {
		final JavaClass result = new ClassStreamAnalyzer().analyze(
			new FileInputStream(findClassLocation(class_))
		);

		if (result.getMethods().length != 1) {
			throw new Exception(
				"Expected one method on object " + class_ + ", was " + result.getMethods().length
			);
		}
		
		long expectedSemantics = 0L;
		for (final Attribute a : expectedAttributes) {
			expectedSemantics |= a.getFlag();
		}
		final long actualSemantics = result.getMethods()[0].getSemantics();
		
		try {
			assertEquals(expectedSemantics, actualSemantics);		
		} catch (AssertionError e) {
			System.out.println("Expected semantics: ");
			printSemantics(expectedSemantics);
			System.out.println("Actual semantics: ");
			printSemantics(actualSemantics);
			throw e;
		}
	}
	
	private void printSemantics(final long semantics) {
		for (final Attribute a : Attribute.values()) {
			if ((a.getFlag() & semantics) != 0)
				System.out.println("  " + a.name());
		}
	}

	private static String findClassLocation(final Class<?> class_) {
		return AnalysisEngineTest.class.getClassLoader().getResource(
		    class_.getName().replace('.', '/') + ".class"
		).getFile();
	}
}
