package no.nr.einar.pb.analysis.code.descriptor;

public interface ITypeParser {

	boolean parse(final int firstCharIndex, final char[] chars);
	String getTypeName();
	int getIndexOfLastCharRead();

}
