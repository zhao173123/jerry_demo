package book.cleancode.junit.tmp;

import junit.framework.Assert;

public class TmpComparisonCompactor {

	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";
	
	private int contextLength;
	private String expected;
	private String actual;
	private int prefixIndex;//前缀
	private int suffixIndex;//后缀
	
	private String compactExpected;
	private String compactActual;
	
	private int suffixLength;
	private int prefixLength;
	
	public TmpComparisonCompactor(int contextLength, String expected, String actual){
		this.contextLength = contextLength;
		this.expected = expected;
		this.actual = actual;
	}
	
	public String compact(String message){
//		if(expected == null || actual == null || areStringEquals()){
//			return Assert.format(message, expected, actual);
//		}
		//1.将条件判断封装起来，从而更清晰的表达代码的意图
//		if(shouldNotCompact()){
//			return Assert.format(message, expected, actual);
//		}
		//2.否定式比肯定式更难理解
		if(canBeCompacted()){
			compactExpectedAndActual();
			return Assert.format(message, compactExpected, compactActual);
		}else{
			return Assert.format(message, expected, actual);	
		}
		
	}

	private void compactExpectedAndActual() {
//		prefixIndex = findCommonPrefix();
//		suffixIndex = findCommonSuffix(prefixIndex);
		findCommonPrefixAndSuffix();
		compactExpected = compactString(expected);
		compactActual = compactString(actual);
	}
	
	private void findCommonPrefixAndSuffix() {
		findCommonPrefix();
//		int expectedSuffix = expected.length() - 1;
//		int actualSuffix = actual.length() - 1;
//		for(;
//			actualSuffix >= prefixIndex && expectedSuffix >= prefixIndex;
//			actualSuffix--, expectedSuffix--){
//			if(expected.charAt(expectedSuffix) != actual.charAt(actualSuffix)){
//				break;
//			}
//		}
//		suffixIndex = expected.length() - expectedSuffix;
		int suffixLength = 1;
		for(; !suffixOverlapsPrefix(suffixLength);suffixLength++){
			if(charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength)){
				break;
			}
		}
		suffixIndex = suffixLength;
	}
	
	private char charFromEnd(String s, int i){
		return s.charAt(s.length() - i);
	}
	
	private boolean suffixOverlapsPrefix(int suffixLength){
		return actual.length() - suffixLength <= prefixLength || 
				expected.length() - suffixLength <= prefixLength;
	}

	private boolean canBeCompacted() {
		return expected != null && actual != null && !areStringEquals();
	}

	private boolean shouldNotCompact() {
		return expected == null || actual == null || areStringEquals();
	}

	private void findCommonPrefix() {
		int prefixIndex = 0;
		int end = Math.min(expected.length(), actual.length());//fExpected.length()和fActual.length()中较小的一个
		for(;prefixIndex < end; prefixIndex++){
			if(expected.charAt(prefixIndex) != actual.charAt(prefixIndex)){
				break;
			}
		}
	}
	
	private int findCommonSuffix(int prefixIndex) {
		int expectedSuffix = expected.length() - 1;
		int actualSuffix = actual.length() - 1;
		for(;actualSuffix >= prefixIndex && expectedSuffix >= prefixIndex; actualSuffix--, expectedSuffix--){
			if(expected.charAt(expectedSuffix) != actual.charAt(actualSuffix)){
				break;
			}
		}
		return expected.length() - expectedSuffix;
	}
	
	private String compactString(String source) {
		String result = DELTA_START + source.substring(prefixIndex, source.length() - suffixIndex + 1)
							+ DELTA_END;
		if(prefixIndex > 0){
			result = computeCommonPrefix() + result;
		}
		if(suffixIndex > 0){
			result = result + computeCommonSuffix();
		}
		return result;
	}

	private String computeCommonSuffix() {
		int end = Math.min(expected.length() - suffixIndex + 1 + contextLength, expected.length());
		return expected.substring(expected.length() - suffixIndex + 1, end) +
				(expected.length() - suffixIndex + 1 < expected.length() -
				contextLength ? ELLIPSIS : "");
	}

	private String computeCommonPrefix() {
		return (prefixIndex > contextLength ? ELLIPSIS : "") + 
				expected.substring(Math.max(0, prefixIndex - contextLength), prefixIndex); 
	}

	private boolean areStringEquals() {
		return expected.equals(actual);
	}
	
}
