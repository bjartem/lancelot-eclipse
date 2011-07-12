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
package no.nr.lancelot.eclipse.actions;

import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.JavaCore;

final class RichRegion implements IRegion {
    private final IRegion underlyingRegion = JavaCore.newRegion();
    
    RichRegion(final List<?> selectedObjects) {
        if (selectedObjects == null)
            throw new IllegalArgumentException();
        populate(selectedObjects);
    }
    
    private void populate(final List<?> selectedObjects) {
        for (final Object selectedObject : selectedObjects) 
            populate(selectedObject);
    }
    
    private void populate(final Object selectedObject) {
        if (! (selectedObject instanceof IJavaElement) ) {
            LancelotPlugin.logError("Unidentified selection: " + selectedObject);
            return;
        }
        
        final IJavaElement selectedJavaElement = (IJavaElement) selectedObject;
        underlyingRegion.add(selectedJavaElement);
    }
    
    public List<IResource> getTopLevelResources() {
        final List<IResource> res = new LinkedList<IResource>();
        for (final IJavaElement javaElement : getElements()) 
            res.add(javaElement.getResource());
        return res;
    }

    @Override
    public void add(final IJavaElement element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final IJavaElement element) {
        return underlyingRegion.contains(element);
    }

    @Override
    public IJavaElement[] getElements() {
        return underlyingRegion.getElements();
    }

    @Override
    public boolean remove(final IJavaElement element) {
        throw new UnsupportedOperationException();
    }
}
