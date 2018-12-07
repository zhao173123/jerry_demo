package com.sun.json;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.json.vo.Book;
import com.sun.json.vo.BookDeserializer;
import com.sun.json.vo.GenericModel;
import com.sun.json.vo.Person;
import com.sun.json.vo.User;
import com.sun.json.vo.UserDate;
import com.sun.json.vo.UserTypeAdapter;

/**
 * GsonBuilder:
 * 1.setFieldNamingPolicy 设置序列字段的命名策略
 * 2.addDeserializationExclusionStrategy 设置反序列化时字段采用策略
 * 3.excludeFieldsWithoutExposeAnnotation 设置没有@Expore则不序列化和反序列化
 * 4.addSerializationExclusionStrategy 设置序列化时字段采用策略，如序列化时不要某字段，当然可以采用@Expore代替。
 * 5.registerTypeAdapter 为某特定对象设置固定的序列和反序列方式，实现JsonSerializer和JsonDeserializer接口
 * 6.setFieldNamingStrategy 设置字段序列和反序列时名称显示，也可以通过@Serializer代替
 * 7.setPrettyPrinting 设置gson转换后的字符串为一个比较好看的字符串
 * 8.setDateFormat 设置默认Date解析时对应的format格式
 * @author jerry
 *
 */
public class TestGSON {

	@Before
	public void init() {
		gson = new Gson();
	}

	@Test
	public void testCreateJSONObject() {
		Gson gson = new Gson();
		String str = gson.toJson(jsonObj);
		System.out.println(str);
	}

	@Test
	public void testString2JSONObject() {
		// jsonObj
		Person person = gson.fromJson(jsonObj, Person.class);
		System.out.println(person.toString());
		// jsonArr

	}
	
	//fromJson：反序列化，toJson：序列化
	@Test
	public void testGson1(){
		//基本数据类型的解析
		int i = gson.fromJson("100", int.class);//100
		double d = gson.fromJson("\"99.99\"", double.class);//99.99
		boolean b = gson.fromJson("true", boolean.class);//true
		String s = gson.fromJson("String", String.class);//string
		System.out.println("int:" + i + ",double:" + d + ",boolean:" + b + ",String:" + s);
		//基本数据的生成
		String jsonNumber = gson.toJson(10);//10
		String jsonBoolean = gson.toJson(false);//false
		String jsonString = gson.toJson("String");//String
		System.out.println("jsonNumber:" + jsonNumber + ",jsonBoolean:" + jsonBoolean + ",jsonString:" + jsonString);
		//pojo类的生成和解析
		//1.生成json
		User user = new User("jerry", 30);
		String jsonObject = gson.toJson(user);
		System.out.println("jsonObject:" + jsonObject);//jsonObject:{"name":"jerry","age":30}
		//2.解析json
		jsonString = "{\"name\":\"jerry\",\"age\":30}";
		user = gson.fromJson(jsonString, User.class);
		System.out.println(user);
		String json = "{\"name\":\"怪盗kidou\",\"age\":24,\"emailAddress\":\"ikidou_1@example.com\",\"email\":\"ikidou_2@example.com\",\"email_address\":\"ikidou_3@example.com\"}";
		user = gson.fromJson(json, User.class);
		System.out.println(user);//name:怪盗kidou,age:24,email:ikidou_3@example.com
		//json->数组
		String[] strings = gson.fromJson(jsonArr2, String[].class);
		System.out.println(strings);
		//json->list:GSON提供了TypeToken来实现对泛型的支持
		List<String> stringList = gson.fromJson(jsonArr2, new TypeToken<List<String>>(){}.getType());
		stringList.forEach(item->{System.out.println(item);});
		//list->json
		String jsonStr = gson.toJson(stringList, new TypeToken<List<String>>(){}.getType());
		System.out.println("jsonStr:" + jsonStr);//jsonStr:["Android","java","ios"]
	}
	
	/** Gson的流式反序列化
	 * @throws IOException **/
	@Test
	public void deserializeGson() throws IOException{
		String json = "{\"name\":\"怪盗kidou\",\"age\":\"24\"}";
		User user = new User();
		JsonReader reader = new JsonReader(new StringReader(json));
		reader.beginObject();
		while(reader.hasNext()){
			String s = reader.nextName();
			switch(s){
				case "name":
					user.setName(reader.nextString());
					break;
				case "age":
					user.setAge(reader.nextInt());
					break;
				case "email":
					user.setEmailAddress(reader.nextString());
					break;
			}
		}
		reader.endObject();
		System.out.println(user);//name:怪盗kidou,age:24,email:null
		reader.close();
	}
	
	/** 流式序列化
	 * @throws IOException **/
	@Test
	public void serializeGson() throws IOException{
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(System.out));
		writer.beginObject()
				.name("name").value("jerry")
				.name("age").value(30)
				.name("eamil").nullValue()
				.endObject();
		writer.flush();
		writer.close();
		//{"name":"jerry","age":30,"eamil":null}
	}
	
	//Test TypeAdapter
	@Test
	public void testTypeAdapter(){
		User user = new User("jerry", 30);
		user.setEmailAddress("zhao173123@163.com");
		gson = new GsonBuilder()
				.registerTypeAdapter(User.class, new UserTypeAdapter())
				.create();
		System.out.println(gson.toJson(user));//{"name":"jerry","age":30,"email":"zhao173123@163.com"}
	}
	
	//Test TypeAdapter->JsonSerializer
	//将所有数字转换成字符串
	@Test
	public void testJsonSerializer(){
		JsonSerializer<Number> numberJsonSerializer = new JsonSerializer<Number>() {
			@Override
			public JsonElement serialize(Number number, Type type,
					JsonSerializationContext context) {
				return new JsonPrimitive(String.valueOf(number));
			}
		};
		gson = new GsonBuilder()
				.registerTypeAdapter(Integer.class, numberJsonSerializer)
				.registerTypeAdapter(Long.class, numberJsonSerializer)
				.registerTypeAdapter(Float.class, numberJsonSerializer)
				.registerTypeAdapter(Double.class, numberJsonSerializer)
				.create();
		System.out.println(gson.toJson(100.0f));//100.0
	}

	@Test
	public void testJsonDeserializer(){
		gson = new GsonBuilder()
				.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>(){
					@Override
					public Integer deserialize(JsonElement element, Type type,
							JsonDeserializationContext context) throws JsonParseException {
						try{
							return element.getAsInt();							
						}catch(Exception e){
							return -1;
						}
					}
				})
				.create();
		System.out.println(gson.toJson(100));//100
		System.out.println(gson.fromJson("\"\"", Integer.class));//-1
	}
	
	/**
	 * 服务器返回data字段不固定，成功返回list，不成功返回string。导致前端需要特殊处理。
	 * 处理方式有2种
	 */
	@Test
	public void dealwithWebData(){
		//方案一：不推荐
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(List.class, new JsonDeserializer<List<?>>(){
					@Override
					public List<?> deserialize(JsonElement json, Type type,
							JsonDeserializationContext context) throws JsonParseException {
						if(json.isJsonArray()){
							//业务处理
							Gson newGson = new Gson();
							return newGson.fromJson(json, type);
						}else{
							//和接口类型不符，返回空list
							return Collections.EMPTY_LIST;
						}
					}
					
				}).create();
		
		//方案二：推荐
		gson = new GsonBuilder()
				.registerTypeHierarchyAdapter(List.class, new JsonDeserializer<List<?>>(){
					@Override
					public List<?> deserialize(JsonElement json, Type type,
							JsonDeserializationContext context) throws JsonParseException {
						if(json.isJsonArray()){
							JsonArray array = json.getAsJsonArray();
							Type itemType = ((ParameterizedType)type).getActualTypeArguments()[0];
							List<Object> list = Lists.newArrayList();
							for(int i = 0; i < array.size(); i++){
								JsonElement element = array.get(i);
								Object item = context.deserialize(element, itemType);
								list.add(item);
							}
							return list;
						}else{
							return Collections.EMPTY_LIST;
						}
					}
					
				}).create();
	}
	
	@Test
	public void testGson2Book() {
		final GsonBuilder gsonBuilder = new GsonBuilder();

		// gsonBuilder.registerTypeAdapter(Book.class, new BookSerialiser());
		// gsonBuilder.setPrettyPrinting();
		final Gson gson = gsonBuilder.create();
		// final Book puzzlers = new Book();
		// puzzlers.setTitle("Java Puzzlers: Traps, Pitfalls, and Corner
		// Cases");
		// puzzlers.setIsbn10("032133678X");
		// puzzlers.setIsbn13("978-0321336781");
		// puzzlers.setAuthors(new String[] { "Joshua Bloch", "Neal Gafter" });
		// final String json = gson.toJson(puzzlers);
		// System.out.println(json);

		gsonBuilder.registerTypeAdapter(Book.class, new BookDeserializer());
		gsonBuilder.setPrettyPrinting();
		try {
			Reader reader = new InputStreamReader(TestGSON.class.getResourceAsStream("sample.json"), "UTF-8");
			Book book = gson.fromJson(reader, Book.class);
			System.out.println(book);

		} catch (Exception e) {

		} finally {

		}
	}
	
	//Gson.ExclusionStrategy
	//对json字符串进行过滤和检查（字段类型过滤）
	@Test
	public void testExclusionStragety(){
		GsonBuilder gsonBuilder = new GsonBuilder();
//		gsonBuilder.setPrettyPrinting();
		gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
			//过滤字段，包含"_"的将自动过滤掉
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return f.getName().contains("_");
			}
			//过滤类
			@Override
			public boolean shouldSkipClass(Class<?> clz) {
				return clz == Date.class || clz == boolean.class;
			}
		});
		Gson gson = gsonBuilder.create();
		UserDate user = new UserDate("Normal", "normal@mainiway.com", 26, true);
		String userJson = gson.toJson(user);
		//age+email
		System.out.println(userJson);//{"email":"normal@mainiway.com","age":26}
	}
	
	//数组的解析
	@Test
	public void testJsonArray(){
		Type listType = new TypeToken<List<String>>(){}.getType();
		List<String> target = Lists.newLinkedList();
		target.add("jerry");
		String json = gson.toJson(target, listType);//List<String>->String
		System.out.println("json:" + json);//json:["jerry"]
		List<String> target2 = gson.fromJson(json, listType);//String->List<String>
		target2.forEach(item->System.out.println(item));//jerry
	}
	
	//test.GenericModel
	//泛型的pojo-json
	@Test
	public void testGenericModel(){
		Gson gson = new Gson();
		GenericModel<Integer> model = new GenericModel<Integer>(12);
		String json = gson.toJson(model);
		System.out.println("json representation:" + json);//{"value":12}
		
		Type type = new TypeToken<GenericModel<Integer>>(){}.getType();
		GenericModel<Integer> modelObj = gson.fromJson(json, type);
		System.out.println("converted object representation:" + modelObj);//model[value=12]
		
		List<String> listOfString = Lists.newArrayList();
		listOfString.add("vip");
		listOfString.add("abc");
		String jsonStr = gson.toJson(listOfString);
		System.out.println("json representation:" + jsonStr);//["vip","abc"]
		
		Type type2 = new TypeToken<List<String>>(){}.getType();
		List<String> listObj = gson.fromJson(jsonStr, type2);
		System.out.println("converted object representation:" + listObj);//[vip, abc]
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		// Gson gson = new GsonBuilder().create();
		// gson.toJson("hello", System.out);
		// gson.toJson(123, System.out);
		// try (Writer writer = new OutputStreamWriter(new
		// FileOutputStream("WebContent/WEB-INF/Output.json"), "UTF-8")) {
		// gson.toJson("hello", writer);
		// gson.toJson(123, writer);
		// }
		TestGSON test = new TestGSON();
//		test.testGson2Book();
	}

	private static Gson gson = null;
	private static final String jsonArr = "[{'name':'jerry','age':18},{'name':'tom','age':2}]";
	private static final String jsonObj = "{'name':'jerry','age':18}";
	private static final String jsonArr1 = "{'title':'Java Traps:Pit','isbn-10':'012319312','isbn-13':'asdsadn','authors':['Joshua Bloch','Neal Gafter']}";
	private static final String jsonArr2 = "[\"Android\",\"java\",\"ios\"]";
}
