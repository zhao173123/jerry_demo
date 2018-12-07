package book.cleancode;

import org.junit.Assert;
import junit.framework.TestCase;

public class ArgsTest extends TestCase{
	
	public void testCreateWithNoSchemaOrArgument() throws Exception{
		Args args = new Args("", new String[0]);
		Assert.assertTrue(0 == args.cardinality());
	}
	
	public void testWithNoSchemaButWithOneArgument() throws Exception{
		try{
			new Args("", new String[]{"-x"});
			Assert.fail();
		}catch(ArgsException e){
			Assert.assertEquals(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, e.getErrorCode());
			Assert.assertEquals('x', e.getErrorArgumentId());
		}
	}
	
	public void testWithNoSchemaButWithMultipleArguments(){
		try{
			new Args("", new String[]{"-x", "-y"});
			Assert.fail();
		}catch(ArgsException e){
			Assert.assertEquals(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, e.getErrorCode());
			Assert.assertEquals('x', e.getErrorArgumentId());
		}
	}
	
	public void testNonLetterSchema(){
		try{
			new Args("*", new String[]{});
			Assert.fail();
		}catch(ArgsException e){
			Assert.assertEquals(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME, e.getErrorCode());
			Assert.assertEquals('*', e.getErrorArgumentId());
		}
	}
	
	public void testInvalidArgumentFormat(){
		try{
			new Args("f~", new String[]{});
			Assert.fail();
		}catch(ArgsException e){
			Assert.assertEquals(ArgsException.ErrorCode.INVALID_ARGUMENT_FORMAT, e.getErrorCode());
			Assert.assertEquals('f', e.getErrorArgumentId());
		}
	}
	
	public void testSimpleBooleanPresent() throws ArgsException{
		Args args = new Args("x", new String[]{"-x"});
		Assert.assertEquals(1, args.cardinality());
		Assert.assertEquals(true, args.getBoolean('x'));
	}
	
	public void testSimpleStringPresent() throws ArgsException{
		Args args = new Args("x*", new String[]{"-x", "param"});
		Assert.assertEquals(1, args.cardinality());
		Assert.assertTrue(args.has('x'));
		Assert.assertEquals("param", args.getString('x'));
	}
	
	public void testMissingStringArgument() throws Exception{
		try{
			new Args("x*", new String[]{"-x"});
			Assert.fail();
		}catch(ArgsException e){
			Assert.assertEquals(ArgsException.ErrorCode.MISSING_STRING, e.getErrorCode());
			Assert.assertEquals('x', e.getErrorArgumentId());
		}
	}
	
	public void testSpacesInFormat() throws Exception{
		Args args = new Args("x, y", new String[]{"-xy"});
		Assert.assertEquals(2, args.cardinality());
		Assert.assertTrue(args.has('x'));
		Assert.assertTrue(args.has('y'));
	}
	
	public void testSimpleIntPresent() throws Exception{
		Args args = new Args("x#", new String[]{"-x", "42"});
		Assert.assertEquals(1, args.cardinality());
		Assert.assertTrue(args.has('x'));
		Assert.assertEquals(42, args.getInt('x'));
	}
	
	public void testInvalidInteger(){
		try{
			new Args("x#", new String[]{"-x", "Forty two"});
			Assert.fail();
		}catch(ArgsException e){
			Assert.assertEquals(ArgsException.ErrorCode.INVALID_INTEGER, e.getErrorCode());
			Assert.assertEquals('x', e.getErrorArgumentId());
			Assert.assertEquals("Forty two", e.getErrorParameter());
		}
	}
	
///后面还有一些测试没有列出，不过已经可以从上述的testcase看出思想
	///重构时需要测试用例的意义
	///预先定义一套测试用例，对需求和代码也是一个整理思考的过程
	///在重构的过程中，不会对代码的原有逻辑产生不良影响
	///TestCase不是为了重构而存在，而是从代码开始阶段就存在，随着逻辑（需求）的变更随时更新或修改（删除）
}
