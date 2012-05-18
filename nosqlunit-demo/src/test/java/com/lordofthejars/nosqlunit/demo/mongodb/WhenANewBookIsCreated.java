package com.lordofthejars.nosqlunit.demo.mongodb;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;

import org.junit.Rule;
import org.junit.Test;

import com.lordofthejars.nosqlunit.annotation.DataSet;
import com.lordofthejars.nosqlunit.annotation.ExpectedDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.demo.model.Book;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

public class WhenANewBookIsCreated {

	@Rule
	public MongoDbRule remoteMongoDbRule = new MongoDbRule(WhenYouFindAllBooks.class,  mongoDb()
			.databaseName("test").build());
	
	@Test
	@DataSet(locations="initialData.json", loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	@ExpectedDataSet(values="expectedData.json")
	public void book_should_be_inserted_into_repository() {
		
		BookManager bookManager = new BookManager(MongoDbUtil.getCollection(Book.class.getSimpleName()));
		
		Book book = new Book("The Lord Of The Rings", 1299);
		bookManager.create(book);
		
	}
	
}