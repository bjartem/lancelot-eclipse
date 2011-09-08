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

import no.nr.lancelot.model.JavaClass;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class ClassAnalysisReport implements IClassAnalysisReport {    
    private final JavaClass javaClass;
    private final List<IMethodBugReport> methodBugReports;
    private final Object operationKey;
    private final BugStatisticsData statisticsData;
    
    ClassAnalysisReport(
        final JavaClass javaClass, 
        final List<IMethodBugReport> bugReports,
        final Object operationKey,
        final BugStatisticsData statisticsData
    ) {
        this.javaClass = javaClass;
        this.methodBugReports = bugReports;
        this.operationKey = operationKey;
        this.statisticsData = statisticsData;
    }
    
    @Override
    public String getPackageName() {
        return javaClass.getNamespace();
    }

    @Override
    public String getClassName() {
        return javaClass.getShortName();
    }
    
    @Override
    @Nullable
    public Object getOperationKey() {
        return operationKey;
    }
    
    @Override
    public boolean hasBugs() {
        return methodBugReports.size() > 0;
    }

    @Override
    public List<IMethodBugReport> getMethodBugReports() {
        return methodBugReports;
    }
    
    @Override
    public BugStatisticsData getStatisticsData() {
        return statisticsData;
    }

    @Override
    public String toString() {
        return String.format("[ClassAnalysisReport. Package: %s. Class: %s]", 
                             getPackageName(), getClassName()); 
    }
}