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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import no.nr.lancelot.frontend.LancelotRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/* This class acts as a initializer and an entry point for 
 * common functionality. 
 * 
 * It is not the correct place to  begin reading the plugin. 
 * See the classes in package "actions" or controller/LancelotController instead.
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
            new Initializer().run();
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
        throwable.printStackTrace();
    }
    
    public static void logException(final Throwable throwable) {
        logException(throwable, "(No comment)");
    }
    
    public static void throwWrappedException(final Throwable throwable, final String comment) 
    throws CoreException {
        throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, comment, throwable));
    }
    
    private static final class Initializer {
        /* The ID of the lancelot *analysis engine* plugin.
         */
        public static final String ANALYSIS_PLUGIN_ID = "no.nr.lancelot"; 
        
        /* This is the bundle belonging to the lancelot analysis engine, 
         * not lancelotEclipse.
         *
         * Atm, we put the work of actually initializing the engine 
         * (by providing the correct paths) to the client code (in 
         * this case lancelotEclipse), even though the resources that are 
         * referenced are bundled with the engine itself. The rationale is 
         * that the engine should not need to know too much about context-
         * specific bundling and packaging.
         */
        private final Bundle analysisBundle = Platform.getBundle(ANALYSIS_PLUGIN_ID);    
        
        private final static IPath RULEBOOK_PATH     = new Path("resources/rules.xml"),
                                   WORDNET_DICT_PATH = new Path("resources/wordnet-3-dict/"),
                                   LINGO_PATH        = new Path("resources/manual_dict.txt"),
                                   REVERSE_MAP_PATH  = new Path("resources/reverse_map.txt");

        public void run() {
            try {
                final long startTimeMs = System.currentTimeMillis();
                LancelotPlugin.logInfo("Initializing Lancelot...");
                
                LancelotRegistry.initialize(
                    findRulebookUrl(), 
                    findWordnetUrl(), 
                    findLingoFile(), 
                    findReverseMapFile()
                );
                
                final long stopTimeMs = System.currentTimeMillis();
                final long spentTimeMs = stopTimeMs - startTimeMs;
                LancelotPlugin.logInfo("Lancelot initialized in " + spentTimeMs + " ms.");
            } catch (final Exception e) {
                LancelotPlugin.logException(e);
                reportFatalError(e);
            }
        }

        private void reportFatalError(final Exception e) {
            new UIJob("Fatal error report") {
                @Override
                public IStatus runInUIThread(final IProgressMonitor monitor) {
                    MessageDialog.openError(
                        null, 
                        "Lancelot Fatal Error", 
                        "Lancelot engine initialization failed. (" + e + ")"
                    );
                    return Status.OK_STATUS;
                }
            }.schedule();
        }
        
        private URL findRulebookUrl() throws IOException {
            return resolveInAnalysisBundle(RULEBOOK_PATH);
        }
        
        private URL findWordnetUrl() throws IOException {
            return resolveInAnalysisBundle(WORDNET_DICT_PATH);
        }
        
        private File findLingoFile() throws IOException, URISyntaxException {
            return new File(resolveInAnalysisBundle(LINGO_PATH).toURI());
        }

        private File findReverseMapFile() throws URISyntaxException, IOException {
            return new File(resolveInAnalysisBundle(REVERSE_MAP_PATH).toURI());
        }
        
        private URL resolveInAnalysisBundle(final IPath path) throws IOException {
            final URL generalUrl = FileLocator.find(analysisBundle, path, null);
            if (generalUrl == null)
                throw new IllegalArgumentException(path + " is invalid. find(path) = null");
            
            final URL resolvedUrl = FileLocator.resolve(generalUrl);
            if (resolvedUrl == null)
                throw new IllegalArgumentException(path + " is invalid. resolve(find(path)) = null");
            
            return resolvedUrl;
        }
    }
}