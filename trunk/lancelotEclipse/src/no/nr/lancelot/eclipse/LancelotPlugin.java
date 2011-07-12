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
package no.nr.lancelot.eclipse;

import no.nr.lancelot.analysis.LancelotRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/* This class acts as a initializer and an entry point for 
 * common functionality. 
 * 
 * It is not the correct place to  begin reading the plugin. 
 * Have a look at the classes in package actions or the main 
 * LancelotController instead.
 */
public class LancelotPlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "no.nr.lancelot.eclipse";

    public static final String SUPPRESS_WARNINGS_NAME = "NamingBug";

    private static LancelotPlugin INSTANCE = null;
    
    public static LancelotPlugin getDefault() {
        return INSTANCE;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;        
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        INSTANCE = null;
        super.stop(context);
    }
    
    public void ensureBackendIsConfigured() {
        if (!LancelotRegistry.isInitialized()) 
            new LancelotInitializer().run();
    }

    public static void logTrace(final Object message) {
        // FIXME !! PLUG IN PROPERERLY TO TRACING SYSTEM. FAQ PAGE 117.
        final boolean isTracing = true;
        if (!isTracing)
            return;
        System.out.println(message);
    }
    
    public static void logInfo(final Object message) {
        logTrace(message);
    }
    
    public static void logInfo(final Class<?> class_, final String message) {
        logInfo(String.format("%s: %s", class_.getSimpleName(), message));
    }
    
    public static void logError(final String message) {
        INSTANCE.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
    }
    
    public static void logException(final Throwable throwable, final String comment) {
        logError(String.format(
            "Exception %s raised. [Message: %s. Comment: %s].",
            throwable, throwable.getMessage(), comment
        ));
    }
    
    public static void logException(final Throwable throwable) {
        throwable.printStackTrace();
        logException(throwable, "(No comment)");
    }
    
    public static void throwWrappedException(final Throwable throwable, final String comment) 
    throws CoreException {
        throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, comment, throwable));
    }
    
    public static void reportFatalError(final String details) {
        // We must ensure that we open the error dialog in the UI thread.
        new UIJob("Message dialog job") {
            @Override
            public IStatus runInUIThread(final IProgressMonitor monitor) {
                final String title = "Fatal error",
                             message = "Lancelot encountered a fatal error.\n\n" +
                                       "Details: " + details;
                MessageDialog.openError(null, title, message);
                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
