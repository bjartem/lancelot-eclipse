package no.nr.einar.pb.analysis.code.test;

import static org.junit.Assert.*;
import no.nr.einar.pb.analysis.code.asm.MethodNodeAnalyzer;
import no.nr.einar.pb.model.IJavaMethod;
import no.nr.einar.pb.model.Attribute;

import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public final class MethodNodeAnalyzerDataDrivenTest {

	private void test(final String className,
			final String methodName,
			final String returnType,
			final String[] paramTypes,
			final long attributeMask) {
		final MethodNode node = MethodNodeFactory.create(className, methodName);
		final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
		final ClassNode cNode = new ClassNode();
		cNode.name = className;
		final IJavaMethod method = mna.analyze(cNode, node);
		assertNotNull(method);
		assertEquals(methodName, method.getMethodName());
		assertEquals(returnType, method.getReturnType());
		final String[] actualParamTypes = method.getParameterTypes();
		assertEquals(paramTypes.length, actualParamTypes.length);
		for (int i = 0; i < paramTypes.length; i++) {
			assertEquals(paramTypes[i], actualParamTypes[i]);
		}

		match(attributeMask, Attribute.RETURNS_VOID, !method.hasReturnValue());
		match(attributeMask, Attribute.FIELD_WRITER, method.isFieldWriter());
		match(attributeMask, Attribute.FIELD_READER, method.isFieldReader());
		match(attributeMask, Attribute.CONTAINS_LOOP, method.hasLoop());
		match(attributeMask, Attribute.THROWS_EXCEPTIONS, method.isExceptionThrower());
		match(attributeMask, Attribute.SAME_NAME_CALL, method.isSameNameDelegator());
		match(attributeMask, Attribute.TYPE_MANIPULATOR, method.isTypeManipulator());
		match(attributeMask, Attribute.NO_PARAMETERS, !method.hasParameters());
		match(attributeMask, Attribute.PARAMETER_TO_FIELD, method.isParameterKeeper());
		match(attributeMask, Attribute.RETURNS_FIELD_VALUE, method.isStateReturner());
//		match(attributeMask, Attribute.PARAMETER_FALLTHROUGH, method.isFilter());
	}

	private void match(final long attributeMask,
			final Attribute attribute,
			final boolean value) {
		assertTrue(attribute.name(), check(attributeMask, attribute, value));
	}

	private boolean check(final long attributeMask,
			final Attribute attribute,
			final boolean value) {
		return (attributeMask & attribute.getFlag()) > 0 == value;
	}

	@Test
	public void getter() {
		final long mask =
			Attribute.FIELD_READER.getFlag() 
			| Attribute.NO_PARAMETERS.getFlag()
			| Attribute.RETURNS_FIELD_VALUE.getFlag();
		test("ImmutableName", "getName", "java.lang.String",
		new String[0],
		mask);
	}

	@Test
	public void setter() {
		final long mask =
			Attribute.FIELD_WRITER.getFlag() 
			| Attribute.RETURNS_VOID.getFlag()
			| Attribute.PARAMETER_TO_FIELD.getFlag();
		test("no/nr/einar/AgeBean", "setAge", "void",
				new String[] { "int" },
				mask);
	}

	@Test
	public void finder() {
		final long mask =
			Attribute.FIELD_READER.getFlag()
			| Attribute.CONTAINS_LOOP.getFlag()
			| Attribute.TYPE_MANIPULATOR.getFlag();
		test("no/nr/einar/Finder", "isInList", "boolean",
				new String[] { "java.lang.String" },
				mask);
	}

	@Test
	public void fibonacci() {
		final long mask =
			Attribute.RECURSIVE_CALL.getFlag();
		test("no/nr/einar/Fibonacci", "fib", "int",
				new String[] { "int" },
				mask);
	}

	@Test
	public void checker() {
		final long mask =
			Attribute.THROWS_EXCEPTIONS.getFlag() 
			| Attribute.RETURNS_VOID.getFlag();
		test("no/nr/einar/Checker", "checkNotNull", "void",
				new String[] { "java.lang.Object" },
				mask);
	}
	
	@Test
	public void equalizer() {
		final long mask =
			Attribute.FIELD_READER.getFlag()
			| Attribute.SAME_NAME_CALL.getFlag() 
			| Attribute.TYPE_MANIPULATOR.getFlag();
		test("ImmutableName", "equals", "boolean",
				new String[] { "java.lang.Object" },
				mask);
	}

}
