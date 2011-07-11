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
package no.nr.einar.naming.tagging;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class PythonTagger implements PosTagger {
	
	public static void main(final String[] args) {
		final PosTagger tagger = new PythonTagger();
		final List<String> result = tagger.tag(getFragments("get", "name"));
		for (final String tag: result) {
			System.out.print(tag + " ");
		}
		System.out.println();
	}
	
	private static List<String> getFragments(final String ... parts) {
		final List<String> $ = new ArrayList<String>();
		for (final String p: parts) {
			$.add(p);
		}
		return $;
	}

	public List<String> tag(final List<String> fragments) {
		
		final List<String> $ = new ArrayList<String>();
		try {
			final String pythonPath = "/Library/Frameworks/Python.framework/Versions/Current/bin/python";
			final String scriptPath = "/Users/einar/phd/Papers/Jarmony/trunk/workspace/Tagger/python/tag_test.py";
			final String name = getName(fragments);
			final Process p = Runtime.getRuntime().exec(pythonPath + " " + scriptPath + " " + name);
			p.waitFor();
			if (p.exitValue() != 0) {
				throw new IOException(getError(p));
			}
			final String[] result = getResult(p).trim().split("-");
			for (int i = 0; i < result.length; i++) {
				$.add(result[i]);
			}
		} catch (final IOException e) {
			System.err.println("IOException while trying to run python script.");
			System.err.println(e.getMessage());
		} catch (final InterruptedException e) {
			System.err.println("InterruptedException while trying to run python script.");
		}
		return $;
	}

	private static String getName(final List<String> fragments) {
		final StringBuffer $ = new StringBuffer();
		for (final String f: fragments) {
			if ($.length() > 0) {
				$.append("-");
			}
			$.append(f);
		}
		return $.toString();
	}

	private static String getResult(final Process p) throws IOException {
		final InputStream stream = p.getInputStream();
		final StringBuffer sb = new StringBuffer();
		while (true) {
			final int val = stream.read();
			if (val < 0) {
				break;
			}
			sb.append((char) val);
		}
		return sb.toString();
		
	}

	private static String getError(final Process p) throws IOException {
		final InputStream stream = p.getErrorStream();
		final StringBuffer sb = new StringBuffer();
		while (true) {
			final int val = stream.read();
			if (val < 0) {
				break;
			}
			sb.append((char) val);
		}
		return sb.toString();
	}

}
