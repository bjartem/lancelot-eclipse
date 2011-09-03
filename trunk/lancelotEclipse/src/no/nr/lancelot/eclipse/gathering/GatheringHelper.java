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
package no.nr.lancelot.eclipse.gathering;

import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.JavaCore;

public final class GatheringHelper {
    private GatheringHelper() {}

    public static boolean isProbableClassFile(final IResource resource) {
        if (resource == null)
            throw new IllegalArgumentException();
        
        final boolean isFile = resource.getType() == IResource.FILE;
        
        final String pathString = resource.getFullPath().toString();
        final boolean hasClassSuffix = pathString.endsWith(".class");
        final boolean isPackageInfo = pathString.endsWith("package-info.class");
        
        return isFile && hasClassSuffix && !isPackageInfo;                                    
    }

    public static IClassFile findClassFileOrNull(final IResource resource) {
        if (resource == null)
            throw new IllegalArgumentException();
        
        if (!isProbableClassFile(resource))
            return null;
        
        try {
            return JavaCore.createClassFileFrom((IFile) resource);
        } catch (ClassCastException e) {
            LancelotPlugin.logException(e, "This should never happen!");
            return null;
        }
    }

    public static boolean hasJavaBuildErrors(final IResource resource) throws CoreException {
        if (resource == null)
            throw new IllegalArgumentException();

        final IMarker[] problemMarkers = resource.findMarkers(
            IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, 
            true, 
            IResource.DEPTH_INFINITE
        );
        
        for (final IMarker marker : problemMarkers) 
            if (indicatesJavaBuildError(marker))
                return true;
        
        return false;
    }

    static boolean indicatesJavaBuildError(final IMarker marker) {
        return marker.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR;
    }
}
