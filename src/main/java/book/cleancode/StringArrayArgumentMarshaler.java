package book.cleancode;

import java.util.Iterator;

public class StringArrayArgumentMarshaler implements ArgumentMarshaler {
	
	private String[] stringValue;
	
	@Override
	public void set(Iterator<String> currentArgument) throws ArgsException {
		String parameter = currentArgument.next();
		
	}
	
	public static String[] getValue(ArgumentMarshaler am){
		if(am != null && am instanceof StringArrayArgumentMarshaler){
			return ((StringArrayArgumentMarshaler)am).stringValue;
		}
		return null;
	}

}
