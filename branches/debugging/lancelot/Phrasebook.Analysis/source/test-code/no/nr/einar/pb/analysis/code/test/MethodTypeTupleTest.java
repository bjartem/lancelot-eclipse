package no.nr.einar.pb.analysis.code.test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import no.nr.einar.pb.analysis.code.asm.MethodTypeTuple;

import org.junit.Test;

public final class MethodTypeTupleTest {

	private static final String VOID = "void";

	@Test(expected=IllegalArgumentException.class)
	public void returnTypeCannotBeNull() {
		new MethodTypeTuple(null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void parameterTypeListCannotBeNull() {
		new MethodTypeTuple(VOID, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void returnTypeCannotBeEmpty() {
		new MethodTypeTuple("", new String[] {});
	}

	@Test
	public void parameterTypeListCanBeEmpty() {
		new MethodTypeTuple(VOID, new String[] {});
	}

	@Test(expected=IllegalArgumentException.class)
	public void parameterTypeListCannotContainNulls() {
		new MethodTypeTuple(VOID, new String[] { null });
	}

	@Test(expected=IllegalArgumentException.class)
	public void parameterTypeListCannotContainEmptyString() {
		new MethodTypeTuple(VOID, new String[] { "" });
	}

	@Test
	public void parameterTypeListIsClonedOnOutputToAvoidInadvertentChange() {
		final String[] paramTypes = new String[] { "int" };
		final MethodTypeTuple tuple = new MethodTypeTuple(VOID, paramTypes);
		assertNotSame(paramTypes, tuple.getParameterTypes());
	}

	@Test
	public void parameterTypeListIsClonedOnInputToAvoidInadvertentChange() {
		final String originalType = "int";
		final String replacementType = "String";
		final String[] inputTypes = new String[] { originalType };
		final MethodTypeTuple tuple = new MethodTypeTuple("void", inputTypes);
		inputTypes[0] = replacementType;
		final String[] outputTypes = tuple.getParameterTypes();
		assertSame(originalType, outputTypes[0]);
	}

}
