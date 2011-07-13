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
package no.nr.lancelot.eclipse.view;

import java.util.Arrays;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

final class SourceTypeFinder {
    private static final SourceTypeFinder INSTANCE = new SourceTypeFinder();
    
    @SuppressWarnings("serial")
    public static final class TypeNotFoundException extends Exception {
        public TypeNotFoundException(final String message) {
            super(message);
        }
        
        public TypeNotFoundException(final JavaModelException e) {
            super(e);
        } 
    }
    
    private SourceTypeFinder() {}
    
    public static SourceTypeFinder getInstance() {
        return INSTANCE;
    }
    
    public IType findSourceType(
        final IJavaProject javaProject, 
        final String packageName, 
        final String typeQualifiedName
    ) throws TypeNotFoundException {
        if (javaProject == null || packageName == null || typeQualifiedName == null)
            throw new IllegalArgumentException();
        
        if (typeQualifiedName.length() == 0)
            throw new IllegalArgumentException("Empty type names are impossible!");

        final String[] typeNames = typeQualifiedName.split("\\$");

        final String outerTypeName = typeNames[0];
        final IType outerType = findOuterType(javaProject, packageName, outerTypeName);

        final boolean isSubTypeLookup = typeNames.length >= 2;
        return isSubTypeLookup ? findSubType(typeNames, outerType) : outerType;
    }

    private IType findOuterType(
        final IJavaProject javaProject, 
        final String packageName, 
        final String typeName
    ) throws TypeNotFoundException {
        try {
            final IType type = javaProject.findType(packageName, typeName, new NullProgressMonitor());
            if (type == null || !type.exists())
                throw new TypeNotFoundException(
                    "PackageName:" + packageName + " outerTypeName:" + typeName
                );
            return type;   
        } catch (JavaModelException e) {
            throw new TypeNotFoundException(e);
        } 
    }

    private IType findSubType(final String[] nameSegments, final IType enclosingType) 
    throws TypeNotFoundException {
        final String[] subNameSegments = Arrays.copyOfRange(nameSegments, 1, nameSegments.length);            

        IType type = enclosingType;
        for (final String name : subNameSegments) {
            type = isAnonymousName(name) ? findAnonymous(type, name) : type.getType(name); 
            if (type == null || !type.exists()) 
                throw new TypeNotFoundException(  
                      "type:" + (type == null ? "null" : type.getElementName()) 
                    + " enclosingType:" + enclosingType.getElementName()
                );
        }
        
        return type;
    }

    private boolean isAnonymousName(final String typeName) {
        if (typeName.isEmpty())
            throw new AssertionError("typeName is empty");
        for (char ch : typeName.toCharArray())
            if (!Character.isDigit(ch))
                return false;
        return true;
    }
    
    private IType findAnonymous(final IType enclosingType, final String typeName) {
        return AnonymousTypeFinder.findAnonymous(enclosingType, typeName);
    }
}
