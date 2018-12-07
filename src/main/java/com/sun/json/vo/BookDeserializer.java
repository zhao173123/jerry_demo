package com.sun.json.vo;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BookDeserializer implements JsonDeserializer<Book> {

	//{'title':'Java Traps:Pit','isbn-10':'012319312','isbn-13':'asdsadn','authors':['Joshua Bloch','Neal Gafter']}
	@Override
	public Book deserialize(final JsonElement json, Type paramType, JsonDeserializationContext context)
			throws JsonParseException {
		final JsonObject jsonObject = json.getAsJsonObject();
		final JsonElement titleElement = jsonObject.get("title");
		final String title = titleElement.getAsString();
		final String isbn10 = jsonObject.get("isbn-10").getAsString();
		final String isbn13 = jsonObject.get("isbn-13").getAsString();
		final JsonArray jsonAuthorsArray = jsonObject.get("authors").getAsJsonArray();
		final String[] authors = new String[jsonAuthorsArray.size()];
		for (int i = 0; i < authors.length; i++) {
			final JsonElement jsonAuthor = jsonAuthorsArray.get(i);
			authors[i] = jsonAuthor.getAsString();
		}
		final Book book = new Book();
		book.setTitle(title);
		book.setIsbn10(isbn10);
		book.setIsbn13(isbn13);
		book.setAuthors(authors);
		return book;
	}

}
