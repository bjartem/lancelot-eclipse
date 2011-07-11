/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Norwegian Computing Center - initial API and implementation
 ******************************************************************************/
package no.nr.einar.naming.jarmony;

import java.util.HashMap;
import java.util.Map;

import no.nr.einar.pb.model.Attribute;

@SuppressWarnings("serial")
public final class Name  {
	
	private final static Map<Attribute, String> map = new HashMap<Attribute, String>() {{
		this.put(Attribute.CONTAINS_LOOP, "loops");
		this.put(Attribute.CREATES_OBJECTS, "creating objects");
		this.put(Attribute.FIELD_READER, "reading field values");
		this.put(Attribute.FIELD_WRITER, "writing to fields");
		this.put(Attribute.HAS_BRANCHES, "branches");
		this.put(Attribute.LOCAL_ASSIGNMENT, "using local variables");
		this.put(Attribute.MULTIPLE_RETURNS, "multiple return points");
		this.put(Attribute.NO_PARAMETERS, "having no parameters");
		this.put(Attribute.PARAMETER_FALLTHROUGH, "using a parameter as return value");
		this.put(Attribute.PARAMETER_TO_FIELD, "writing a parameter to a field");
		this.put(Attribute.RECURSIVE_CALL, "recursive calls");
		this.put(Attribute.RETURNS_FIELD_VALUE, "returning a field value");
		this.put(Attribute.RETURNS_VOID, "having a void return type");
		this.put(Attribute.SAME_NAME_CALL, "calling methods of the same name");
		this.put(Attribute.STATIC, "the static modifier");
		this.put(Attribute.THROWS_EXCEPTIONS, "throwing exceptions");
		this.put(Attribute.TYPE_MANIPULATOR, "performing runtime type manipulation");
	}};
	
	public static String get(final Attribute a) {
		return map.get(a);
	}
	
}
