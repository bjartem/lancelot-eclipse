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
package no.nr.lancelot.analysis;

import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

import no.nr.einar.pb.model.JavaClass;

public final class ClassAnalysisReport implements IClassAnalysisReport {
	private final String packageName;
	private final String className;
	private final List<MethodBugReport> methodBugReports;
	private final Object operationKey;

	ClassAnalysisReport(
		final JavaClass javaClass, 
		final List<MethodBugReport> bugReports,
		final Object operationKey
	) {
		this.packageName = javaClass.getNamespace();
		this.className = javaClass.getShortName();
		this.methodBugReports = bugReports;
		this.operationKey = operationKey;
	}
	
	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public String getClassName() {
		return className;
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
	public List<MethodBugReport> getMethodBugReports() {
		return methodBugReports;
	}

	@Override
	public String toString() {
		return String.format("[ClassAnalysisReport. Package: %s. Class: %s. Bug count: %d]", 
							 packageName, className, methodBugReports.size()); 
	}
}
