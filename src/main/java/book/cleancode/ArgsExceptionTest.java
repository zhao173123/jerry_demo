package book.cleancode;

import org.junit.Assert;

import junit.framework.TestCase;

public class ArgsExceptionTest extends TestCase {
	
	public void testUnexceptedMessage(){
		ArgsException e = new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, 'x', null);
		Assert.assertEquals("Argument -x unexpected.", e.errorMessage());
	}
	
	public void testMissingStringMessage(){
		ArgsException e = new ArgsException(ArgsException.ErrorCode.MISSING_STRING, 'x', null);
		Assert.assertEquals("Could not find string parameter for -x.", e.errorMessage());
	}
	
	public void testInvalidIntegerMessage(){
		ArgsException e = new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER, 'x', "Forty two");
		Assert.assertEquals("Argument -x expects an integer but was 'Forty two'.", e.errorMessage());
	}
	
	///....
	
	public void testMissingDoubleMessage(){
		ArgsException e = new ArgsException(ArgsException.ErrorCode.MISSING_DOUBLE, 'x', null);
		Assert.assertEquals("Could not find double parameter for -x.", e.errorMessage());
	}
}
