package com.sun.json.vo;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 实现序列化器，可以得到约定的json展示
 * 
 * @author jerry
 *
 */
public class BookSerialiser implements JsonSerializer<Book> {

	@Override
	public JsonElement serialize(final Book book, final Type paramType,
			final JsonSerializationContext context) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("title", book.getTitle());
		jsonObject.addProperty("isbn-10", book.getIsbn10());
		jsonObject.addProperty("isbn-13", book.getIsbn13());
		
		final JsonArray jsonAuthorsArray = new JsonArray();
		for(final String author : book.getAuthors()){
			final JsonPrimitive jsonAuthor = new JsonPrimitive(author);
			jsonAuthorsArray.add(jsonAuthor);
		}
		jsonObject.add("authors", jsonAuthorsArray);
		return jsonObject;
	}

}
