package book.cleancode.junit;

import junit.framework.Assert;

public class MyComparisonCompactor {

	private static final String ELLIPSIS = "...";
	private static final String DELTA_START = "[";
	private static final String DELTA_END = "]";
	
	private int contextLength;
	private String expected;
	private String actual;
	private int prefixLength;//前缀
	private int suffixLength;//后缀
	
	public MyComparisonCompactor(int contextLength, String expected, String actual){
		this.contextLength = contextLength;
		this.expected = expected;
		this.actual = actual;
	}
	
	public String formatCompactedComparison(String message){
		String compactExpected = expected;
		String compactActual = actual;
		if(shouldBeCompacted()){
			findCommonPrefixAndSuffix();
			compactExpected = compact(expected);
			compactActual = compact(actual);
		}
		return Assert.format(message, compactExpected, compactActual);
	}
	
	private boolean shouldBeCompacted(){
		return !shouldNotBeCompacted();
	}
	
	private boolean shouldNotBeCompacted(){
		return expected == null || 
			   actual == null ||
			   expected.equals(actual);
	}
	
	private void findCommonPrefixAndSuffix(){
		findCommonPrefix();
		//从后缀开始比较expected和actual的不同，标记于suffixLength
		for(suffixLength = 0; !suffixOverlapsPrefix(); suffixLength++){
			if(charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength)){
				break;
			}
		}
	}

	/**
	* 先比较expected和actual相同位数的值
	* 1.查找expected.length和actual.length较小的length
	* 2.鉴别出expected和actual的哪一位开始不同,记于prefixLength
	* 比如："abcde"和"adcde"，prefixLength = 1
	*/
	private void findCommonPrefix(){
		int end = Math.min(expected.length(), actual.length());
		for(prefixLength = 0; prefixLength < end; prefixLength++){
			if(expected.charAt(prefixLength) != actual.charAt(prefixLength)){
				break;
			}
		}
	}
	
	//不越过前缀记录的prefixLength（已经标记）
	private boolean suffixOverlapsPrefix(){
		return actual.length() - suffixLength <= prefixLength ||
				expected.length() - suffixLength <= prefixLength;
	}
	
	private char charFromEnd(String s, int i){
		return s.charAt(s.length() - i -1);
	}
	
	String compact(String message){
		return new StringBuilder()
				.append(startingEllipsis())
				.append(startingContext())
				.append(DELTA_START)
				.append(delta(message))
				.append(DELTA_END)
				.append(endingContext())
				.append(endingEllipsis())
				.toString();
	}

	private String endingEllipsis() {
		return (suffixLength > contextLength ? ELLIPSIS : "");
	}

	private String endingContext() {
		int contextStart = expected.length() - suffixLength;
		int contextEnd = Math.min(contextStart + contextLength, expected.length());
		return expected.substring(contextStart, contextEnd);
	}

	private String delta(String message) {
		int deltaStart = prefixLength;
		int deltaEnd = message.length() - suffixLength;
		return message.substring(deltaStart, deltaEnd);
	}

	private String startingEllipsis() {
		return prefixLength > contextLength ? ELLIPSIS : "";
	}
	
	private String startingContext(){
		int contextStart = Math.max(0, prefixLength - contextLength);
		int contextEnd = prefixLength;
		return expected.substring(contextStart, contextEnd);
	}
	
	
	
	
}
