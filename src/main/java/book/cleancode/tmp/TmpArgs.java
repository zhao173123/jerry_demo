package book.cleancode.tmp;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 需求：解析命令行参数
 * 1.第一版只支持boolean类型
 * 2.加入对String的支持：注意观察代码是如何变烂掉
 * 
 * @author jerry
 *
 */
public class TmpArgs {
	
	private String schema;
	private String[] args;
	private boolean valid;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Map<Character, Boolean> booleanArgs = new HashMap<Character, Boolean>();
	private int numberOfArguments = 0;
	
	///️2⃣️ add parameter begin
	private Map<Character, String> stringArgs = new HashMap<Character, String>();
	private Set<Character> argsFound = new HashSet<Character>();
	private int currentArgument;
	private char errorArgument = '\0';
	enum ErrorCode{
		OK, MISSING_STRING
	}
	private ErrorCode errorCode = ErrorCode.OK;
	//2⃣️ end 
	
	public TmpArgs(String schema, String[] args) throws ParseException{
		this.schema = schema;
		this.args = args;
		valid = parse();
	}

	private boolean parse() throws ParseException {
		if(schema.length() == 0 && args.length == 0){
			return true;
		}
		parseSchema();
		parseArguments();
//		return unexpectedArguments.size() == 0;
		return valid;//2⃣️
	}
	
	private boolean parseSchema() throws ParseException {
		for(String element : schema.split(",")){
//			parseSchemaElement(element);//1
			//2
			if(element.length() > 0){
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
		}
		return true;
	}

	private void parseSchemaElement(String element) throws ParseException {
		//1
//		if(element.length() == 1){
//			parseBooleanSchemaElement(element);
//		}
		//2
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if(isBooleanSchemaElement(elementTail)){
			parseBooleanSchemaElement(elementId);
		}else if(isStringSchemaElement(elementTail)){
			parseStringSchemaElement(elementId);
		}
	}

	//2
	private void validateSchemaElementId(char elementId) throws ParseException {
		if(!Character.isLetter(elementId)){
			throw new ParseException("Bad character : " + elementId + " in age format : " + schema, 0);
		}
	}

	private boolean isBooleanSchemaElement(String elementTail) {
		return elementTail.length() == 0;
	}

	private void parseBooleanSchemaElement(char elementId) {
		booleanArgs.put(elementId, false);
	}

	private boolean isStringSchemaElement(String elementTail) {
		return elementTail.equals("*");
	}

	private void parseStringSchemaElement(char elementId) {
		stringArgs.put(elementId, "");
	}
	
	//1
//	private void parseBooleanSchemaElement(String element) {
//		char c = element.charAt(0);
//		if(Character.isLetter(c)){
//			booleanArgs.put(c, false);
//		}
//	}
	
	private boolean parseArguments() {
		//1
//		for(String arg : args){
//			parseArgument(arg);
//		}
		//2
		for(currentArgument = 0; currentArgument < args.length; currentArgument++){
			String arg = args[currentArgument];
			parseArgument(arg);
		}
		return true;
	}

	private void parseArgument(String arg) {
		if(arg.startsWith("-")){
			parseElements(arg);
		}
	}

	private void parseElements(String arg) {
		for(int i = 1; i < arg.length(); i++){
			parseElement(arg.charAt(i));
		}
	}

	private void parseElement(char argChar) {
//		if(isBoolean(argChar)){
//			numberOfArguments++;
//			setBooleanArg(argChar, true);
		if(setArgument(argChar)){
			argsFound.add(argChar);
		}else{
			unexpectedArguments.add(argChar);
			valid = false;
		}
	}

	private boolean setArgument(char argChar) {
		boolean set = true;
		if(isBoolean(argChar)){
			setBooleanArg(argChar, true);
		}else if(isString(argChar)){
			setStringArg(argChar, "");
		}else{
			set = false;
		}
		return set;
	}

	private boolean isBoolean(char argChar) {
		return booleanArgs.containsKey(argChar);
	}

	private void setBooleanArg(char argChar, boolean value) {
		booleanArgs.put(argChar, value);
	}
	
	private void setStringArg(char argChar, String string) {
		currentArgument++;
		try{
			stringArgs.put(argChar, args[currentArgument]);
		}catch(ArrayIndexOutOfBoundsException e){
			valid = false;
			errorArgument = argChar;
			errorCode = ErrorCode.MISSING_STRING;
		}
	}

	private boolean isString(char argChar) {
		return stringArgs.containsKey(argChar);
	}
	
	public int cardinality(){
		return numberOfArguments;
	}
	
	public String usage(){
		if(schema.length() > 0){
			return "-[" + schema + "]"; 
		}else
			return "";
	}
	
	public String errorMessage() throws Exception{
		if(unexpectedArguments.size() > 0){
			return unexpectedArgumentMessage();
		}else{
			switch(errorCode){
				case MISSING_STRING:
					return String.format("Could not find string parameter for -%c.", errorArgument);
				case OK:
					throw new Exception("TILT: Should not get here.");
			}
			return "";
		}
	}

	private String unexpectedArgumentMessage() {
		StringBuffer sb = new StringBuffer("Argument(s) -");
		for(char c : unexpectedArguments){
			sb.append(c);
		}
		sb.append(" unexpected.");
		return sb.toString();
	}
	
	public boolean isValid(){
		return this.valid;
	}
	
	public boolean getBoolean(char arg){
//		return booleanArgs.get(arg);
		return falseIfNull(booleanArgs.get(arg));
	}

	private boolean falseIfNull(Boolean b) {
		return b == null ? false : true;
	}
	
	public String getString(char arg){
		return blankIfNull(stringArgs.get(arg));
	}

	private String blankIfNull(String s) {
		return s == null ? "" : s;
	}
	
	public boolean has(char arg){
		return argsFound.contains(arg);
	}
}
