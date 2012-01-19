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

import static org.eclipse.core.resources.IMarker.CHAR_END;
import static org.eclipse.core.resources.IMarker.CHAR_START;
import static org.eclipse.core.resources.IMarker.LOCATION;
import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IMarker.SEVERITY;
import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;

import edu.umd.cs.findbugs.annotations.Nullable;

public final class LancelotMarkerUtil {
    private static final LancelotMarkerUtil INSTANCE = new LancelotMarkerUtil();

    public static final String BUG_MARKER_TYPE = LancelotPlugin.PLUGIN_ID + ".namingBugMarker";
    
    public static final String ALTERNATIVE_NAMES_ATTRIBUTE = "ALTERNATIVE_NAMES",
                               METHOD_HANDLE_ID_ATTRIBUTE  = "METHOD_HANDLE";
    
    public static final char   ALTERNATIVE_NAMES_SEPARATOR = '|';
    public static final String ALTERNATIVE_NAMES_SEPARATOR_RE = "\\|";
    
    private LancelotMarkerUtil() {}
    
    public static LancelotMarkerUtil getInstance() {
        return INSTANCE;
    }
    
    public void removeMarkersOn(final IResource resource) throws CoreException {
        if (resource == null)
            throw new IllegalArgumentException();
        resource.deleteMarkers(BUG_MARKER_TYPE, false, IResource.DEPTH_INFINITE);
    }
    
    public IMarker[] getMarkersInWorkspace() throws CoreException {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        return workspaceRoot.findMarkers(BUG_MARKER_TYPE, false, IResource.DEPTH_INFINITE);
    }
    
    @Nullable
    public static IMethod findMethod(final IMarker marker) {
        final String id = marker.getAttribute(METHOD_HANDLE_ID_ATTRIBUTE, null);

        if (id == null) {
            LancelotPlugin.logError("marker " + marker + " has no method handle id attribute!");
            return null;
        }

        final IMethod method = (IMethod) JavaCore.create(id);
        if (method == null || !method.exists()) {
            LancelotPlugin.logError("method for handle " + id + "is null or does not exists!");
            return null;
        }

        return method;
    }
    
    // Entry point to our little semi-fluent interface :-) 
    public static MarkerBuilder createMarker() {
        return new MarkerBuilder();
    }
    
    public static class MarkerBuilder {
        private IResource resource = null;
        private int charStart = -1;
        private int charEnd = -1;
        private String location = null;
        private String message = null;
        private int severity = -1;
        private boolean severityIsSet = false;
        private String alternativeNames = null;
        private String methodHandle = null;
        
        private MarkerBuilder() {}
        
        public MarkerBuilder forResource(final IResource r)   { resource = r;  return this; }
        public MarkerBuilder startingAt(final int n)          { charStart = n; return this; }
        public MarkerBuilder endingAt(final int n)            { charEnd = n;   return this; }
        public MarkerBuilder withLocationText(final String s) { location = s;  return this; }
        public MarkerBuilder withMessage(final String s)      { message = s;   return this; }
        public MarkerBuilder withSeverity(final int n) { 
            severity = n; 
            severityIsSet = true; 
            return this; 
        }
        public MarkerBuilder withAlternativeNames(final String an) { 
            alternativeNames = an; 
            return this;
        }
        public MarkerBuilder withMethodHandle(final String mh) { methodHandle = mh; return this; }
        
        public IMarker create() throws CoreException {
            if (!isProperlyConfigured()) 
                throw new IllegalStateException("Not properly initialized!");

            final IMarker marker = resource.createMarker(BUG_MARKER_TYPE);
            addCommonAttributes(marker);
            addLancelotSpecificAttributes(marker);
            return marker;
        }
        
        protected boolean isProperlyConfigured() {
            return resource != null 
            && charStart != -1       && charEnd != -1  && location != null 
            && message != null       && severityIsSet  && alternativeNames != null 
            && methodHandle != null;
        }

        private void addLancelotSpecificAttributes(final IMarker marker) throws CoreException {
            marker.setAttributes(
                new String[]{ ALTERNATIVE_NAMES_ATTRIBUTE, METHOD_HANDLE_ID_ATTRIBUTE }, 
                new Object[]{ alternativeNames,            methodHandle }
            );
        }

        private void addCommonAttributes(final IMarker marker) throws CoreException {
            marker.setAttributes(
                new String[]{ CHAR_START, CHAR_END, LOCATION, SEVERITY, MESSAGE },
                new Object[]{ charStart,  charEnd,  location, severity, message } 
            );
        }
    }
}
