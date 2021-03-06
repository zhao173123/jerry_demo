package book.cleancode.junit;

import junit.framework.ComparisonCompactor;
import junit.framework.TestCase;

/**
 * test case of junit framework class @link<junit.framework.ComparisonCompactor>
 * 
 * @author jerry
 *
 */
public class ComparisonCompactorTest extends TestCase {

	public void testMessage(){
//		String failure = new ComparisonCompactor(0, "b", "c").compact("a");
		String failure = new MyComparisonCompactor(0, "b", "c").formatCompactedComparison("a");
		assertTrue("a expected:<[b]> but was:<[c]>".equals(failure));
	}
	
	public void testStartSame(){
//		String failure = new ComparisonCompactor(1, "ba", "bc").compact(null);
		String failure = new MyComparisonCompactor(1, "ba", "bc").formatCompactedComparison(null);
		assertEquals("expected:<b[a]> but was:<b[c]>", failure);
	}
	
	public void testEndSame(){
		String failure = new ComparisonCompactor(1, "ab", "cb").compact(null);
		assertEquals("expected:<[a]b> but was:<[c]b>", failure);
	}
	
	public void testSame(){
		String failure = new ComparisonCompactor(0, "ab", "ab").compact(null);
		assertEquals("expected:<ab> but was:<ab>", failure);
	}
	
	public void testNonContextStartAndEndSame(){
		String failure = new ComparisonCompactor(0, "abc", "adc").compact(null);
		assertEquals("expected:<...[b]...> but was:<...[d]...>", failure);
	}
	
	public void testStartAndEndContext(){
		String failure = new ComparisonCompactor(1, "abc", "adc").compact(null);
		assertEquals("expected:<a[b]c> but was:<a[d]c>", failure);
	}
	
	public void testStartAndEndContextWithEllipses(){
		String failure = new ComparisonCompactor(1, "abcde", "abfde").compact(null);
		assertEquals("expected:<...b[c]d...> but was:<...b[f]d...>", failure);
	}
	
	public void testComparisonErrorStartSameComplete(){
		String failure = new ComparisonCompactor(2, "ab", "abc").compact(null);
		assertEquals("expected:<ab[]> but was:<ab[c]>", failure);
	}
	
	public void testComparisonErrorEndSameComplete(){
		String failure = new ComparisonCompactor(0, "bc", "abc").compact(null);
		assertEquals("expected:<[]...> but was:<[a]...>", failure);
	}
	
	public void testComparisonErrorEndSameCompleteContext(){
		String failure = new ComparisonCompactor(2, "bc", "abc").compact(null);
		assertEquals("expected:<[]bc> but was:<[a]bc>", failure);
	}
	
	public void testComaprsionErrorOverlapingMatches(){
		String failure = new ComparisonCompactor(0, "abc", "abbc").compact(null);
		assertEquals("expected:<...[]...> but was:<...[b]...>", failure);
	}
	
	public void testComparisonErrorOverlapingMatchersContext(){
		String failure = new ComparisonCompactor(2, "abc", "abbc").compact(null);
		assertEquals("expected:<ab[]c> but was:<ab[b]c>", failure);
	}
	
	public void testComparisonErrorOverlapingMatchers2(){
		String failure = new ComparisonCompactor(0, "abcddde", "abcde").compact(null);
		assertEquals("expected:<...[dd]...> but was:<...[]...>", failure);
	}
	
	public void testComparisonErrorOverlapingMatchers2Context(){
		String failure = new ComparisonCompactor(2, "abcdde", "abcde").compact(null);
		assertEquals("expected:<...cd[d]e> but was:<...cd[]e>", failure);
	}
	
	public void testComparisonErrorWithActualNull(){
		String failure = new ComparisonCompactor(0, "a", null).compact(null);
		assertEquals("expected:<a> but was:<null>", failure);
	}
	
	public void testComparisonErrorWithActualNullContext(){
		String failure = new ComparisonCompactor(2, "a", null).compact(null);
		assertEquals("expected:<a> but was:<null>", failure);
	}
	
	public void testComparisonErrorWithExpectedNull(){
		String failure = new ComparisonCompactor(0, null, "a").compact(null);
		assertEquals("expected:<null> but was:<a>", failure);
	}
	
	public void testComparisonErrorWithExpectedNullContext(){
		String failure = new ComparisonCompactor(2, null, "a").compact(null);
		assertEquals("expected:<null> but was:<a>", failure);
	}
	
	public void testBug609972(){
//		String failure = new ComparisonCompactor(10, "S&P500", "0").compact(null);
		String failure = new MyComparisonCompactor(10, "S&P500", "0").formatCompactedComparison(null);
		assertEquals("expected:<[S&P50]0> but was:<[]0>", failure);
	}
}
