package no.nr.einar.pb.analysis.code.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import no.nr.einar.pb.analysis.code.asm.MethodTypeTuple;
import no.nr.einar.pb.analysis.code.descriptor.MethodDescriptorParser;

import org.junit.Test;

public final class MethodDescriptorParserTest {

	@Test(expected=IllegalArgumentException.class)
	public void descriptorCannotBeNull() {
		MethodDescriptorParser.getMethodTypeTuple(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void descriptorCannotBeEmpty() {
		MethodDescriptorParser.getMethodTypeTuple("");
	}

	@Test
	public void returnVoidNoParameters() {
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple("()V");
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
		assertEquals("void", tuple.getReturnType());
		assertNotNull(tuple.getParameterTypes());
		assertEquals(0, tuple.getParameterTypes().length);
	}

	@Test
	public void returnIntegerNoParameters() {
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple("()I");
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
		assertEquals("int", tuple.getReturnType());
		assertNotNull(tuple.getParameterTypes());
		assertEquals(0, tuple.getParameterTypes().length);
	}

	@Test
	public void returnIntegerParameterInteger() {
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple("(I)I");
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
		assertEquals("int", tuple.getReturnType());
		final String[] paramTypes = tuple.getParameterTypes();
		assertNotNull(paramTypes);
		assertEquals(1, paramTypes.length);
		assertEquals("int", paramTypes[0]);
	}

	@Test
	public void returnLongPrimitiveParameters() {
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple("(ZIB)J");
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
		assertEquals("long", tuple.getReturnType());
		final String[] paramTypes = tuple.getParameterTypes();
		assertNotNull(paramTypes);
		assertEquals(3, paramTypes.length);
		assertEquals("boolean", paramTypes[0]);
		assertEquals("int", paramTypes[1]);
		assertEquals("byte", paramTypes[2]);
	}

	@Test
	public void returnStringPrimitiveParameters() {
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple("(ZIB)Ljava/lang/String;");
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
//		assertEquals("String", tuple.getReturnType());
		assertEquals("java.lang.String", tuple.getReturnType());
		final String[] paramTypes = tuple.getParameterTypes();
		assertNotNull(paramTypes);
		assertEquals(3, paramTypes.length);
		assertEquals("boolean", paramTypes[0]);
		assertEquals("int", paramTypes[1]);
		assertEquals("byte", paramTypes[2]);
	}

	@Test
	public void complexMix() {
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple("(Ljava/lang/Object;FLno/nr/einar/Foo$Bar;Z)Lno/nr/einar/Foo$Baz$4;");
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
//		assertEquals("Foo$Baz$4", tuple.getReturnType());
		assertEquals("no.nr.einar.Foo$Baz$4", tuple.getReturnType());
		final String[] paramTypes = tuple.getParameterTypes();
		assertNotNull(paramTypes);
		assertEquals(4, paramTypes.length);
//		assertEquals("Object", paramTypes[0]);
		assertEquals("java.lang.Object", paramTypes[0]);
		assertEquals("float", paramTypes[1]);
//		assertEquals("Foo$Bar", paramTypes[2]);
		assertEquals("no.nr.einar.Foo$Bar", paramTypes[2]);
		assertEquals("boolean", paramTypes[3]);
	}

	@Test
	public void arrayType() {
		final String descriptor = "([Ljava/lang/Class;)V";
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple(descriptor);
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
		assertEquals("void", tuple.getReturnType());
		final String[] paramTypes = tuple.getParameterTypes();
		assertNotNull(paramTypes);
		assertEquals(1, paramTypes.length);
//		assertEquals("Class[]", paramTypes[0]);
		assertEquals("java.lang.Class[]", paramTypes[0]);
	}

	@Test
	public void arrayOfPrimitiveTypes() {
		final String descriptor = "([[I)V";
		final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple(descriptor);
		assertNotNull(tuple);
		assertNotNull(tuple.getReturnType());
		assertEquals("void", tuple.getReturnType());
		final String[] paramTypes = tuple.getParameterTypes();
		assertNotNull(paramTypes);
		assertEquals(1, paramTypes.length);
		assertEquals("int[][]", paramTypes[0]);
	}

}
