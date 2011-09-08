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
package no.nr.lancelot.frontend;

import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface IClassAnalysisReport {
    class BugStatisticsData {
        public final int numMethodsTotal,
                         numMethodsCovered,
                         numMethodsBuggy;

        BugStatisticsData(
            final int numMethodsTotal, 
            final int numMethodsCovered, 
            final int numMethodsBuggy
        ) {
            this.numMethodsTotal = numMethodsTotal;
            this.numMethodsCovered = numMethodsCovered;
            this.numMethodsBuggy = numMethodsBuggy;
            
        }
    }
    
    String getPackageName();
    String getClassName();
    @Nullable Object getOperationKey();
    boolean hasBugs();
    List<IMethodBugReport> getMethodBugReports();
    BugStatisticsData getStatisticsData();
}
