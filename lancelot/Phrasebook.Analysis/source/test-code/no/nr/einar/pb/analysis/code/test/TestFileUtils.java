package no.nr.einar.pb.analysis.code.test;

import java.io.File;
import java.io.IOException;

public final class TestFileUtils {

	private static final String TEST_DATA_DIR = "test-data";
	private static final String APPLICATIONS_DIR = "applications";
	private static final String CLASS_FILES_DIR = "classfiles";
	private static final String JAR_FILES_DIR = "jarfiles";
	private static final String INVALID_FILES_DIR = "invalidfiles";

	private TestFileUtils() {}

	public static String getCurrentDir() {
		final File dir = new File(".");
		String currentDir = "";
		try {
			currentDir = dir.getCanonicalPath();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return currentDir;
	}

	private static String getTestDataDir() {
		return getCurrentDir() + "/" + TEST_DATA_DIR + "/";
	}

	public static String getApplicationTestDataDirectory() {
		final String path = getTestDataDir() + APPLICATIONS_DIR;
		return getFile(path);
	}

	public static String getApplicationDirectory(final String appName) {
		final String path = getTestDataDir() + APPLICATIONS_DIR + "/"
			+ appName;
		return getFile(path);
	}

	public static String getClassFile(final String className) {
		final String path = getTestDataDir() + CLASS_FILES_DIR + "/"
			+ className + ".class";
		return getFile(path);
	}

	public static String getJarFile(final String jarFileName) {
		final String path = getTestDataDir() + JAR_FILES_DIR + "/"
			+ jarFileName;
		return getFile(path);
	}

	public static String getInvalidFile(final String fileName) {
		final String path = getTestDataDir() + INVALID_FILES_DIR + "/"
			+ fileName;
		return getFile(path);
	}

	private static String getFile(final String path) {
		final File file = new File(path);
		String result = "";
		try {
			result = file.getCanonicalPath();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
