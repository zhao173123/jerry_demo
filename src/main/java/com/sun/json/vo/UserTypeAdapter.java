package com.sun.json.vo;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * TypeAdapter:用于接管某种类型的序列化和反序列化过程
 * 包含主要的2个方法：write(JsonWriter, T)和read(JsonReader)
 * 
 * TypeAdapter以及JsonSerializer和JsonDeseralizer都需要和GsonBuilder.registerTypeAdapter
 * 或GsonBuilder.registerTypeHierarchyAdapter配合使用
 * 
 * 接管序列化和反序列化的过程简单点可以直接使用JsonSerializer和JsonDeserializer
 * 
 * @author jerry
 *
 */
public class UserTypeAdapter extends TypeAdapter<User> {

	@Override
	public User read(JsonReader in) throws IOException {
		User user = new User();
		in.beginObject();
		while(in.hasNext()){
			switch(in.nextName()){
			case "name":
				user.setName(in.nextString());
				break;
			case "age":
				user.setAge(in.nextInt());
				break;
			case "email":
			case "emailAddress":
			case "email_address":
				user.setEmailAddress(in.nextString());
				break;
			}
		}
		in.endObject();
		return user;
	}

	@Override
	public void write(JsonWriter out, User user) throws IOException {
		out.beginObject()
		.name("name").value(user.getName())
		.name("age").value(user.getAge())
		.name("email").value(user.getEmailAddress())
		.endObject();
	}

}
